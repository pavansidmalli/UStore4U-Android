package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cart_items")
data class CartEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val productId: Long,
    val name: String,
    val price: Double,
    val regularPrice: Double,
    val imageUrl: String,
    val quantity: Int = 1,
    val selectedVariation: String = "",
    val sku: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "wishlist_items")
data class WishlistEntity(
    @PrimaryKey val productId: Long,
    val name: String,
    val price: Double,
    val regularPrice: Double,
    val imageUrl: String,
    val stockStatus: String = "instock",
    val rating: Float = 5.0f,
    val ratingCount: Int = 0,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "recently_viewed")
data class RecentlyViewedEntity(
    @PrimaryKey val productId: Long,
    val name: String,
    val price: Double,
    val regularPrice: Double,
    val imageUrl: String,
    val timestamp: Long = System.currentTimeMillis()
)
