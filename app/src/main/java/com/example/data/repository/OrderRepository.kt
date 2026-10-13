package com.example.data.repository

import com.example.data.api.NetworkClient
import com.example.data.api.WooCommerceConfig
import com.example.data.local.UserPreferences
import com.example.data.model.Address
import com.example.data.model.Order
import com.example.data.model.OrderLineItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class OrderRepository(
    private val networkClient: NetworkClient,
    private val userPreferences: UserPreferences
) {
    private val _localOrders = MutableStateFlow(getInitialOrders())
    val orders: StateFlow<List<Order>> = _localOrders.asStateFlow()

    private fun getInitialOrders(): List<Order> {
        return listOf(
            Order(
                id = 98412,
                status = "processing",
                currency = "INR",
                total = "1850000.00",
                discountTotal = "0.00",
                shippingTotal = "0.00",
                dateCreated = "2026-08-30T10:15:00",
                paymentMethod = "stripe",
                paymentMethodTitle = "Direct Bank Wire / Card",
                trackingNumber = "U4U-FEDEX-918234102",
                trackingUrl = "https://www.fedex.com",
                estimatedDelivery = "September 03, 2026 by 6:00 PM",
                billing = Address(
                    firstName = "Alexander",
                    lastName = "Wright",
                    address1 = "Plot 42, Jubilee Hills Road No. 36",
                    city = "Hyderabad",
                    state = "Telangana",
                    postcode = "500033",
                    country = "India",
                    email = "alexander.w@example.com",
                    phone = "+91 98765 43210"
                ),
                shipping = Address(
                    firstName = "Alexander",
                    lastName = "Wright",
                    address1 = "Plot 42, Jubilee Hills Road No. 36",
                    city = "Hyderabad",
                    state = "Telangana",
                    postcode = "500033",
                    country = "India",
                    phone = "+91 98765 43210"
                ),
                lineItems = listOf(
                    OrderLineItem(
                        id = 101,
                        name = "Patek Philippe Grand Complications Celestial Ref. 6102P",
                        productId = 1004,
                        quantity = 1,
                        price = 1850000.00,
                        subtotal = "1850000.00",
                        total = "1850000.00",
                        sku = "PP-6102P-SKY",
                        imageUrl = "https://images.unsplash.com/photo-1524805444758-089113d48a6d?w=800&auto=format&fit=crop&q=80"
                    )
                )
            ),
            Order(
                id = 97150,
                status = "shipped",
                currency = "INR",
                total = "980000.00",
                discountTotal = "0.00",
                shippingTotal = "0.00",
                dateCreated = "2026-08-28T09:45:00",
                paymentMethod = "stripe",
                paymentMethodTitle = "Credit Card / Stripe",
                trackingNumber = "U4U-BD-749201948",
                trackingUrl = "https://www.bluedart.com",
                estimatedDelivery = "September 01, 2026 (Tomorrow) by 2:00 PM",
                billing = Address(
                    firstName = "Alexander",
                    lastName = "Wright",
                    address1 = "Plot 42, Jubilee Hills Road No. 36",
                    city = "Hyderabad",
                    state = "Telangana",
                    postcode = "500033",
                    country = "India",
                    email = "alexander.w@example.com",
                    phone = "+91 98765 43210"
                ),
                shipping = Address(
                    firstName = "Alexander",
                    lastName = "Wright",
                    address1 = "Plot 42, Jubilee Hills Road No. 36",
                    city = "Hyderabad",
                    state = "Telangana",
                    postcode = "500033",
                    country = "India",
                    phone = "+91 98765 43210"
                ),
                lineItems = listOf(
                    OrderLineItem(
                        id = 102,
                        name = "Rolex Cosmograph Daytona Oystersteel 116500LN",
                        productId = 1002,
                        quantity = 1,
                        price = 980000.00,
                        subtotal = "980000.00",
                        total = "980000.00",
                        sku = "RLX-116500LN-BLK",
                        imageUrl = "https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=800&auto=format&fit=crop&q=80"
                    )
                )
            ),
            Order(
                id = 94821,
                status = "completed",
                currency = "INR",
                total = "48900.00",
                discountTotal = "0.00",
                shippingTotal = "0.00",
                dateCreated = "2026-08-20T14:32:00",
                paymentMethod = "stripe",
                paymentMethodTitle = "Credit Card / Stripe",
                trackingNumber = "U4U-FEDEX-883920194",
                trackingUrl = "https://www.fedex.com",
                estimatedDelivery = "Delivered on August 24, 2026",
                billing = Address(
                    firstName = "Alexander",
                    lastName = "Wright",
                    address1 = "Plot 42, Jubilee Hills Road No. 36",
                    city = "Hyderabad",
                    state = "Telangana",
                    postcode = "500033",
                    country = "India",
                    email = "alexander.w@example.com",
                    phone = "+91 98765 43210"
                ),
                shipping = Address(
                    firstName = "Alexander",
                    lastName = "Wright",
                    address1 = "Plot 42, Jubilee Hills Road No. 36",
                    city = "Hyderabad",
                    state = "Telangana",
                    postcode = "500033",
                    country = "India",
                    phone = "+91 98765 43210"
                ),
                lineItems = listOf(
                    OrderLineItem(
                        id = 103,
                        name = "UStore4U Royal Chronometer Automatic",
                        productId = 1001,
                        quantity = 1,
                        price = 48900.00,
                        subtotal = "48900.00",
                        total = "48900.00",
                        sku = "U4U-RC-001",
                        imageUrl = "https://images.unsplash.com/photo-1522335789203-aabd1fc54bc9?w=800&auto=format&fit=crop&q=80"
                    )
                )
            )
        )
    }

    /**
     * Fetch orders from the live WooCommerce Order API
     */
    suspend fun fetchCustomerOrders(customerId: Long? = null): Result<List<Order>> = withContext(Dispatchers.IO) {
        if (userPreferences.isLiveApiActive()) {
            try {
                val targetCustomerId = customerId ?: userPreferences.getSavedCustomer()?.id ?: 0L
                val response = networkClient.getApiService().getCustomerOrders(
                    customerId = targetCustomerId,
                    perPage = 20
                )
                if (response.isSuccessful && response.body() != null) {
                    val remoteOrders = response.body()!!
                    if (remoteOrders.isNotEmpty()) {
                        // Merge remote orders with local list
                        val currentList = _localOrders.value.toMutableList()
                        val remoteIds = remoteOrders.map { it.id }.toSet()
                        val filteredLocal = currentList.filter { it.id !in remoteIds }
                        val merged = remoteOrders + filteredLocal
                        _localOrders.value = merged
                        return@withContext Result.success(merged)
                    }
                }
            } catch (e: Exception) {
                // Fallback to local cached orders
            }
        }
        Result.success(_localOrders.value)
    }

    /**
     * Fetch single order details directly from WooCommerce Order API (/wp-json/wc/v3/orders/{id})
     */
    suspend fun fetchOrderByIdFromApi(orderId: Long): Result<Order> = withContext(Dispatchers.IO) {
        if (userPreferences.isLiveApiActive()) {
            try {
                val response = networkClient.getApiService().getOrderDetail(orderId)
                if (response.isSuccessful && response.body() != null) {
                    val remoteOrder = response.body()!!
                    // Update in memory cache
                    val currentList = _localOrders.value.toMutableList()
                    val index = currentList.indexOfFirst { it.id == orderId }
                    if (index >= 0) {
                        currentList[index] = remoteOrder
                    } else {
                        currentList.add(0, remoteOrder)
                    }
                    _localOrders.value = currentList
                    return@withContext Result.success(remoteOrder)
                }
            } catch (e: Exception) {
                // Return local order if network failed
            }
        }

        val localOrder = _localOrders.value.find { it.id == orderId }
        if (localOrder != null) {
            Result.success(localOrder)
        } else {
            Result.failure(NoSuchElementException("Order #$orderId could not be found."))
        }
    }

    /**
     * Search orders by order ID number or tracking AWB code
     */
    suspend fun searchOrders(query: String): List<Order> = withContext(Dispatchers.IO) {
        val cleanQuery = query.trim().removePrefix("#")
        if (cleanQuery.isBlank()) return@withContext _localOrders.value

        val idNum = cleanQuery.toLongOrNull()
        _localOrders.value.filter { order ->
            (idNum != null && order.id == idNum) ||
            order.trackingNumber?.contains(cleanQuery, ignoreCase = true) == true ||
            order.lineItems.any { it.name.contains(cleanQuery, ignoreCase = true) || it.sku.contains(cleanQuery, ignoreCase = true) } ||
            order.shipping.firstName.contains(cleanQuery, ignoreCase = true) ||
            order.shipping.lastName.contains(cleanQuery, ignoreCase = true)
        }
    }

    suspend fun createOrder(
        billing: Address,
        shipping: Address,
        lineItems: List<OrderLineItem>,
        subtotal: Double,
        discount: Double,
        shippingFee: Double,
        total: Double,
        paymentMethod: String,
        paymentTitle: String,
        orderNotes: String
    ): Result<Order> = withContext(Dispatchers.IO) {
        val orderId = (80000..99999).random().toLong()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US)
        val currentDate = dateFormat.format(Date())

        val newOrder = Order(
            id = orderId,
            status = "processing",
            currency = WooCommerceConfig.CURRENCY_CODE,
            total = "%.2f".format(total),
            discountTotal = "%.2f".format(discount),
            shippingTotal = "%.2f".format(shippingFee),
            dateCreated = currentDate,
            paymentMethod = paymentMethod,
            paymentMethodTitle = paymentTitle,
            trackingNumber = "U4U-FEDEX-${(100000..999999).random()}",
            trackingUrl = "https://www.fedex.com",
            estimatedDelivery = "Estimated delivery in 2-4 business days",
            billing = billing,
            shipping = shipping,
            lineItems = lineItems
        )

        // If live API enabled, attempt to push order to WooCommerce
        if (userPreferences.isLiveApiActive()) {
            try {
                val orderMap = mapOf(
                    "payment_method" to paymentMethod,
                    "payment_method_title" to paymentTitle,
                    "set_paid" to false,
                    "billing" to mapOf(
                        "first_name" to billing.firstName,
                        "last_name" to billing.lastName,
                        "address_1" to billing.address1,
                        "city" to billing.city,
                        "state" to billing.state,
                        "postcode" to billing.postcode,
                        "country" to billing.country,
                        "email" to billing.email,
                        "phone" to billing.phone
                    ),
                    "shipping" to mapOf(
                        "first_name" to shipping.firstName,
                        "last_name" to shipping.lastName,
                        "address_1" to shipping.address1,
                        "city" to shipping.city,
                        "state" to shipping.state,
                        "postcode" to shipping.postcode,
                        "country" to shipping.country
                    ),
                    "line_items" to lineItems.map {
                        mapOf(
                            "product_id" to it.productId,
                            "quantity" to it.quantity
                        )
                    },
                    "customer_note" to orderNotes
                )
                val response = networkClient.getApiService().createOrder(orderMap)
                if (response.isSuccessful && response.body() != null) {
                    val remoteOrder = response.body()!!
                    val currentList = _localOrders.value.toMutableList()
                    currentList.add(0, remoteOrder)
                    _localOrders.value = currentList
                    return@withContext Result.success(remoteOrder)
                }
            } catch (e: Exception) {
                // Fallback to local recorded order
            }
        }

        val currentList = _localOrders.value.toMutableList()
        currentList.add(0, newOrder)
        _localOrders.value = currentList

        Result.success(newOrder)
    }

    suspend fun getOrderById(orderId: Long): Order? = withContext(Dispatchers.IO) {
        _localOrders.value.find { it.id == orderId }
    }
}
