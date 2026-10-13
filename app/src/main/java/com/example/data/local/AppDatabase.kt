package com.example.data.local

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RoomDatabase
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface CartDao {
    @Query("SELECT * FROM cart_items ORDER BY timestamp DESC")
    fun getAllCartItems(): Flow<List<CartEntity>>

    @Query("SELECT * FROM cart_items WHERE productId = :productId AND selectedVariation = :variation LIMIT 1")
    suspend fun getCartItem(productId: Long, variation: String): CartEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(item: CartEntity)

    @Update
    suspend fun update(item: CartEntity)

    @Delete
    suspend fun delete(item: CartEntity)

    @Query("DELETE FROM cart_items WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM cart_items")
    suspend fun clearCart()

    @Query("SELECT SUM(quantity) FROM cart_items")
    fun getCartCount(): Flow<Int?>
}

@Dao
interface WishlistDao {
    @Query("SELECT * FROM wishlist_items ORDER BY timestamp DESC")
    fun getAllWishlistItems(): Flow<List<WishlistEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM wishlist_items WHERE productId = :productId)")
    fun isInWishlist(productId: Long): Flow<Boolean>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addToWishlist(item: WishlistEntity)

    @Query("DELETE FROM wishlist_items WHERE productId = :productId")
    suspend fun removeFromWishlist(productId: Long)

    @Query("DELETE FROM wishlist_items")
    suspend fun clearWishlist()
}

@Dao
interface RecentlyViewedDao {
    @Query("SELECT * FROM recently_viewed ORDER BY timestamp DESC LIMIT 10")
    fun getRecentlyViewed(): Flow<List<RecentlyViewedEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecentlyViewed(item: RecentlyViewedEntity)

    @Query("DELETE FROM recently_viewed")
    suspend fun clearRecentlyViewed()
}

@Database(
    entities = [CartEntity::class, WishlistEntity::class, RecentlyViewedEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun cartDao(): CartDao
    abstract fun wishlistDao(): WishlistDao
    abstract fun recentlyViewedDao(): RecentlyViewedDao
}
