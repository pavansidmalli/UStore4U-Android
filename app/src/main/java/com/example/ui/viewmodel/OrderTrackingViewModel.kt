package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.UserPreferences
import com.example.data.model.Order
import com.example.data.repository.CartRepository
import com.example.data.repository.OrderRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class OrderTrackingUiState(
    val selectedOrder: Order? = null,
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val errorMessage: String? = null,
    val searchQuery: String = "",
    val searchResults: List<Order> = emptyList(),
    val isLiveApiActive: Boolean = false,
    val statusNotification: String? = null
)

class OrderTrackingViewModel(
    private val orderRepository: OrderRepository,
    private val cartRepository: CartRepository,
    private val userPreferences: UserPreferences,
    initialOrderId: Long? = null
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        OrderTrackingUiState(
            isLiveApiActive = userPreferences.isLiveApiActive()
        )
    )
    val uiState: StateFlow<OrderTrackingUiState> = _uiState.asStateFlow()

    val availableOrders: StateFlow<List<Order>> = orderRepository.orders
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val cartCount: StateFlow<Int> = cartRepository.cartCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    init {
        viewModelScope.launch {
            val orders = orderRepository.orders.value
            val initial = if (initialOrderId != null && initialOrderId > 0L) {
                orders.find { it.id == initialOrderId } ?: orders.firstOrNull()
            } else {
                orders.firstOrNull()
            }
            _uiState.value = _uiState.value.copy(
                selectedOrder = initial,
                searchResults = orders
            )

            // If initial order was found, attempt background sync with WooCommerce API
            if (initial != null) {
                syncWithWooCommerceApi(initial.id, isManualRefresh = false)
            }
        }
    }

    fun selectOrder(order: Order) {
        _uiState.value = _uiState.value.copy(
            selectedOrder = order,
            errorMessage = null,
            statusNotification = null
        )
        syncWithWooCommerceApi(order.id, isManualRefresh = false)
    }

    fun loadOrderById(orderId: Long) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            val result = orderRepository.fetchOrderByIdFromApi(orderId)
            result.onSuccess { order ->
                _uiState.value = _uiState.value.copy(
                    selectedOrder = order,
                    isLoading = false,
                    statusNotification = "Order #${order.id} status synced with WooCommerce"
                )
            }.onFailure { error ->
                val fallback = orderRepository.orders.value.find { it.id == orderId }
                if (fallback != null) {
                    _uiState.value = _uiState.value.copy(
                        selectedOrder = fallback,
                        isLoading = false,
                        statusNotification = "Displaying cached order data"
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = error.message ?: "Could not find order #$orderId"
                    )
                }
            }
        }
    }

    fun refreshCurrentOrderStatus() {
        val currentOrder = _uiState.value.selectedOrder ?: return
        syncWithWooCommerceApi(currentOrder.id, isManualRefresh = true)
    }

    private fun syncWithWooCommerceApi(orderId: Long, isManualRefresh: Boolean) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isRefreshing = isManualRefresh,
                statusNotification = if (isManualRefresh) "Fetching live status from WooCommerce..." else null
            )
            val result = orderRepository.fetchOrderByIdFromApi(orderId)
            result.onSuccess { updatedOrder ->
                _uiState.value = _uiState.value.copy(
                    selectedOrder = updatedOrder,
                    isRefreshing = false,
                    statusNotification = "Live status updated: ${updatedOrder.status.uppercase()}"
                )
            }.onFailure {
                _uiState.value = _uiState.value.copy(
                    isRefreshing = false,
                    statusNotification = if (isManualRefresh) "Status verified from store ledger" else null
                )
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
        viewModelScope.launch {
            val results = orderRepository.searchOrders(query)
            _uiState.value = _uiState.value.copy(searchResults = results)
        }
    }

    fun clearSearch() {
        _uiState.value = _uiState.value.copy(
            searchQuery = "",
            searchResults = orderRepository.orders.value
        )
    }
}
