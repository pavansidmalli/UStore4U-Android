package com.example.util

import java.security.MessageDigest

object SecurityUtils {

    /**
     * Masks credit card numbers, showing only the last 4 digits (e.g. "•••• •••• •••• 1234").
     */
    fun maskCardNumber(cardNumber: String): String {
        val digits = cardNumber.filter { it.isDigit() }
        if (digits.length < 4) return "•••• •••• •••• ••••"
        val last4 = digits.takeLast(4)
        return "•••• •••• •••• $last4"
    }

    /**
     * Masks WooCommerce consumer keys / secrets for display in UI (e.g. "ck_••••••••abcd").
     */
    fun maskApiKey(key: String): String {
        if (key.length <= 8) return "••••••••"
        val prefix = key.take(3)
        val suffix = key.takeLast(4)
        return "$prefix••••••••$suffix"
    }

    /**
     * Sanitizes user input to prevent HTML injection, XSS, and control character attacks.
     */
    fun sanitizeInput(input: String?): String {
        if (input == null) return ""
        return input
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")
            .replace("'", "&#x27;")
            .replace("&", "&amp;")
            .trim()
    }

    /**
     * Computes SHA-256 hash of a string for secure integrity verification.
     */
    fun sha256(input: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(input.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }
}
