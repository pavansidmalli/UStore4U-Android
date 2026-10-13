package com.example.data.local

import android.content.Context
import android.content.SharedPreferences
import com.example.data.model.Address
import com.example.data.model.Customer
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class UserPreferences(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("ustore4u_user_prefs", Context.MODE_PRIVATE)

    private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    private val customerAdapter = moshi.adapter(Customer::class.java)
    private val addressAdapter = moshi.adapter(Address::class.java)

    private val _onboardingCompleted = MutableStateFlow(isOnboardingCompleted())
    val onboardingCompleted: StateFlow<Boolean> = _onboardingCompleted.asStateFlow()

    private val _currentCustomer = MutableStateFlow(getSavedCustomer())
    val currentCustomer: StateFlow<Customer?> = _currentCustomer.asStateFlow()

    private val _recentSearches = MutableStateFlow(getSavedRecentSearches())
    val recentSearches: StateFlow<List<String>> = _recentSearches.asStateFlow()

    private val _apiBaseUrl = MutableStateFlow(getCustomApiBaseUrl())
    val apiBaseUrl: StateFlow<String> = _apiBaseUrl.asStateFlow()

    private val _consumerKey = MutableStateFlow(getCustomConsumerKey())
    val consumerKey: StateFlow<String> = _consumerKey.asStateFlow()

    private val _consumerSecret = MutableStateFlow(getCustomConsumerSecret())
    val consumerSecret: StateFlow<String> = _consumerSecret.asStateFlow()

    private val _isLiveApiEnabled = MutableStateFlow(isLiveApiActive())
    val isLiveApiEnabled: StateFlow<Boolean> = _isLiveApiEnabled.asStateFlow()

    fun isOnboardingCompleted(): Boolean = prefs.getBoolean(KEY_ONBOARDING_DONE, false)

    fun setOnboardingCompleted(completed: Boolean) {
        prefs.edit().putBoolean(KEY_ONBOARDING_DONE, completed).apply()
        _onboardingCompleted.value = completed
    }

    fun getSavedCustomer(): Customer? {
        val json = prefs.getString(KEY_CUSTOMER_DATA, null) ?: return null
        return try {
            customerAdapter.fromJson(json)
        } catch (e: Exception) {
            null
        }
    }

    fun saveCustomer(customer: Customer?) {
        if (customer == null) {
            prefs.edit().remove(KEY_CUSTOMER_DATA).remove(KEY_AUTH_TOKEN).apply()
            _currentCustomer.value = null
        } else {
            val json = customerAdapter.toJson(customer)
            prefs.edit().putString(KEY_CUSTOMER_DATA, json).apply()
            _currentCustomer.value = customer
        }
    }

    fun getAuthToken(): String? = prefs.getString(KEY_AUTH_TOKEN, null)

    fun saveAuthToken(token: String?) {
        if (token == null) {
            prefs.edit().remove(KEY_AUTH_TOKEN).apply()
        } else {
            prefs.edit().putString(KEY_AUTH_TOKEN, token).apply()
        }
    }

    fun getSavedRecentSearches(): List<String> {
        val set = prefs.getStringSet(KEY_RECENT_SEARCHES, emptySet()) ?: emptySet()
        return set.toList().take(8)
    }

    fun addRecentSearch(query: String) {
        if (query.isBlank()) return
        val current = getSavedRecentSearches().toMutableList()
        current.remove(query)
        current.add(0, query)
        val updated = current.take(8)
        prefs.edit().putStringSet(KEY_RECENT_SEARCHES, updated.toSet()).apply()
        _recentSearches.value = updated
    }

    fun clearRecentSearches() {
        prefs.edit().remove(KEY_RECENT_SEARCHES).apply()
        _recentSearches.value = emptyList()
    }

    // WooCommerce API connection configuration settings
    fun isLiveApiActive(): Boolean = prefs.getBoolean(KEY_USE_LIVE_API, false)

    fun setUseLiveApi(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_USE_LIVE_API, enabled).apply()
        _isLiveApiEnabled.value = enabled
    }

    fun getCustomApiBaseUrl(): String = prefs.getString(KEY_CUSTOM_API_URL, "https://ustore4u.com/") ?: "https://ustore4u.com/"

    fun setCustomApiBaseUrl(url: String) {
        val cleanUrl = if (!url.endsWith("/")) "$url/" else url
        prefs.edit().putString(KEY_CUSTOM_API_URL, cleanUrl).apply()
        _apiBaseUrl.value = cleanUrl
    }

    fun getCustomConsumerKey(): String = prefs.getString(KEY_CONSUMER_KEY, "") ?: ""

    fun setCustomConsumerKey(key: String) {
        prefs.edit().putString(KEY_CONSUMER_KEY, key).apply()
        _consumerKey.value = key
    }

    fun getCustomConsumerSecret(): String = prefs.getString(KEY_CONSUMER_SECRET, "") ?: ""

    fun setCustomConsumerSecret(secret: String) {
        prefs.edit().putString(KEY_CONSUMER_SECRET, secret).apply()
        _consumerSecret.value = secret
    }

    // Notification preferences
    fun isNotificationsEnabled(): Boolean = prefs.getBoolean(KEY_NOTIF_ENABLED, true)
    fun setNotificationsEnabled(enabled: Boolean) = prefs.edit().putBoolean(KEY_NOTIF_ENABLED, enabled).apply()

    fun isOrderUpdatesEnabled(): Boolean = prefs.getBoolean(KEY_NOTIF_ORDERS, true)
    fun setOrderUpdatesEnabled(enabled: Boolean) = prefs.edit().putBoolean(KEY_NOTIF_ORDERS, enabled).apply()

    fun isOffersEnabled(): Boolean = prefs.getBoolean(KEY_NOTIF_OFFERS, true)
    fun setOffersEnabled(enabled: Boolean) = prefs.edit().putBoolean(KEY_NOTIF_OFFERS, enabled).apply()

    // Security & Privacy Shield Preferences
    private val _isScreenProtectionEnabled = MutableStateFlow(isScreenProtectionActive())
    val isScreenProtectionEnabled: StateFlow<Boolean> = _isScreenProtectionEnabled.asStateFlow()

    fun isScreenProtectionActive(): Boolean = prefs.getBoolean(KEY_SCREEN_PROTECTION, false)
    fun setScreenProtection(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_SCREEN_PROTECTION, enabled).apply()
        _isScreenProtectionEnabled.value = enabled
    }

    fun isBiometricLockActive(): Boolean = prefs.getBoolean(KEY_BIOMETRIC_LOCK, false)
    fun setBiometricLock(enabled: Boolean) = prefs.edit().putBoolean(KEY_BIOMETRIC_LOCK, enabled).apply()

    fun isBiometricCheckoutActive(): Boolean = prefs.getBoolean(KEY_BIOMETRIC_CHECKOUT, true)
    fun setBiometricCheckout(enabled: Boolean) = prefs.edit().putBoolean(KEY_BIOMETRIC_CHECKOUT, enabled).apply()

    // Store Owner / Admin Security PIN for REST API Access
    fun getAdminPin(): String = prefs.getString(KEY_ADMIN_PIN, DEFAULT_ADMIN_PIN) ?: DEFAULT_ADMIN_PIN
    fun setAdminPin(pin: String) = prefs.edit().putString(KEY_ADMIN_PIN, pin).apply()
    fun verifyAdminPin(pin: String): Boolean = getAdminPin() == pin.trim()

    fun clearAllSensitiveData() {
        prefs.edit()
            .remove(KEY_CUSTOMER_DATA)
            .remove(KEY_AUTH_TOKEN)
            .remove(KEY_RECENT_SEARCHES)
            .apply()
        _currentCustomer.value = null
        _recentSearches.value = emptyList()
    }

    companion object {
        private const val KEY_ONBOARDING_DONE = "onboarding_done"
        private const val KEY_CUSTOMER_DATA = "customer_data"
        private const val KEY_AUTH_TOKEN = "auth_token"
        private const val KEY_RECENT_SEARCHES = "recent_searches"
        private const val KEY_USE_LIVE_API = "use_live_api"
        private const val KEY_CUSTOM_API_URL = "custom_api_url"
        private const val KEY_CONSUMER_KEY = "consumer_key"
        private const val KEY_CONSUMER_SECRET = "consumer_secret"
        private const val KEY_NOTIF_ENABLED = "notif_enabled"
        private const val KEY_NOTIF_ORDERS = "notif_orders"
        private const val KEY_NOTIF_OFFERS = "notif_offers"
        private const val KEY_SCREEN_PROTECTION = "screen_protection_flag_secure"
        private const val KEY_BIOMETRIC_LOCK = "biometric_app_lock"
        private const val KEY_BIOMETRIC_CHECKOUT = "biometric_checkout"
        private const val KEY_ADMIN_PIN = "store_admin_pin"
        private const val DEFAULT_ADMIN_PIN = "7492"
    }
}
