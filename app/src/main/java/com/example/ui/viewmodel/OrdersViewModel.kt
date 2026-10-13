package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.Order
import com.example.data.model.OrderLineItem
import com.example.data.model.Product
import com.example.data.repository.CartRepository
import com.example.data.repository.OrderRepository
import com.example.data.repository.ProductRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class OrdersViewModel(
    private val orderRepository: OrderRepository,
    private val cartRepository: CartRepository,
    private val productRepository: ProductRepository? = null
) : ViewModel() {

    val orders: StateFlow<List<Order>> = orderRepository.orders
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val cartCount: StateFlow<Int> = cartRepository.cartCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedStatusFilter = MutableStateFlow("ALL")
    val selectedStatusFilter: StateFlow<String> = _selectedStatusFilter.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private val _selectedOrderForInvoice = MutableStateFlow<Order?>(null)
    val selectedOrderForInvoice: StateFlow<Order?> = _selectedOrderForInvoice.asStateFlow()

    private val _reorderSuccessMessage = MutableStateFlow<String?>(null)
    val reorderSuccessMessage: StateFlow<String?> = _reorderSuccessMessage.asStateFlow()

    val filteredOrders: StateFlow<List<Order>> = combine(
        orders,
        _searchQuery,
        _selectedStatusFilter
    ) { orderList, query, filter ->
        var list = orderList

        // Filter by Status
        if (filter != "ALL") {
            list = list.filter { order ->
                when (filter) {
                    "PROCESSING" -> order.status.equals("processing", ignoreCase = true) || order.status.equals("on-hold", ignoreCase = true)
                    "SHIPPED" -> order.status.equals("shipped", ignoreCase = true) || order.status.equals("in_transit", ignoreCase = true)
                    "COMPLETED" -> order.status.equals("completed", ignoreCase = true) || order.status.equals("delivered", ignoreCase = true)
                    "CANCELLED" -> order.status.equals("cancelled", ignoreCase = true) || order.status.equals("refunded", ignoreCase = true)
                    else -> true
                }
            }
        }

        // Filter by Search query
        if (query.isNotBlank()) {
            val q = query.trim().removePrefix("#").lowercase()
            list = list.filter { order ->
                order.id.toString().contains(q) ||
                order.trackingNumber?.lowercase()?.contains(q) == true ||
                order.lineItems.any { item ->
                    item.name.lowercase().contains(q) ||
                    item.sku.lowercase().contains(q)
                } ||
                order.paymentMethodTitle.lowercase().contains(q) ||
                order.shipping.firstName.lowercase().contains(q) ||
                order.shipping.lastName.lowercase().contains(q)
            }
        }

        list
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setStatusFilter(filter: String) {
        _selectedStatusFilter.value = filter
    }

    fun selectOrderForInvoice(order: Order?) {
        _selectedOrderForInvoice.value = order
    }

    fun clearReorderMessage() {
        _reorderSuccessMessage.value = null
    }

    fun refreshOrders() {
        viewModelScope.launch {
            _isRefreshing.value = true
            try {
                orderRepository.fetchCustomerOrders()
            } catch (e: Exception) {
                // Ignore network errors
            } finally {
                _isRefreshing.value = false
            }
        }
    }

    fun reorderItem(item: OrderLineItem) {
        viewModelScope.launch {
            val dummyProduct = Product(
                id = item.productId,
                name = item.name,
                price = item.price.toString(),
                regularPrice = item.price.toString(),
                images = listOf(com.example.data.model.ProductImage(src = item.imageUrl)),
                sku = item.sku
            )
            cartRepository.addToCart(dummyProduct, quantity = 1)
            _reorderSuccessMessage.value = "Added \"${item.name}\" to your Bag!"
        }
    }

    fun reorderAllItems(order: Order) {
        viewModelScope.launch {
            order.lineItems.forEach { item ->
                val dummyProduct = Product(
                    id = item.productId,
                    name = item.name,
                    price = item.price.toString(),
                    regularPrice = item.price.toString(),
                    images = listOf(com.example.data.model.ProductImage(src = item.imageUrl)),
                    sku = item.sku
                )
                cartRepository.addToCart(dummyProduct, quantity = item.quantity)
            }
            _reorderSuccessMessage.value = "All items from Order #${order.id} added to Bag!"
        }
    }
}

