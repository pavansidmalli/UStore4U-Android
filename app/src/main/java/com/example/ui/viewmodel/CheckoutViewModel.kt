package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.api.WooCommerceConfig
import com.example.data.local.CartEntity
import com.example.data.model.Address
import com.example.data.model.Coupon
import com.example.data.model.Order
import com.example.data.model.OrderLineItem
import com.example.data.repository.CartRepository
import com.example.data.repository.OrderRepository
import com.example.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class CheckoutUiState(
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val phone: String = "",
    val address1: String = "",
    val city: String = "",
    val state: String = "",
    val postcode: String = "",
    val country: String = "United States",
    val sameShippingAsBilling: Boolean = true,
    val shipAddress1: String = "",
    val shipCity: String = "",
    val shipState: String = "",
    val shipPostcode: String = "",
    val orderNotes: String = "",
    val shippingMethod: String = "standard", // standard, express
    val paymentMethod: String = "web_checkout", // web_checkout, cod, bacs
    val isSubmitting: Boolean = false,
    val orderCreated: Order? = null,
    val errorMessage: String? = null
)

class CheckoutViewModel(
    private val cartRepository: CartRepository,
    private val orderRepository: OrderRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    val cartItems: StateFlow<List<CartEntity>> = cartRepository.cartItems
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _uiState = MutableStateFlow(CheckoutUiState())
    val uiState: StateFlow<CheckoutUiState> = _uiState.asStateFlow()

    init {
        // Prepopulate with logged in user if available
        userRepository.currentCustomer.value?.let { customer ->
            _uiState.value = _uiState.value.copy(
                firstName = customer.firstName,
                lastName = customer.lastName,
                email = customer.email,
                phone = customer.billing.phone,
                address1 = customer.billing.address1,
                city = customer.billing.city,
                state = customer.billing.state,
                postcode = customer.billing.postcode,
                country = customer.billing.country.ifBlank { "United States" }
            )
        }
    }

    fun onFirstNameChanged(v: String) { _uiState.value = _uiState.value.copy(firstName = v, errorMessage = null) }
    fun onLastNameChanged(v: String) { _uiState.value = _uiState.value.copy(lastName = v, errorMessage = null) }
    fun onEmailChanged(v: String) { _uiState.value = _uiState.value.copy(email = v, errorMessage = null) }
    fun onPhoneChanged(v: String) { _uiState.value = _uiState.value.copy(phone = v, errorMessage = null) }
    fun onAddress1Changed(v: String) { _uiState.value = _uiState.value.copy(address1 = v, errorMessage = null) }
    fun onCityChanged(v: String) { _uiState.value = _uiState.value.copy(city = v, errorMessage = null) }
    fun onStateChanged(v: String) { _uiState.value = _uiState.value.copy(state = v, errorMessage = null) }
    fun onPostcodeChanged(v: String) { _uiState.value = _uiState.value.copy(postcode = v, errorMessage = null) }
    fun onCountryChanged(v: String) { _uiState.value = _uiState.value.copy(country = v) }
    fun onSameShippingChanged(v: Boolean) { _uiState.value = _uiState.value.copy(sameShippingAsBilling = v) }
    fun onShipAddress1Changed(v: String) { _uiState.value = _uiState.value.copy(shipAddress1 = v) }
    fun onShipCityChanged(v: String) { _uiState.value = _uiState.value.copy(shipCity = v) }
    fun onShipStateChanged(v: String) { _uiState.value = _uiState.value.copy(shipState = v) }
    fun onShipPostcodeChanged(v: String) { _uiState.value = _uiState.value.copy(shipPostcode = v) }
    fun onOrderNotesChanged(v: String) { _uiState.value = _uiState.value.copy(orderNotes = v) }
    fun onShippingMethodChanged(v: String) { _uiState.value = _uiState.value.copy(shippingMethod = v) }
    fun onPaymentMethodChanged(v: String) { _uiState.value = _uiState.value.copy(paymentMethod = v) }

    fun placeOrder(coupon: Coupon? = null, onSuccess: (Order) -> Unit) {
        val s = _uiState.value
        if (s.firstName.isBlank() || s.lastName.isBlank() || s.email.isBlank() || s.address1.isBlank() || s.city.isBlank() || s.postcode.isBlank()) {
            _uiState.value = s.copy(errorMessage = "Please complete all required shipping & contact fields.")
            return
        }

        val items = cartItems.value
        if (items.isEmpty()) {
            _uiState.value = s.copy(errorMessage = "Your shopping cart is empty.")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSubmitting = true, errorMessage = null)

            val subtotal = items.sumOf { it.price * it.quantity }
            val discount = if (coupon != null) {
                val amt = coupon.amount.toDoubleOrNull() ?: 0.0
                if (coupon.discountType == "percent") (subtotal * (amt / 100.0)) else amt.coerceAtMost(subtotal)
            } else 0.0

            val shippingFee = if (s.shippingMethod == "express") {
                WooCommerceConfig.EXPRESS_SHIPPING_COST
            } else {
                if (subtotal >= WooCommerceConfig.FREE_SHIPPING_THRESHOLD) 0.0 else WooCommerceConfig.STANDARD_SHIPPING_COST
            }

            val finalTotal = (subtotal - discount + shippingFee).coerceAtLeast(0.0)

            val billingAddress = Address(
                firstName = s.firstName,
                lastName = s.lastName,
                address1 = s.address1,
                city = s.city,
                state = s.state,
                postcode = s.postcode,
                country = s.country,
                email = s.email,
                phone = s.phone
            )

            val shippingAddress = if (s.sameShippingAsBilling) {
                billingAddress
            } else {
                Address(
                    firstName = s.firstName,
                    lastName = s.lastName,
                    address1 = s.shipAddress1,
                    city = s.shipCity,
                    state = s.shipState,
                    postcode = s.shipPostcode,
                    country = s.country
                )
            }

            val lineItems = items.map {
                OrderLineItem(
                    id = it.id,
                    name = it.name,
                    productId = it.productId,
                    quantity = it.quantity,
                    price = it.price,
                    subtotal = "%.2f".format(it.price * it.quantity),
                    total = "%.2f".format(it.price * it.quantity),
                    sku = it.sku,
                    imageUrl = it.imageUrl
                )
            }

            val paymentTitle = when (s.paymentMethod) {
                "web_checkout" -> "Official UStore4U Secure Web Gateway (Stripe/Cards/PayPal)"
                "bacs" -> "Direct Bank Wire Transfer"
                "cod" -> "Cash on Delivery / Courier Pay"
                else -> "Secure WooCommerce Gateway"
            }

            val result = orderRepository.createOrder(
                billing = billingAddress,
                shipping = shippingAddress,
                lineItems = lineItems,
                subtotal = subtotal,
                discount = discount,
                shippingFee = shippingFee,
                total = finalTotal,
                paymentMethod = s.paymentMethod,
                paymentTitle = paymentTitle,
                orderNotes = s.orderNotes
            )

            if (result.isSuccess) {
                val order = result.getOrNull()!!
                cartRepository.clearCart()
                _uiState.value = _uiState.value.copy(isSubmitting = false, orderCreated = order)
                onSuccess(order)
            } else {
                _uiState.value = _uiState.value.copy(
                    isSubmitting = false,
                    errorMessage = result.exceptionOrNull()?.message ?: "Order creation failed. Please try again."
                )
            }
        }
    }
}
