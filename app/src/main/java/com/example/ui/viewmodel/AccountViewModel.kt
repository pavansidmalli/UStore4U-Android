package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.UserPreferences
import com.example.data.model.Address
import com.example.data.model.Customer
import com.example.data.model.Order
import com.example.data.repository.CartRepository
import com.example.data.repository.OrderRepository
import com.example.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class AccountUiState(
    val isLoginMode: Boolean = true,
    val authChannel: String = "mobile", // "mobile", "email", "password", or "owner"
    val isOwnerTabSelected: Boolean = false,
    val mobileInput: String = "+1 (555) 392-1084",
    val emailInput: String = "alexander.wright@luxuryhorology.com",
    val passwordInput: String = "LuxuryPass2026!",
    val ownerPinInput: String = "",
    val ownerNameInput: String = "Store Director",
    val firstNameInput: String = "Alexander",
    val lastNameInput: String = "Wright",
    val otpCode: String = "",
    val generatedOtp: String = "849201",
    val isOtpSent: Boolean = false,
    val otpCountdown: Int = 0,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isAddressEditing: Boolean = false,
    val editingAddressType: String = "shipping", // "shipping" or "billing"
    val isApiSettingsOpen: Boolean = false,
    // Address Edit Form State
    val addressFirstName: String = "",
    val addressLastName: String = "",
    val addressCompany: String = "",
    val addressLine1: String = "",
    val addressLine2: String = "",
    val addressCity: String = "",
    val addressState: String = "",
    val addressPostcode: String = "",
    val addressCountry: String = "United States",
    val addressPhone: String = ""
)

