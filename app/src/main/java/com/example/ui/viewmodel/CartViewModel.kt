package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.api.WooCommerceConfig
import com.example.data.local.CartEntity
import com.example.data.model.Coupon
import com.example.data.repository.CartRepository
import com.example.data.repository.ProductRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class CartUiState(
    val appliedCoupon: Coupon? = null,
    val couponCodeInput: String = "",
    val couponError: String? = null,
    val couponSuccessMessage: String? = null,
    val isApplyingCoupon: Boolean = false
)

class CartViewModel(
    private val cartRepository: CartRepository,
    private val productRepository: ProductRepository
) : ViewModel() {

    val cartItems: StateFlow<List<CartEntity>> = cartRepository.cartItems
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val cartCount: StateFlow<Int> = cartRepository.cartCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    private val _uiState = MutableStateFlow(CartUiState())
    val uiState: StateFlow<CartUiState> = _uiState.asStateFlow()

    fun updateQuantity(item: CartEntity, newQty: Int) {
        viewModelScope.launch {
            if (newQty <= 0) {
                cartRepository.removeItem(item)
            } else {
                cartRepository.updateItem(item.copy(quantity = newQty))
            }
        }
    }

    fun removeItem(item: CartEntity) {
        viewModelScope.launch {
            cartRepository.removeItem(item)
        }
    }

    fun saveForLater(item: CartEntity) {
        viewModelScope.launch {
            cartRepository.moveToWishlist(item)
        }
    }

    fun onCouponCodeChanged(code: String) {
        _uiState.value = _uiState.value.copy(couponCodeInput = code, couponError = null, couponSuccessMessage = null)
    }

    fun applyCoupon() {
        val code = _uiState.value.couponCodeInput.trim()
        if (code.isBlank()) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isApplyingCoupon = true, couponError = null)
            val result = productRepository.validateCoupon(code)
            if (result.isSuccess) {
                val coupon = result.getOrNull()
                _uiState.value = _uiState.value.copy(
                    isApplyingCoupon = false,
                    appliedCoupon = coupon,
                    couponSuccessMessage = "Coupon '${coupon?.code}' applied successfully!",
                    couponError = null
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    isApplyingCoupon = false,
                    couponError = result.exceptionOrNull()?.message ?: "Invalid coupon code."
                )
            }
        }
    }

    fun removeCoupon() {
        _uiState.value = _uiState.value.copy(
            appliedCoupon = null,
            couponCodeInput = "",
            couponSuccessMessage = null,
            couponError = null
        )
    }

    fun calculateSubtotal(items: List<CartEntity>): Double {
        return items.sumOf { it.price * it.quantity }
    }

    fun calculateDiscount(subtotal: Double, coupon: Coupon?): Double {
        if (coupon == null) return 0.0
        val amount = coupon.amount.toDoubleOrNull() ?: 0.0
        return when (coupon.discountType) {
            "percent" -> (subtotal * (amount / 100.0))
            "fixed_cart", "fixed_product" -> amount.coerceAtMost(subtotal)
            else -> 0.0
        }
    }

    fun calculateShipping(subtotal: Double): Double {
        return if (subtotal >= WooCommerceConfig.FREE_SHIPPING_THRESHOLD || subtotal == 0.0) 0.0 else WooCommerceConfig.STANDARD_SHIPPING_COST
    }

    fun calculateTotal(subtotal: Double, discount: Double, shipping: Double): Double {
        return (subtotal - discount + shipping).coerceAtLeast(0.0)
    }

    fun clearCart() {
        viewModelScope.launch {
            cartRepository.clearCart()
        }
    }
}
