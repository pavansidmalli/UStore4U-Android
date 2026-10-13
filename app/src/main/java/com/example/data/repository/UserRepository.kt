package com.example.data.repository

import com.example.data.api.NetworkClient
import com.example.data.local.UserPreferences
import com.example.data.model.Address
import com.example.data.model.Customer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext

class UserRepository(
    private val networkClient: NetworkClient,
    private val userPreferences: UserPreferences
) {
    val currentCustomer: StateFlow<Customer?> = userPreferences.currentCustomer

    suspend fun login(email: String, password: String):Result<Customer> = withContext(Dispatchers.IO) {
        if (email.isBlank() || password.isBlank()) {
            return@withContext Result.failure(IllegalArgumentException("Please enter your email and password"))
        }

        // Demo / Fallback login simulation
        val nameParts = email.substringBefore("@").split(".", "_", "-")
        val firstName = nameParts.firstOrNull()?.replaceFirstChar { it.uppercase() } ?: "Valued"
        val lastName = nameParts.getOrNull(1)?.replaceFirstChar { it.uppercase() } ?: "Member"

        val customer = Customer(
            id = (1000..9999).random().toLong(),
            email = email,
            firstName = firstName,
            lastName = lastName,
            username = email.substringBefore("@"),
            billing = Address(
                firstName = firstName,
                lastName = lastName,
                email = email,
                address1 = "450 Luxury Avenue",
                city = "New York",
                state = "NY",
                postcode = "10001",
                country = "United States",
                phone = "+1 (212) 555-0199"
            ),
            shipping = Address(
                firstName = firstName,
                lastName = lastName,
                address1 = "450 Luxury Avenue",
                city = "New York",
                state = "NY",
                postcode = "10001",
                country = "United States"
            ),
            avatarUrl = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=400&auto=format&fit=crop&q=80"
        )

        userPreferences.saveCustomer(customer)
        userPreferences.saveAuthToken("token_u4u_" + System.currentTimeMillis())
        Result.success(customer)
    }

    suspend fun register(
        firstName: String,
        lastName: String,
        email: String,
        password: String
    ): Result<Customer> = withContext(Dispatchers.IO) {
        if (firstName.isBlank() || email.isBlank() || password.isBlank()) {
            return@withContext Result.failure(IllegalArgumentException("Please fill all required fields"))
        }

        val customer = Customer(
            id = (1000..9999).random().toLong(),
            email = email,
            firstName = firstName,
            lastName = lastName,
            username = email.substringBefore("@"),
            billing = Address(
                firstName = firstName,
                lastName = lastName,
                email = email,
                country = "United States"
            ),
            shipping = Address(
                firstName = firstName,
                lastName = lastName,
                country = "United States"
            )
        )

        userPreferences.saveCustomer(customer)
        userPreferences.saveAuthToken("token_u4u_" + System.currentTimeMillis())
        Result.success(customer)
    }

    suspend fun loginWithOtp(
        identifier: String, // phone or email
        isMobile: Boolean
    ): Result<Customer> = withContext(Dispatchers.IO) {
        if (identifier.isBlank()) {
            return@withContext Result.failure(IllegalArgumentException("Please enter your ${if (isMobile) "mobile number" else "email address"}"))
        }

        val email = if (isMobile) {
            val cleanPhone = identifier.filter { it.isDigit() }
            "user_$cleanPhone@client.ustore4u.com"
        } else {
            identifier
        }

        val nameParts = if (isMobile) listOf("VIP", "Client") else email.substringBefore("@").split(".", "_", "-")
        val firstName = nameParts.firstOrNull()?.replaceFirstChar { it.uppercase() } ?: "VIP"
        val lastName = nameParts.getOrNull(1)?.replaceFirstChar { it.uppercase() } ?: "Collector"

        val customer = Customer(
            id = (1000..9999).random().toLong(),
            email = email,
            firstName = firstName,
            lastName = lastName,
            username = if (isMobile) "user_${identifier.filter { it.isDigit() }}" else email.substringBefore("@"),
            billing = Address(
                firstName = firstName,
                lastName = lastName,
                email = email,
                address1 = "742 Heritage Boulevard",
                city = "New York",
                state = "NY",
                postcode = "10001",
                country = "United States",
                phone = if (isMobile) identifier else "+1 (555) 392-1084"
            ),
            shipping = Address(
                firstName = firstName,
                lastName = lastName,
                address1 = "742 Heritage Boulevard",
                city = "New York",
                state = "NY",
                postcode = "10001",
                country = "United States",
                phone = if (isMobile) identifier else "+1 (555) 392-1084"
            ),
            avatarUrl = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=400&auto=format&fit=crop&q=80"
        )

        userPreferences.saveCustomer(customer)
        userPreferences.saveAuthToken("token_u4u_" + System.currentTimeMillis())
        Result.success(customer)
    }

    suspend fun registerWithOtp(
        firstName: String,
        lastName: String,
        identifier: String,
        isMobile: Boolean
    ): Result<Customer> = withContext(Dispatchers.IO) {
        if (firstName.isBlank() || identifier.isBlank()) {
            return@withContext Result.failure(IllegalArgumentException("Please complete your name and ${if (isMobile) "mobile number" else "email"}"))
        }

        val email = if (isMobile) {
            val cleanPhone = identifier.filter { it.isDigit() }
            "user_$cleanPhone@client.ustore4u.com"
        } else {
            identifier
        }

        val customer = Customer(
            id = (1000..9999).random().toLong(),
            email = email,
            firstName = firstName,
            lastName = lastName.ifBlank { "Member" },
            username = if (isMobile) "user_${identifier.filter { it.isDigit() }}" else email.substringBefore("@"),
            billing = Address(
                firstName = firstName,
                lastName = lastName,
                email = email,
                country = "United States",
                phone = if (isMobile) identifier else ""
            ),
            shipping = Address(
                firstName = firstName,
                lastName = lastName,
                country = "United States",
                phone = if (isMobile) identifier else ""
            )
        )

        userPreferences.saveCustomer(customer)
        userPreferences.saveAuthToken("token_u4u_" + System.currentTimeMillis())
        Result.success(customer)
    }

    suspend fun loginAsOwner(securityPin: String, ownerName: String = "Store Director"): Result<Customer> = withContext(Dispatchers.IO) {
        if (securityPin.isBlank()) {
            return@withContext Result.failure(IllegalArgumentException("Please enter the Store Owner Security PIN"))
        }

        if (!userPreferences.verifyAdminPin(securityPin)) {
            return@withContext Result.failure(IllegalArgumentException("Invalid Store Owner Security PIN. Access denied."))
        }

        val nameParts = ownerName.trim().split(" ")
        val firstName = nameParts.firstOrNull() ?: "Store"
        val lastName = nameParts.drop(1).joinToString(" ").ifBlank { "Owner" }

        val ownerCustomer = Customer(
            id = 1L,
            email = "owner@ustore4u.com",
            firstName = firstName,
            lastName = lastName,
            username = "store_owner",
            role = "owner",
            billing = Address(
                firstName = firstName,
                lastName = lastName,
                email = "owner@ustore4u.com",
                company = "U Store Flagship Horology",
                address1 = "Kukatpally Flagship HQ",
                city = "Hyderabad",
                state = "Telangana",
                postcode = "500072",
                country = "India",
                phone = "+91 80080 07492"
            ),
            shipping = Address(
                firstName = firstName,
                lastName = lastName,
                company = "U Store Flagship Horology",
                address1 = "Kukatpally Flagship HQ",
                city = "Hyderabad",
                state = "Telangana",
                postcode = "500072",
                country = "India",
                phone = "+91 80080 07492"
            ),
            avatarUrl = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=400&auto=format&fit=crop&q=80"
        )

        userPreferences.saveCustomer(ownerCustomer)
        userPreferences.saveAuthToken("token_owner_" + System.currentTimeMillis())
        Result.success(ownerCustomer)
    }

    suspend fun logout() = withContext(Dispatchers.IO) {
        userPreferences.saveCustomer(null)
        userPreferences.saveAuthToken(null)
    }

    suspend fun updateAddress(billing: Address, shipping: Address) = withContext(Dispatchers.IO) {
        val current = userPreferences.getSavedCustomer()
        if (current != null) {
            val updated = current.copy(billing = billing, shipping = shipping)
            userPreferences.saveCustomer(updated)
        }
    }
}
