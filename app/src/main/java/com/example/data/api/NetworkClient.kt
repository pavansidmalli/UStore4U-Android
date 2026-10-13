package com.example.data.api

import android.util.Base64
import com.example.data.local.UserPreferences
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.ConnectionPool
import okhttp3.ConnectionSpec
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.TlsVersion
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

class NetworkClient(private val userPreferences: UserPreferences) {

    private val moshi: Moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val connectionPool = ConnectionPool(8, 5, TimeUnit.MINUTES)

    // Strict TLS Specifications for secure HTTPS communication
    private val modernTlsSpec = ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
        .tlsVersions(TlsVersion.TLS_1_3, TlsVersion.TLS_1_2)
        .supportsTlsExtensions(true)
        .build()

    @Volatile
    private var cachedOkHttpClient: OkHttpClient? = null

    @Volatile
    private var cachedApiService: WooCommerceApiService? = null

    @Volatile
    private var lastConfigSignature: String = ""

    private fun getConfigSignature(): String {
        return "${userPreferences.getCustomApiBaseUrl()}|${userPreferences.getCustomConsumerKey()}|${userPreferences.getCustomConsumerSecret()}|${userPreferences.getAuthToken()}"
    }

    private fun createOkHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }

        val authInterceptor = Interceptor { chain ->
            val original = chain.request()
            val requestBuilder = original.newBuilder()
                .header("User-Agent", "UStore4U-Android-App/1.0")
                .header("Accept", "application/json")
                .header("X-Content-Type-Options", "nosniff")
                .header("X-Frame-Options", "DENY")

            val consumerKey = userPreferences.getCustomConsumerKey()
            val consumerSecret = userPreferences.getCustomConsumerSecret()

            if (consumerKey.isNotBlank() && consumerSecret.isNotBlank()) {
                val credentials = "$consumerKey:$consumerSecret"
                val basicAuth = "Basic " + Base64.encodeToString(credentials.toByteArray(), Base64.NO_WRAP)
                requestBuilder.header("Authorization", basicAuth)
            } else {
                val token = userPreferences.getAuthToken()
                if (!token.isNullOrBlank()) {
                    requestBuilder.header("Authorization", "Bearer $token")
                }
            }

            chain.proceed(requestBuilder.build())
        }

        return OkHttpClient.Builder()
            .connectionSpecs(listOf(modernTlsSpec, ConnectionSpec.RESTRICTED_TLS))
            .connectionPool(connectionPool)
            .addInterceptor(loggingInterceptor)
            .addInterceptor(authInterceptor)
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .build()
    }

    @Synchronized
    fun getApiService(): WooCommerceApiService {
        val currentSignature = getConfigSignature()
        val cached = cachedApiService
        if (cached != null && currentSignature == lastConfigSignature) {
            return cached
        }

        var baseUrl = userPreferences.getCustomApiBaseUrl().trim().ifBlank { WooCommerceConfig.DEFAULT_STORE_URL }
        if (!baseUrl.endsWith("/")) {
            baseUrl += "/"
        }
        val okHttpClient = createOkHttpClient()
        cachedOkHttpClient = okHttpClient
        lastConfigSignature = currentSignature

        val newService = try {
            Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(okHttpClient)
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .build()
                .create(WooCommerceApiService::class.java)
        } catch (e: Exception) {
            Retrofit.Builder()
                .baseUrl(WooCommerceConfig.DEFAULT_STORE_URL)
                .client(okHttpClient)
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .build()
                .create(WooCommerceApiService::class.java)
        }

        cachedApiService = newService
        return newService
    }
}

