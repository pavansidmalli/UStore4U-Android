package com.example.data.api

import com.example.data.model.Coupon
import com.example.data.model.Customer
import com.example.data.model.Order
import com.example.data.model.Product
import com.example.data.model.ProductCategory
import com.example.data.model.ProductReview
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface WooCommerceApiService {

    @GET("wp-json/wc/v3/products")
    suspend fun getProducts(
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 20,
        @Query("search") search: String? = null,
        @Query("category") categoryId: Long? = null,
        @Query("orderby") orderBy: String? = "date",
        @Query("order") order: String? = "desc",
        @Query("featured") featured: Boolean? = null,
        @Query("on_sale") onSale: Boolean? = null,
        @Query("min_price") minPrice: String? = null,
        @Query("max_price") maxPrice: String? = null,
        @Query("status") status: String = "publish"
    ): Response<List<Product>>

    @GET("wp-json/wc/v3/products/{id}")
    suspend fun getProductDetail(
        @Path("id") productId: Long
    ): Response<Product>

    @GET("wp-json/wc/v3/products/categories")
    suspend fun getCategories(
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 50,
        @Query("hide_empty") hideEmpty: Boolean = false
    ): Response<List<ProductCategory>>

    @GET("wp-json/wc/v3/products/reviews")
    suspend fun getProductReviews(
        @Query("product") productId: Long,
        @Query("per_page") perPage: Int = 20
    ): Response<List<ProductReview>>

    @GET("wp-json/wc/v3/coupons")
    suspend fun getCoupons(
        @Query("code") code: String
    ): Response<List<Coupon>>

    @POST("wp-json/wc/v3/orders")
    suspend fun createOrder(
        @Body order: Map<String, Any>
    ): Response<Order>

    @GET("wp-json/wc/v3/orders")
    suspend fun getCustomerOrders(
        @Query("customer") customerId: Long,
        @Query("per_page") perPage: Int = 20
    ): Response<List<Order>>

    @GET("wp-json/wc/v3/orders/{id}")
    suspend fun getOrderDetail(
        @Path("id") orderId: Long
    ): Response<Order>

    @POST("wp-json/wc/v3/customers")
    suspend fun createCustomer(
        @Body customer: Map<String, Any>
    ): Response<Customer>
}
