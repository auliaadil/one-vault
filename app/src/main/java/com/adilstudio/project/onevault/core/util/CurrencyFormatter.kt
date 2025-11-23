package com.adilstudio.project.onevault.core.util

import com.adilstudio.project.onevault.domain.model.Currency
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols

/**
 * Utility object for formatting amounts with currency
 */
object CurrencyFormatter {

    /**
     * Format amount with currency symbol
     * @param amount The amount to format
     * @param currency The currency to use
     * @param showSymbol Whether to show currency symbol (default: true)
     * @return Formatted string with currency
     */
    fun format(
        amount: Double,
        currency: Currency,
        showSymbol: Boolean = true
    ): String {
        val symbols = DecimalFormatSymbols().apply {
            decimalSeparator = currency.decimalSeparator
            groupingSeparator = currency.thousandSeparator
        }

        val pattern = if (currency == Currency.IDR) {
            // IDR doesn't use decimal places
            "#,##0"
        } else {
            // Other currencies use 2 decimal places
            "#,##0.00"
        }

        val formatter = DecimalFormat(pattern, symbols)
        val formattedAmount = formatter.format(amount)

        return if (showSymbol) {
            "${currency.symbol} $formattedAmount"
        } else {
            formattedAmount
        }
    }

    /**
     * Format amount with currency code instead of symbol
     * @param amount The amount to format
     * @param currency The currency to use
     * @return Formatted string with currency code
     */
    fun formatWithCode(
        amount: Double,
        currency: Currency
    ): String {
        val symbols = DecimalFormatSymbols().apply {
            decimalSeparator = currency.decimalSeparator
            groupingSeparator = currency.thousandSeparator
        }

        val pattern = if (currency == Currency.IDR) {
            "#,##0"
        } else {
            "#,##0.00"
        }

        val formatter = DecimalFormat(pattern, symbols)
        val formattedAmount = formatter.format(amount)

        return "$formattedAmount ${currency.code}"
    }

    /**
     * Parse formatted currency string to double
     * @param formattedAmount The formatted string to parse
     * @param currency The currency used in formatting
     * @return Parsed amount as Double
     */
    fun parse(
        formattedAmount: String,
        currency: Currency
    ): Double? {
        return try {
            // Remove currency symbol and code
            val cleanAmount = formattedAmount
                .replace(currency.symbol, "")
                .replace(currency.code, "")
                .trim()
                .replace(currency.thousandSeparator.toString(), "")
                .replace(currency.decimalSeparator, '.')

            cleanAmount.toDoubleOrNull()
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Extract numeric value from currency text
     * Removes all non-numeric characters except decimal separator
     * @param text The text containing currency amount
     * @param currency The currency to use for parsing
     * @return The numeric value as Long
     */
    fun extractNumber(text: String, currency: Currency): Long {
        return try {
            val cleanText = text
                .replace(currency.symbol, "")
                .replace(currency.code, "")
                .replace(currency.thousandSeparator.toString(), "")
                .replace(currency.decimalSeparator.toString(), "")
                .replace(Regex("[^0-9]"), "")
                .trim()

            cleanText.toLongOrNull() ?: 0L
        } catch (e: Exception) {
            0L
        }
    }

    /**
     * Format a Long amount as display format (without currency symbol)
     * @param amount The amount to format
     * @param currency The currency to use for formatting rules
     * @return Formatted string without currency symbol
     */
    fun formatDisplay(amount: Long, currency: Currency): String {
        val symbols = DecimalFormatSymbols().apply {
            decimalSeparator = currency.decimalSeparator
            groupingSeparator = currency.thousandSeparator
        }

        val pattern = if (currency == Currency.IDR) {
            "#,##0"
        } else {
            "#,##0.00"
        }

        val formatter = DecimalFormat(pattern, symbols)
        return formatter.format(amount)
    }

    /**
     * Parse display formatted text back to Long value
     * @param displayText The formatted display text
     * @param currency The currency used for formatting
     * @return Parsed amount as Long
     */
    fun parseDisplayToLong(displayText: String, currency: Currency): Long {
        return try {
            val cleanText = displayText
                .replace(currency.thousandSeparator.toString(), "")
                .replace(currency.decimalSeparator.toString(), "")
                .trim()

            cleanText.toLongOrNull() ?: 0L
        } catch (e: Exception) {
            0L
        }
    }

    /**
     * Validate if a text contains a valid currency amount
     * @param text The text to validate
     * @param currency The currency context for validation
     * @return True if valid, false otherwise
     */
    fun isValid(text: String, currency: Currency): Boolean {
        if (text.isBlank()) return false

        return try {
            val cleanText = text
                .replace(currency.symbol, "")
                .replace(currency.code, "")
                .replace(currency.thousandSeparator.toString(), "")
                .trim()

            // Check if it contains valid number with optional decimal separator
            val pattern = if (currency == Currency.IDR) {
                // IDR: only integers
                Regex("^[0-9]+$")
            } else {
                // Others: allow decimal
                val escapedSeparator = Regex.escape(currency.decimalSeparator.toString())
                Regex("^[0-9]+($escapedSeparator[0-9]{0,2})?$")
            }

            pattern.matches(cleanText)
        } catch (_: Exception) {
            false
        }
    }

    /**
     * Format amount with currency symbol prefix
     * @param amount The amount to format
     * @param currency The currency to use
     * @return Formatted string with currency symbol prefix
     */
    fun formatWithPrefix(amount: Long, currency: Currency): String {
        val symbols = DecimalFormatSymbols().apply {
            decimalSeparator = currency.decimalSeparator
            groupingSeparator = currency.thousandSeparator
        }

        val pattern = if (currency == Currency.IDR) {
            "#,##0"
        } else {
            "#,##0.00"
        }

        val formatter = DecimalFormat(pattern, symbols)
        val formattedAmount = formatter.format(amount)

        return "${currency.symbol} $formattedAmount"
    }
}
