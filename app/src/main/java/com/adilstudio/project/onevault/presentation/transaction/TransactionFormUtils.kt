package com.adilstudio.project.onevault.presentation.transaction

import com.adilstudio.project.onevault.core.util.CurrencyFormatter
import com.adilstudio.project.onevault.domain.model.Currency

/**
 * Utility functions for TransactionForm operations
 */
object TransactionFormUtils {

    /**
     * Extracts numeric amount from scanned text
     */
    fun extractAmountFromText(text: String, currency: Currency): Double {
        // Remove common currency symbols and formatting
        val cleanText = text
            .replace(currency.symbol, "", ignoreCase = true)
            .replace(currency.code, "", ignoreCase = true)
            .replace(currency.thousandSeparator.toString(), "")
            .replace(currency.decimalSeparator.toString(), "")
            .replace(" ", "")

        // Extract numbers from the cleaned text
        val numbers = Regex("\\d+").findAll(cleanText).map { it.value }.toList()

        // Try to find the largest number (likely to be the amount)
        return numbers.maxOfOrNull { it.toLongOrNull() ?: 0L }?.toDouble() ?: 0.0
    }

    /**
     * Formats display values for form fields from scanned data
     */
    fun getFormattedAmountDisplay(amount: Double, currency: Currency): String {
        return if (amount > 0) {
            CurrencyFormatter.formatDisplay(amount.toLong(), currency)
        } else {
            ""
        }
    }
}