class AccountViewModel(
    private val userRepository: UserRepository,
    private val cartRepository: CartRepository,
    private val orderRepository: OrderRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    val currentCustomer: StateFlow<Customer?> = userRepository.currentCustomer

    val orders: StateFlow<List<Order>> = orderRepository.orders

    val cartCount: StateFlow<Int> = cartRepository.cartCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val isLiveApiEnabled: StateFlow<Boolean> = userPreferences.isLiveApiEnabled
    val apiBaseUrl: StateFlow<String> = userPreferences.apiBaseUrl
    val consumerKey: StateFlow<String> = userPreferences.consumerKey
    val consumerSecret: StateFlow<String> = userPreferences.consumerSecret

    private val _uiState = MutableStateFlow(AccountUiState())
    val uiState: StateFlow<AccountUiState> = _uiState.asStateFlow()

    fun toggleAuthMode() {
        _uiState.value = _uiState.value.copy(
            isLoginMode = !_uiState.value.isLoginMode,
            isOtpSent = false,
            otpCode = "",
            errorMessage = null
        )
    }

    fun setAuthMode(isLogin: Boolean) {
        _uiState.value = _uiState.value.copy(
            isLoginMode = isLogin,
            isOtpSent = false,
            otpCode = "",
            errorMessage = null
        )
    }

    fun setAuthChannel(channel: String) {
        _uiState.value = _uiState.value.copy(
            authChannel = channel,
            isOtpSent = false,
            otpCode = "",
            errorMessage = null
        )
    }

    fun setOwnerTabSelected(selected: Boolean) {
        _uiState.value = _uiState.value.copy(
            isOwnerTabSelected = selected,
            authChannel = if (selected) "owner" else "mobile",
            isOtpSent = false,
            otpCode = "",
            errorMessage = null
        )
    }

    fun onOwnerPinChanged(pin: String) {
        if (pin.length <= 8) {
            _uiState.value = _uiState.value.copy(ownerPinInput = pin, errorMessage = null)
        }
    }

    fun onOwnerNameChanged(name: String) {
        _uiState.value = _uiState.value.copy(ownerNameInput = name, errorMessage = null)
    }

    fun onMobileChanged(v: String) { _uiState.value = _uiState.value.copy(mobileInput = v, errorMessage = null) }
    fun onEmailChanged(v: String) { _uiState.value = _uiState.value.copy(emailInput = v, errorMessage = null) }
    fun onPasswordChanged(v: String) { _uiState.value = _uiState.value.copy(passwordInput = v, errorMessage = null) }
    fun onFirstNameChanged(v: String) { _uiState.value = _uiState.value.copy(firstNameInput = v, errorMessage = null) }
    fun onLastNameChanged(v: String) { _uiState.value = _uiState.value.copy(lastNameInput = v, errorMessage = null) }
    fun onOtpCodeChanged(v: String) {
        if (v.length <= 6 && v.all { it.isDigit() }) {
            _uiState.value = _uiState.value.copy(otpCode = v, errorMessage = null)
        }
    }

    fun sendOtp() {
        val s = _uiState.value
        val identifier = if (s.authChannel == "mobile") s.mobileInput else s.emailInput
        if (identifier.isBlank()) {
            _uiState.value = _uiState.value.copy(
                errorMessage = "Please enter your ${if (s.authChannel == "mobile") "mobile number" else "email address"} to receive OTP."
            )
            return
        }

        if (!s.isLoginMode && s.firstNameInput.isBlank()) {
            _uiState.value = _uiState.value.copy(
                errorMessage = "Please provide your first name for registration."
            )
            return
        }

        // Generate a 6-digit OTP
        val newOtp = (100000..999999).random().toString()
        _uiState.value = _uiState.value.copy(
            generatedOtp = newOtp,
            otpCode = "",
            isOtpSent = true,
            otpCountdown = 30,
            errorMessage = null
        )
    }

    fun resendOtp() {
        val newOtp = (100000..999999).random().toString()
        _uiState.value = _uiState.value.copy(
            generatedOtp = newOtp,
            otpCode = "",
            otpCountdown = 30,
            errorMessage = null
        )
    }

    fun backToIdentifierInput() {
        _uiState.value = _uiState.value.copy(
            isOtpSent = false,
            otpCode = "",
            errorMessage = null
        )
    }

    fun quickFillDemo(isVIP: Boolean = true) {
        if (isVIP) {
            _uiState.value = _uiState.value.copy(
                mobileInput = "+1 (555) 392-1084",
                emailInput = "alexander.wright@luxuryhorology.com",
                passwordInput = "LuxuryVIP2026!",
                firstNameInput = "Alexander",
                lastNameInput = "Wright",
                errorMessage = null
            )
        } else {
            _uiState.value = _uiState.value.copy(
                mobileInput = "+91 98765 43210",
                emailInput = "elena.rostova@ustore4u.com",
                passwordInput = "Timepiece123!",
                firstNameInput = "Elena",
                lastNameInput = "Rostova",
                errorMessage = null
            )
        }
    }

    fun submitOwnerLogin() {
        val s = _uiState.value
        if (s.ownerPinInput.isBlank()) {
            _uiState.value = _uiState.value.copy(errorMessage = "Please enter the Store Owner Security PIN.")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            val res = userRepository.loginAsOwner(
                securityPin = s.ownerPinInput,
                ownerName = s.ownerNameInput.ifBlank { "Store Director" }
            )
            if (res.isSuccess) {
                _uiState.value = _uiState.value.copy(isLoading = false, ownerPinInput = "", errorMessage = null)
            } else {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = res.exceptionOrNull()?.message ?: "Owner authentication failed. Please check the PIN."
                )
            }
        }
    }

    fun verifyOtpAndAuthenticate() {
        val s = _uiState.value
        if (s.otpCode.isBlank() || s.otpCode.length < 4) {
            _uiState.value = _uiState.value.copy(errorMessage = "Please enter the valid 6-digit OTP code.")
            return
        }

        // Allow matching generated OTP, or standard test codes 123456 / 849201
        val isValidOtp = s.otpCode == s.generatedOtp || s.otpCode == "123456" || s.otpCode == "849201"
        if (!isValidOtp) {
            _uiState.value = _uiState.value.copy(errorMessage = "Incorrect OTP code. Please enter the ${s.generatedOtp} shown above.")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            val isMobile = s.authChannel == "mobile"
            val identifier = if (isMobile) s.mobileInput else s.emailInput

            val res = if (s.isLoginMode) {
                userRepository.loginWithOtp(identifier, isMobile)
            } else {
                userRepository.registerWithOtp(s.firstNameInput, s.lastNameInput, identifier, isMobile)
            }

            if (res.isSuccess) {
                _uiState.value = _uiState.value.copy(isLoading = false, isOtpSent = false, otpCode = "", errorMessage = null)
            } else {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = res.exceptionOrNull()?.message ?: "Authentication failed."
                )
            }
        }
    }

    fun submitAuth() {
        val s = _uiState.value
        if (s.isOwnerTabSelected || s.authChannel == "owner") {
            submitOwnerLogin()
            return
        }

        if (s.authChannel != "password") {
            if (!s.isOtpSent) {
                sendOtp()
            } else {
                verifyOtpAndAuthenticate()
            }
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            val res = if (s.isLoginMode) {
                userRepository.login(s.emailInput, s.passwordInput)
            } else {
                userRepository.register(s.firstNameInput, s.lastNameInput, s.emailInput, s.passwordInput)
            }

            if (res.isSuccess) {
                _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = null)
            } else {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = res.exceptionOrNull()?.message ?: "Authentication failed."
                )
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            userRepository.logout()
        }
    }

    fun setLiveApiEnabled(enabled: Boolean) {
        userPreferences.setUseLiveApi(enabled)
    }

    fun updateApiConfig(url: String, key: String, secret: String) {
        userPreferences.setCustomApiBaseUrl(url)
        userPreferences.setCustomConsumerKey(key)
        userPreferences.setCustomConsumerSecret(secret)
    }

    fun setApiSettingsOpen(open: Boolean) {
        _uiState.value = _uiState.value.copy(isApiSettingsOpen = open)
    }

    fun startEditingAddress(type: String) {
        val cust = currentCustomer.value
        val currentAddress = if (type == "billing") cust?.billing ?: Address() else cust?.shipping ?: Address()

        _uiState.value = _uiState.value.copy(
            isAddressEditing = true,
            editingAddressType = type,
            addressFirstName = currentAddress.firstName.ifBlank { cust?.firstName ?: "" },
            addressLastName = currentAddress.lastName.ifBlank { cust?.lastName ?: "" },
            addressCompany = currentAddress.company,
            addressLine1 = currentAddress.address1.ifBlank { "742 Evergreen Terrace" },
            addressLine2 = currentAddress.address2,
            addressCity = currentAddress.city.ifBlank { "Beverly Hills" },
            addressState = currentAddress.state.ifBlank { "CA" },
            addressPostcode = currentAddress.postcode.ifBlank { "90210" },
            addressCountry = currentAddress.country.ifBlank { "United States" },
            addressPhone = currentAddress.phone.ifBlank { "+1 (555) 392-1084" }
        )
    }

    fun dismissAddressEditing() {
        _uiState.value = _uiState.value.copy(isAddressEditing = false)
    }

    fun updateAddressFormField(
        firstName: String? = null,
        lastName: String? = null,
        company: String? = null,
        line1: String? = null,
        line2: String? = null,
        city: String? = null,
        state: String? = null,
        postcode: String? = null,
        country: String? = null,
        phone: String? = null
    ) {
        _uiState.value = _uiState.value.copy(
            addressFirstName = firstName ?: _uiState.value.addressFirstName,
            addressLastName = lastName ?: _uiState.value.addressLastName,
            addressCompany = company ?: _uiState.value.addressCompany,
            addressLine1 = line1 ?: _uiState.value.addressLine1,
            addressLine2 = line2 ?: _uiState.value.addressLine2,
            addressCity = city ?: _uiState.value.addressCity,
            addressState = state ?: _uiState.value.addressState,
            addressPostcode = postcode ?: _uiState.value.addressPostcode,
            addressCountry = country ?: _uiState.value.addressCountry,
            addressPhone = phone ?: _uiState.value.addressPhone
        )
    }

    fun saveAddressForm() {
        val s = _uiState.value
        val cust = currentCustomer.value ?: return

        val newAddress = Address(
            firstName = s.addressFirstName,
            lastName = s.addressLastName,
            company = s.addressCompany,
            address1 = s.addressLine1,
            address2 = s.addressLine2,
            city = s.addressCity,
            state = s.addressState,
            postcode = s.addressPostcode,
            country = s.addressCountry,
            email = cust.email,
            phone = s.addressPhone
        )

        viewModelScope.launch {
            if (s.editingAddressType == "billing") {
                userRepository.updateAddress(billing = newAddress, shipping = cust.shipping)
            } else {
                userRepository.updateAddress(billing = cust.billing, shipping = newAddress)
            }
            _uiState.value = _uiState.value.copy(isAddressEditing = false)
        }
    }
}
