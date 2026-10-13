package com.example.data.repository

import com.example.data.api.NetworkClient
import com.example.data.local.RecentlyViewedDao
import com.example.data.local.RecentlyViewedEntity
import com.example.data.local.UserPreferences
import com.example.data.model.Coupon
import com.example.data.model.Product
import com.example.data.model.ProductCategory
import com.example.data.model.ProductReview
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class ProductRepository(
    private val networkClient: NetworkClient,
    private val recentlyViewedDao: RecentlyViewedDao,
    private val userPreferences: UserPreferences
) {

    suspend fun getCategories(): Result<List<ProductCategory>> = withContext(Dispatchers.IO) {
        if (userPreferences.isLiveApiActive()) {
            try {
                val response = networkClient.getApiService().getCategories(page = 1, perPage = 50)
                if (response.isSuccessful && !response.body().isNullOrEmpty()) {
                    return@withContext Result.success(response.body()!!)
                }
            } catch (e: Exception) {
                // Fallback to local curated categories on network failure
            }
        }
        Result.success(UStore4UWatchData.categories)
    }

    suspend fun getFeaturedProducts(): Result<List<Product>> = withContext(Dispatchers.IO) {
        if (userPreferences.isLiveApiActive()) {
            try {
                val response = networkClient.getApiService().getProducts(featured = true, perPage = 10)
                if (response.isSuccessful && !response.body().isNullOrEmpty()) {
                    return@withContext Result.success(response.body()!!)
                }
            } catch (e: Exception) {
                // Fallback
            }
        }
        val featured = UStore4UWatchData.products.filter { it.featured }
        Result.success(featured.ifEmpty { UStore4UWatchData.products.take(4) })
    }

    suspend fun getNewArrivals(): Result<List<Product>> = withContext(Dispatchers.IO) {
        if (userPreferences.isLiveApiActive()) {
            try {
                val response = networkClient.getApiService().getProducts(orderBy = "date", order = "desc", perPage = 8)
                if (response.isSuccessful && !response.body().isNullOrEmpty()) {
                    return@withContext Result.success(response.body()!!)
                }
            } catch (e: Exception) {
                // Fallback
            }
        }
        Result.success(UStore4UWatchData.products.sortedByDescending { it.id }.take(6))
    }

    suspend fun getBestSellers(): Result<List<Product>> = withContext(Dispatchers.IO) {
        if (userPreferences.isLiveApiActive()) {
            try {
                val response = networkClient.getApiService().getProducts(orderBy = "popularity", perPage = 8)
                if (response.isSuccessful && !response.body().isNullOrEmpty()) {
                    return@withContext Result.success(response.body()!!)
                }
            } catch (e: Exception) {
                // Fallback
            }
        }
        Result.success(UStore4UWatchData.products.sortedByDescending { it.ratingCount }.take(6))
    }

    suspend fun getSpecialOffers(): Result<List<Product>> = withContext(Dispatchers.IO) {
        if (userPreferences.isLiveApiActive()) {
            try {
                val response = networkClient.getApiService().getProducts(onSale = true, perPage = 8)
                if (response.isSuccessful && !response.body().isNullOrEmpty()) {
                    return@withContext Result.success(response.body()!!)
                }
            } catch (e: Exception) {
                // Fallback
            }
        }
        Result.success(UStore4UWatchData.products.filter { it.onSale })
    }

    suspend fun getProducts(
        page: Int = 1,
        perPage: Int = 20,
        searchQuery: String? = null,
        categoryId: Long? = null,
        brandFilter: String? = null,
        minPrice: Double? = null,
        maxPrice: Double? = null,
        onlyInStock: Boolean = false,
        onlyOnSale: Boolean = false,
        sortBy: SortOption = SortOption.LATEST
    ): Result<List<Product>> = withContext(Dispatchers.IO) {
        if (userPreferences.isLiveApiActive()) {
            try {
                val orderParam = when (sortBy) {
                    SortOption.PRICE_ASC -> "asc"
                    else -> "desc"
                }
                val orderByParam = when (sortBy) {
                    SortOption.PRICE_ASC, SortOption.PRICE_DESC -> "price"
                    SortOption.POPULARITY -> "popularity"
                    SortOption.RATING -> "rating"
                    SortOption.LATEST -> "date"
                }
                val response = networkClient.getApiService().getProducts(
                    page = page,
                    perPage = perPage,
                    search = searchQuery?.takeIf { it.isNotBlank() },
                    categoryId = categoryId,
                    orderBy = orderByParam,
                    order = orderParam,
                    onSale = if (onlyOnSale) true else null,
                    minPrice = minPrice?.toString(),
                    maxPrice = maxPrice?.toString()
                )
                if (response.isSuccessful && response.body() != null) {
                    return@withContext Result.success(response.body()!!)
                }
            } catch (e: Exception) {
                // Fallback to local filter
            }
        }

        // Local curated data filtering
        var filtered = UStore4UWatchData.products

        if (!searchQuery.isNullOrBlank()) {
            val q = searchQuery.trim().lowercase()
            filtered = filtered.filter {
                it.name.lowercase().contains(q) ||
                it.brand.lowercase().contains(q) ||
                it.sku.lowercase().contains(q) ||
                it.categories.any { cat -> cat.name.lowercase().contains(q) } ||
                it.shortDescription.lowercase().contains(q)
            }
        }

        if (categoryId != null && categoryId > 0) {
            val normalizedCatId = when (categoryId) {
                12L -> 101L // Men's Watches
                13L -> 102L // Women's Watches
                14L -> 103L // Wall Clocks
                else -> categoryId
            }
            filtered = filtered.filter { product ->
                product.categories.any { cat ->
                    cat.id == categoryId || cat.id == normalizedCatId ||
                    (normalizedCatId == 101L && (cat.slug.contains("men") || cat.name.contains("Men", ignoreCase = true))) ||
                    (normalizedCatId == 102L && (cat.slug.contains("women") || cat.name.contains("Women", ignoreCase = true))) ||
                    (normalizedCatId == 103L && (cat.slug.contains("clock") || cat.name.contains("Clock", ignoreCase = true)))
                }
            }
        }

        if (!brandFilter.isNullOrBlank() && brandFilter != "All Brands") {
            filtered = filtered.filter { it.brand.equals(brandFilter, ignoreCase = true) }
        }

        if (minPrice != null) {
            filtered = filtered.filter { (it.price.toDoubleOrNull() ?: 0.0) >= minPrice }
        }

        if (maxPrice != null) {
            filtered = filtered.filter { (it.price.toDoubleOrNull() ?: 0.0) <= maxPrice }
        }

        if (onlyInStock) {
            filtered = filtered.filter { it.inStock }
        }

        if (onlyOnSale) {
            filtered = filtered.filter { it.onSale }
        }

        filtered = when (sortBy) {
            SortOption.LATEST -> filtered.sortedByDescending { it.id }
            SortOption.PRICE_ASC -> filtered.sortedBy { it.price.toDoubleOrNull() ?: 0.0 }
            SortOption.PRICE_DESC -> filtered.sortedByDescending { it.price.toDoubleOrNull() ?: 0.0 }
            SortOption.POPULARITY -> filtered.sortedByDescending { it.ratingCount }
            SortOption.RATING -> filtered.sortedByDescending { it.averageRating.toDoubleOrNull() ?: 0.0 }
        }

        Result.success(filtered)
    }

    suspend fun getProductById(productId: Long): Result<Product?> = withContext(Dispatchers.IO) {
        if (userPreferences.isLiveApiActive()) {
            try {
                val response = networkClient.getApiService().getProductDetail(productId)
                if (response.isSuccessful && response.body() != null) {
                    val p = response.body()!!
                    recordRecentlyViewed(p)
                    return@withContext Result.success(p)
                }
            } catch (e: Exception) {
                // Fallback
            }
        }
        val product = UStore4UWatchData.products.find { it.id == productId }
        if (product != null) {
            recordRecentlyViewed(product)
        }
        Result.success(product)
    }

    suspend fun getProductBySlug(slug: String): Result<Product?> = withContext(Dispatchers.IO) {
        val product = UStore4UWatchData.products.find { it.slug.equals(slug, ignoreCase = true) }
            ?: UStore4UWatchData.products.firstOrNull()
        if (product != null) {
            recordRecentlyViewed(product)
        }
        Result.success(product)
    }

    suspend fun getProductReviews(productId: Long): Result<List<ProductReview>> = withContext(Dispatchers.IO) {
        if (userPreferences.isLiveApiActive()) {
            try {
                val response = networkClient.getApiService().getProductReviews(productId)
                if (response.isSuccessful && !response.body().isNullOrEmpty()) {
                    return@withContext Result.success(response.body()!!)
                }
            } catch (e: Exception) {
                // Fallback
            }
        }
        Result.success(UStore4UWatchData.sampleReviews)
    }

    suspend fun getRelatedProducts(product: Product): Result<List<Product>> = withContext(Dispatchers.IO) {
        val related = UStore4UWatchData.products.filter { p ->
            p.id != product.id && (product.relatedIds.contains(p.id) || p.categories.any { c -> product.categories.any { pc -> pc.id == c.id } })
        }.take(4)
        Result.success(related)
    }

    suspend fun validateCoupon(code: String): Result<Coupon?> = withContext(Dispatchers.IO) {
        val coupon = UStore4UWatchData.sampleCoupons.find { it.code.equals(code.trim(), ignoreCase = true) }
        if (coupon != null) {
            Result.success(coupon)
        } else {
            Result.failure(Exception("Invalid or expired coupon code: '$code'"))
        }
    }

    private suspend fun recordRecentlyViewed(product: Product) {
        try {
            recentlyViewedDao.insertRecentlyViewed(
                RecentlyViewedEntity(
                    productId = product.id,
                    name = product.name,
                    price = product.price.toDoubleOrNull() ?: 0.0,
                    regularPrice = product.regularPrice.toDoubleOrNull() ?: 0.0,
                    imageUrl = product.primaryImageUrl
                )
            )
        } catch (e: Exception) {
            // Ignore DB error for recently viewed
        }
    }

    fun getRecentlyViewed(): Flow<List<RecentlyViewedEntity>> = recentlyViewedDao.getRecentlyViewed()
}

enum class SortOption(val title: String) {
    LATEST("Latest Releases"),
    PRICE_ASC("Price: Low to High"),
    PRICE_DESC("Price: High to Low"),
    POPULARITY("Best Sellers"),
    RATING("Highest Rated")
}
