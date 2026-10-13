package com.example.data.repository

import com.example.data.local.CartDao
import com.example.data.local.CartEntity
import com.example.data.local.WishlistDao
import com.example.data.local.WishlistEntity
import com.example.data.model.Product
import kotlinx.coroutines.flow.Flow

class WishlistRepository(
    private val wishlistDao: WishlistDao,
    private val cartDao: CartDao
) {
    val wishlistItems: Flow<List<WishlistEntity>> = wishlistDao.getAllWishlistItems()

    fun isInWishlist(productId: Long): Flow<Boolean> = wishlistDao.isInWishlist(productId)

    suspend fun toggleWishlist(product: Product) {
        val inWishlist = false // Checked via action
        wishlistDao.addToWishlist(
            WishlistEntity(
                productId = product.id,
                name = product.name,
                price = product.price.toDoubleOrNull() ?: 0.0,
                regularPrice = product.regularPrice.toDoubleOrNull() ?: 0.0,
                imageUrl = product.primaryImageUrl,
                stockStatus = product.stockStatus,
                rating = product.averageRating.toFloatOrNull() ?: 5.0f,
                ratingCount = product.ratingCount
            )
        )
    }

    suspend fun addToWishlist(product: Product) {
        wishlistDao.addToWishlist(
            WishlistEntity(
                productId = product.id,
                name = product.name,
                price = product.price.toDoubleOrNull() ?: 0.0,
                regularPrice = product.regularPrice.toDoubleOrNull() ?: 0.0,
                imageUrl = product.primaryImageUrl,
                stockStatus = product.stockStatus,
                rating = product.averageRating.toFloatOrNull() ?: 5.0f,
                ratingCount = product.ratingCount
            )
        )
    }

    suspend fun removeFromWishlist(productId: Long) {
        wishlistDao.removeFromWishlist(productId)
    }

    suspend fun moveToCart(item: WishlistEntity) {
        wishlistDao.removeFromWishlist(item.productId)
        val existing = cartDao.getCartItem(item.productId, "")
        if (existing != null) {
            cartDao.update(existing.copy(quantity = existing.quantity + 1))
        } else {
            cartDao.insertOrUpdate(
                CartEntity(
                    productId = item.productId,
                    name = item.name,
                    price = item.price,
                    regularPrice = item.regularPrice,
                    imageUrl = item.imageUrl,
                    quantity = 1,
                    selectedVariation = ""
                )
            )
        }
    }

    suspend fun clearWishlist() {
        wishlistDao.clearWishlist()
    }
}
