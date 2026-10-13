package com.example.data.repository

import com.example.data.local.CartDao
import com.example.data.local.CartEntity
import com.example.data.local.WishlistDao
import com.example.data.local.WishlistEntity
import com.example.data.model.Coupon
import com.example.data.model.Product
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class CartRepository(
    private val cartDao: CartDao,
    private val wishlistDao: WishlistDao
) {
    val cartItems: Flow<List<CartEntity>> = cartDao.getAllCartItems()
    val cartCount: Flow<Int> = cartDao.getCartCount().map { it ?: 0 }

    suspend fun addToCart(
        product: Product,
        quantity: Int = 1,
        selectedVariation: String = ""
    ) {
        val existing = cartDao.getCartItem(product.id, selectedVariation)
        if (existing != null) {
            cartDao.update(existing.copy(quantity = existing.quantity + quantity))
        } else {
            cartDao.insertOrUpdate(
                CartEntity(
                    productId = product.id,
                    name = product.name,
                    price = product.price.toDoubleOrNull() ?: 0.0,
                    regularPrice = product.regularPrice.toDoubleOrNull() ?: 0.0,
                    imageUrl = product.primaryImageUrl,
                    quantity = quantity,
                    selectedVariation = selectedVariation,
                    sku = product.sku
                )
            )
        }
    }

    suspend fun updateQuantity(cartId: Long, newQuantity: Int) {
        if (newQuantity <= 0) {
            cartDao.deleteById(cartId)
        } else {
            // Read items and update
        }
    }

    suspend fun updateItem(item: CartEntity) {
        if (item.quantity <= 0) {
            cartDao.delete(item)
        } else {
            cartDao.update(item)
        }
    }

    suspend fun removeItem(item: CartEntity) {
        cartDao.delete(item)
    }

    suspend fun moveToWishlist(item: CartEntity) {
        cartDao.delete(item)
        wishlistDao.addToWishlist(
            WishlistEntity(
                productId = item.productId,
                name = item.name,
                price = item.price,
                regularPrice = item.regularPrice,
                imageUrl = item.imageUrl
            )
        )
    }

    suspend fun clearCart() {
        cartDao.clearCart()
    }
}
