package com.example.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Product(
    @Json(name = "id") val id: Long,
    @Json(name = "name") val name: String,
    @Json(name = "slug") val slug: String = "",
    @Json(name = "permalink") val permalink: String = "",
    @Json(name = "price") val price: String = "0",
    @Json(name = "regular_price") val regularPrice: String = "",
    @Json(name = "sale_price") val salePrice: String = "",
    @Json(name = "on_sale") val onSale: Boolean = false,
    @Json(name = "description") val description: String = "",
    @Json(name = "short_description") val shortDescription: String = "",
    @Json(name = "sku") val sku: String = "",
    @Json(name = "stock_status") val stockStatus: String = "instock",
    @Json(name = "stock_quantity") val stockQuantity: Int? = null,
    @Json(name = "rating_count") val ratingCount: Int = 0,
    @Json(name = "average_rating") val averageRating: String = "0.0",
    @Json(name = "featured") val featured: Boolean = false,
    @Json(name = "categories") val categories: List<ProductCategoryRef> = emptyList(),
    @Json(name = "images") val images: List<ProductImage> = emptyList(),
    @Json(name = "attributes") val attributes: List<ProductAttribute> = emptyList(),
    @Json(name = "related_ids") val relatedIds: List<Long> = emptyList(),
    // Custom watch specifications (parsed or computed)
    val brand: String = "UStore4U Signature",
    val movement: String = "Japanese Quartz",
    val caseDiameter: String = "42mm",
    val waterResistance: String = "5 ATM / 50m",
    val glassType: String = "Sapphire Crystal",
    val strapMaterial: String = "Genuine Leather",
    val warranty: String = "2-Year International Warranty"
) {
    val formattedPrice: String
        get() = try {
            val num = price.toDoubleOrNull() ?: 0.0
            if (num % 1.0 == 0.0) "₹%,d".format(num.toLong()) else "₹%,.2f".format(num)
        } catch (e: Exception) {
            "₹${price}"
        }

    val formattedRegularPrice: String
        get() = try {
            val num = regularPrice.toDoubleOrNull() ?: 0.0
            if (num % 1.0 == 0.0) "₹%,d".format(num.toLong()) else "₹%,.2f".format(num)
        } catch (e: Exception) {
            "₹${regularPrice}"
        }

    val discountPercentage: Int
        get() {
            val reg = regularPrice.toDoubleOrNull() ?: 0.0
            val cur = price.toDoubleOrNull() ?: 0.0
            return if (reg > cur && reg > 0) {
                (((reg - cur) / reg) * 100).toInt()
            } else 0
        }

    val primaryImageUrl: String
        get() = images.firstOrNull()?.src ?: ""

    val inStock: Boolean
        get() = stockStatus.equals("instock", ignoreCase = true)
}

@JsonClass(generateAdapter = true)
data class ProductCategoryRef(
    @Json(name = "id") val id: Long,
    @Json(name = "name") val name: String,
    @Json(name = "slug") val slug: String = ""
)

@JsonClass(generateAdapter = true)
data class ProductCategory(
    @Json(name = "id") val id: Long,
    @Json(name = "name") val name: String,
    @Json(name = "slug") val slug: String = "",
    @Json(name = "description") val description: String = "",
    @Json(name = "count") val count: Int = 0,
    @Json(name = "image") val image: ProductImage? = null
)

@JsonClass(generateAdapter = true)
data class ProductImage(
    @Json(name = "id") val id: Long = 0,
    @Json(name = "src") val src: String = "",
    @Json(name = "name") val name: String = "",
    @Json(name = "alt") val alt: String = ""
)

@JsonClass(generateAdapter = true)
data class ProductAttribute(
    @Json(name = "id") val id: Long = 0,
    @Json(name = "name") val name: String = "",
    @Json(name = "options") val options: List<String> = emptyList(),
    @Json(name = "visible") val visible: Boolean = true,
    @Json(name = "variation") val variation: Boolean = false
)

