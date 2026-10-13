package com.example.data.api

import com.example.BuildConfig
import com.example.data.local.UserPreferences
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.ConnectionPool
import okhttp3.Credentials
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

/**
 * RetrofitClient for WooCommerce REST API integration.
 * Manages Retrofit instance creation, HTTP client pooling, logging,
 * and OAuth / Basic Authentication headers with placeholders for API keys.
 */
object RetrofitClient {

    // =========================================================================
    // WOOCOMMERCE API CONFIGURATION & PLACEHOLDERS
    // =========================================================================
    /**
     * Default WooCommerce Store Base URL
     */
    const val BASE_URL: String = "https://ustore4u.com/"

    /**
     * Placeholder for WooCommerce REST API Consumer Key (ck_...).
     * In production, configure this via Secrets / BuildConfig or UserPreferences.
     */
    const val DEFAULT_CONSUMER_KEY: String = "ck_your_woocommerce_consumer_key_here"

    /**
     * Placeholder for WooCommerce REST API Consumer Secret (cs_...).
     * In production, configure this via Secrets / BuildConfig or UserPreferences.
     */
    const val DEFAULT_CONSUMER_SECRET: String = "cs_your_woocommerce_consumer_secret_here"

    // =========================================================================
    // JSON & HTTP CLIENT CONFIGURATION
    // =========================================================================
    private val moshi: Moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val connectionPool = ConnectionPool(8, 5, TimeUnit.MINUTES)

    @Volatile
    private var cachedRetrofit: Retrofit? = null

    @Volatile
    private var cachedApiService: WooCommerceApiService? = null

    @Volatile
    private var lastConfigSignature: String = ""

    /**
     * Builds or returns the cached OkHttpClient with OAuth/Basic Auth interceptors
     */
    fun createOkHttpClient(
        consumerKey: String = DEFAULT_CONSUMER_KEY,
        consumerSecret: String = DEFAULT_CONSUMER_SECRET,
        bearerToken: String? = null
    ): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }

        val authInterceptor = Interceptor { chain ->
            val originalRequest = chain.request()
            val requestBuilder = originalRequest.newBuilder()
                .header("Accept", "application/json")
                .header("User-Agent", "UStore4U-Android-App/1.0")

            if (!bearerToken.isNullOrBlank()) {
                requestBuilder.header("Authorization", "Bearer $bearerToken")
            } else if (consumerKey.isNotBlank() && consumerSecret.isNotBlank() &&
                !consumerKey.startsWith("ck_your_") && !consumerSecret.startsWith("cs_your_")
            ) {
                // Standard WooCommerce REST API Basic Auth with Consumer Key & Secret
                val credential = Credentials.basic(consumerKey.trim(), consumerSecret.trim())
                requestBuilder.header("Authorization", credential)
            }

            chain.proceed(requestBuilder.build())
        }

        return OkHttpClient.Builder()
            .connectionPool(connectionPool)
            .addInterceptor(loggingInterceptor)
            .addInterceptor(authInterceptor)
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .build()
    }

    /**
     * Creates a Retrofit instance configured for the specified Base URL and credentials
     */
    fun createRetrofit(
        baseUrl: String = BASE_URL,
        consumerKey: String = DEFAULT_CONSUMER_KEY,
        consumerSecret: String = DEFAULT_CONSUMER_SECRET,
        bearerToken: String? = null
    ): Retrofit {
        val client = createOkHttpClient(consumerKey, consumerSecret, bearerToken)
        val normalizedBaseUrl = if (baseUrl.endsWith("/")) baseUrl else "$baseUrl/"

        return Retrofit.Builder()
            .baseUrl(normalizedBaseUrl)
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }

    /**
     * Returns a thread-safe singleton instance of WooCommerceApiService
     */
    @Synchronized
    fun getApiService(
        userPreferences: UserPreferences? = null
    ): WooCommerceApiService {
        val baseUrl = userPreferences?.getCustomApiBaseUrl()?.ifBlank { BASE_URL } ?: BASE_URL
        val consumerKey = userPreferences?.getCustomConsumerKey()?.ifBlank { DEFAULT_CONSUMER_KEY } ?: DEFAULT_CONSUMER_KEY
        val consumerSecret = userPreferences?.getCustomConsumerSecret()?.ifBlank { DEFAULT_CONSUMER_SECRET } ?: DEFAULT_CONSUMER_SECRET
        val authToken = userPreferences?.getAuthToken()

        val currentSignature = "$baseUrl|$consumerKey|$consumerSecret|$authToken"
        val cached = cachedApiService
        if (cached != null && currentSignature == lastConfigSignature) {
            return cached
        }

        val retrofit = createRetrofit(
            baseUrl = baseUrl,
            consumerKey = consumerKey,
            consumerSecret = consumerSecret,
            bearerToken = authToken
        )

        val service = retrofit.create(WooCommerceApiService::class.java)
        cachedRetrofit = retrofit
        cachedApiService = service
        lastConfigSignature = currentSignature

        return service
    }
}
