package com.example

import android.app.Application
import androidx.room.Room
import coil.Coil
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.disk.DiskCache
import coil.memory.MemoryCache
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.example.data.api.NetworkClient
import com.example.data.local.AppDatabase
import com.example.data.local.UserPreferences
import com.example.data.repository.CartRepository
import com.example.data.repository.OrderRepository
import com.example.data.repository.ProductRepository
import com.example.data.repository.UserRepository
import com.example.data.repository.UStore4UWatchData
import com.example.data.repository.WishlistRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import okhttp3.Cache
import okhttp3.ConnectionPool
import okhttp3.Dispatcher
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

class UStore4UApp : Application(), ImageLoaderFactory {

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    lateinit var database: AppDatabase
        private set

    lateinit var userPreferences: UserPreferences
        private set

    lateinit var networkClient: NetworkClient
        private set

    lateinit var productRepository: ProductRepository
        private set

    lateinit var cartRepository: CartRepository
        private set

    lateinit var wishlistRepository: WishlistRepository
        private set

    lateinit var orderRepository: OrderRepository
        private set

    lateinit var userRepository: UserRepository
        private set

    lateinit var imageLoaderInstance: ImageLoader
        private set

    override fun onCreate() {
        super.onCreate()
        instance = this

        userPreferences = UserPreferences(this)
        networkClient = NetworkClient(userPreferences)

        database = Room.databaseBuilder(
            this,
            AppDatabase::class.java,
            "ustore4u_luxury_watches.db"
        ).fallbackToDestructiveMigration().build()

        productRepository = ProductRepository(
            networkClient = networkClient,
            recentlyViewedDao = database.recentlyViewedDao(),
            userPreferences = userPreferences
        )

        cartRepository = CartRepository(
            cartDao = database.cartDao(),
            wishlistDao = database.wishlistDao()
        )

        wishlistRepository = WishlistRepository(
            wishlistDao = database.wishlistDao(),
            cartDao = database.cartDao()
        )

        orderRepository = OrderRepository(
            networkClient = networkClient,
            userPreferences = userPreferences
        )

        userRepository = UserRepository(
            networkClient = networkClient,
            userPreferences = userPreferences
        )

        // Initialize and set global singleton ImageLoader
        imageLoaderInstance = newImageLoader()
        Coil.setImageLoader(imageLoaderInstance)

        // Launch background pre-fetching for instant, stutter-free high-res watch rendering
        preloadCatalogImagesInBackground()
    }

    override fun newImageLoader(): ImageLoader {
        val okHttpClient = OkHttpClient.Builder()
            .cache(Cache(cacheDir.resolve("luxury_okhttp_image_cache"), 200L * 1024 * 1024))
            .connectionPool(ConnectionPool(12, 5, TimeUnit.MINUTES))
            .dispatcher(Dispatcher().apply {
                maxRequests = 64
                maxRequestsPerHost = 16
            })
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .build()

        return ImageLoader.Builder(this)
            .okHttpClient(okHttpClient)
            .memoryCache {
                MemoryCache.Builder(this)
                    // Allocate 35% of available RAM for instant in-memory retrieval during fast flings
                    .maxSizePercent(0.35)
                    .strongReferencesEnabled(true)
                    .build()
            }
            .diskCache {
                DiskCache.Builder()
                    .directory(cacheDir.resolve("luxury_product_images_v2"))
                    // 500 MB persistent disk cache for high-res macro shots, carousel lookbooks & dials
                    .maxSizeBytes(500L * 1024 * 1024)
                    .build()
            }
            .memoryCachePolicy(CachePolicy.ENABLED)
            .diskCachePolicy(CachePolicy.ENABLED)
            .networkCachePolicy(CachePolicy.ENABLED)
            .respectCacheHeaders(false) // Cache high-res imagery aggressively regardless of server cache headers
            .allowHardware(true) // Direct GPU hardware bitmaps for stutter-free 60/120fps Compose scrolling
            .allowRgb565(false) // Preserve maximum color depth and sharp fidelity for watch dials & sapphire glass
            .crossfade(true)
            .crossfade(150)
            .build()
    }

    /**
     * Pre-fetches a collection of image URLs into disk and memory caches.
     */
    fun prefetchProductImages(urls: Collection<String>) {
        if (urls.isEmpty()) return
        applicationScope.launch {
            urls.filter { it.isNotBlank() && it.startsWith("http") }.distinct().forEach { url ->
                val request = ImageRequest.Builder(this@UStore4UApp)
                    .data(url)
                    .memoryCachePolicy(CachePolicy.ENABLED)
                    .diskCachePolicy(CachePolicy.ENABLED)
                    .networkCachePolicy(CachePolicy.ENABLED)
                    .build()
                imageLoaderInstance.enqueue(request)
            }
        }
    }

    /**
     * Pre-loads the core catalog, banner heroes, and category visuals in the background.
     */
    private fun preloadCatalogImagesInBackground() {
        applicationScope.launch {
            val keyUrls = mutableListOf<String>()

            // 1. Splash & Onboarding visuals
            keyUrls.add("https://images.unsplash.com/photo-1522335789203-aabd1fc54bc9?w=1000&auto=format&fit=crop&q=80")
            keyUrls.add("https://images.unsplash.com/photo-1547996160-71dfabbce5ed?w=1000&auto=format&fit=crop&q=80")
            keyUrls.add("https://images.unsplash.com/photo-1508685096489-7aacd43bd3b1?w=1000&auto=format&fit=crop&q=80")

            // 2. Category thumbnails
            UStore4UWatchData.categories.forEach { cat ->
                cat.image?.src?.let { keyUrls.add(it) }
            }

            // 3. Featured watch photography & galleries
            UStore4UWatchData.products.forEach { product ->
                product.images.forEach { img ->
                    if (img.src.isNotBlank()) {
                        keyUrls.add(img.src)
                    }
                }
            }

            // 4. Lookbook heroes
            keyUrls.add("https://images.unsplash.com/photo-1508057198894-247b23fe5ade?w=400&auto=format&fit=crop&q=80")
            keyUrls.add("https://images.unsplash.com/photo-1563861826100-9cb868fdbe1c?w=400&auto=format&fit=crop&q=80")
            keyUrls.add("https://images.unsplash.com/photo-1524805444758-089113d48a6d?w=600&auto=format&fit=crop&q=80")

            prefetchProductImages(keyUrls)
        }
    }

    companion object {
        lateinit var instance: UStore4UApp
            private set
    }
}

