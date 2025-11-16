package com.adilstudio.project.onevault.core.util

import com.adilstudio.project.onevault.domain.model.Currency

/**
 * Utility class for handling Indonesian Rupiah formatting and text processing
 * @deprecated Use CurrencyFormatter instead for multi-currency support
 */
@Deprecated("Use CurrencyFormatter for multi-currency support")
object RupiahFormatter {

    /**
     * Extracts numeric value from Indonesian Rupiah text
     * @deprecated Use CurrencyFormatter.extractNumber with Currency parameter
     */
    @Deprecated("Use CurrencyFormatter.extractNumber", ReplaceWith("CurrencyFormatter.extractNumber(text, Currency.IDR)"))
    fun extractNumberFromRupiahText(text: String): Long {
        return CurrencyFormatter.extractNumber(text, Currency.IDR)
    }

    /**
     * Formats a Long amount as Indonesian Rupiah display format
     * @deprecated Use CurrencyFormatter.formatDisplay with Currency parameter
     */
    @Deprecated("Use CurrencyFormatter.formatDisplay", ReplaceWith("CurrencyFormatter.formatDisplay(amount, Currency.IDR)"))
    fun formatRupiahDisplay(amount: Long): String {
        return CurrencyFormatter.formatDisplay(amount, Currency.IDR)
    }

    /**
     * Parses a display formatted text back to Long value
     * @deprecated Use CurrencyFormatter.parseDisplayToLong with Currency parameter
     */
    @Deprecated("Use CurrencyFormatter.parseDisplayToLong", ReplaceWith("CurrencyFormatter.parseDisplayToLong(displayText, Currency.IDR)"))
    fun parseDisplayToLong(displayText: String): Long {
        return CurrencyFormatter.parseDisplayToLong(displayText, Currency.IDR)
    }

    /**
     * Validates if a text contains a valid Rupiah amount
     * @deprecated Use CurrencyFormatter.isValid with Currency parameter
     */
    @Deprecated("Use CurrencyFormatter.isValid", ReplaceWith("CurrencyFormatter.isValid(text, Currency.IDR)"))
    fun isValidRupiahText(text: String): Boolean {
        return CurrencyFormatter.isValid(text, Currency.IDR)
    }

    /**
     * Formats amount with "Rp" prefix for display
     * @deprecated Use CurrencyFormatter.formatWithPrefix with Currency parameter
     */
    @Deprecated("Use CurrencyFormatter.formatWithPrefix", ReplaceWith("CurrencyFormatter.formatWithPrefix(amount, Currency.IDR)"))
    fun formatWithRupiahPrefix(amount: Long): String {
        return CurrencyFormatter.formatWithPrefix(amount, Currency.IDR)
    }
}