@JsonClass(generateAdapter = true)
data class ProductReview(
    @Json(name = "id") val id: Long = 0,
    @Json(name = "product_id") val productId: Long = 0,
    @Json(name = "date_created") val dateCreated: String = "",
    @Json(name = "reviewer") val reviewer: String = "",
    @Json(name = "reviewer_email") val reviewerEmail: String = "",
    @Json(name = "review") val review: String = "",
    @Json(name = "rating") val rating: Int = 5,
    @Json(name = "verified") val verified: Boolean = true
)

@JsonClass(generateAdapter = true)
data class Coupon(
    @Json(name = "id") val id: Long = 0,
    @Json(name = "code") val code: String = "",
    @Json(name = "amount") val amount: String = "0",
    @Json(name = "discount_type") val discountType: String = "percent", // percent, fixed_cart, fixed_product
    @Json(name = "description") val description: String = "",
    @Json(name = "minimum_amount") val minimumAmount: String = "0"
)

@JsonClass(generateAdapter = true)
data class Customer(
    @Json(name = "id") val id: Long = 0,
    @Json(name = "email") val email: String = "",
    @Json(name = "first_name") val firstName: String = "",
    @Json(name = "last_name") val lastName: String = "",
    @Json(name = "username") val username: String = "",
    @Json(name = "billing") val billing: Address = Address(),
    @Json(name = "shipping") val shipping: Address = Address(),
    @Json(name = "avatar_url") val avatarUrl: String = "",
    @Json(name = "role") val role: String = "customer" // "customer" or "owner"
) {
    val isOwner: Boolean
        get() = role.equals("owner", ignoreCase = true) || role.equals("admin", ignoreCase = true) || role.equals("store_owner", ignoreCase = true)

    val fullName: String
        get() = listOf(firstName, lastName).filter { it.isNotBlank() }.joinToString(" ").ifBlank { username.ifBlank { if (isOwner) "Store Owner" else "Valued Customer" } }
}

@JsonClass(generateAdapter = true)
data class Address(
    @Json(name = "first_name") val firstName: String = "",
    @Json(name = "last_name") val lastName: String = "",
    @Json(name = "company") val company: String = "",
    @Json(name = "address_1") val address1: String = "",
    @Json(name = "address_2") val address2: String = "",
    @Json(name = "city") val city: String = "",
    @Json(name = "state") val state: String = "",
    @Json(name = "postcode") val postcode: String = "",
    @Json(name = "country") val country: String = "United States",
    @Json(name = "email") val email: String = "",
    @Json(name = "phone") val phone: String = ""
) {
    val fullName: String
        get() = listOf(firstName, lastName).filter { it.isNotBlank() }.joinToString(" ")

    val formattedOneLine: String
        get() = listOf(address1, city, state, postcode, country).filter { it.isNotBlank() }.joinToString(", ")
}

@JsonClass(generateAdapter = true)
data class Order(
    @Json(name = "id") val id: Long = 0,
    @Json(name = "status") val status: String = "processing",
    @Json(name = "currency") val currency: String = "USD",
    @Json(name = "total") val total: String = "0.00",
    @Json(name = "discount_total") val discountTotal: String = "0.00",
    @Json(name = "shipping_total") val shippingTotal: String = "0.00",
    @Json(name = "date_created") val dateCreated: String = "",
    @Json(name = "line_items") val lineItems: List<OrderLineItem> = emptyList(),
    @Json(name = "billing") val billing: Address = Address(),
    @Json(name = "shipping") val shipping: Address = Address(),
    @Json(name = "payment_method") val paymentMethod: String = "cod",
    @Json(name = "payment_method_title") val paymentMethodTitle: String = "Direct Bank Transfer / Card",
    val trackingNumber: String? = null,
    val trackingUrl: String? = null,
    val estimatedDelivery: String? = null
)

@JsonClass(generateAdapter = true)
data class OrderLineItem(
    @Json(name = "id") val id: Long = 0,
    @Json(name = "name") val name: String = "",
    @Json(name = "product_id") val productId: Long = 0,
    @Json(name = "variation_id") val variationId: Long = 0,
    @Json(name = "quantity") val quantity: Int = 1,
    @Json(name = "subtotal") val subtotal: String = "0.00",
    @Json(name = "total") val total: String = "0.00",
    @Json(name = "sku") val sku: String = "",
    @Json(name = "price") val price: Double = 0.0,
    val imageUrl: String = ""
)
