package com.example.util

import java.text.NumberFormat
import java.util.Locale

/**
 * Standard Indian Rupee (INR ₹) formatting utility.
 */
object CurrencyUtils {
    fun formatInr(amount: Double): String {
        return try {
            val format = NumberFormat.getCurrencyInstance(Locale("en", "IN"))
            format.maximumFractionDigits = 0
            val formatted = format.format(amount)
            if (formatted.startsWith("INR")) {
                formatted.replace("INR", "₹").trim()
            } else if (!formatted.contains("₹")) {
                "₹$formatted"
            } else {
                formatted
            }
        } catch (e: Exception) {
            "₹${amount.toLong()}"
        }
    }
}
