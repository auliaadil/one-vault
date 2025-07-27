package com.adilstudio.project.onevault.core.util

import java.text.NumberFormat
import java.util.Locale

/**
 * Utility class for handling Indonesian Rupiah formatting and text processing
 */
object RupiahFormatter {

    /**
     * Extracts numeric value from Indonesian Rupiah text
     * Handles various formats like:
     * - "RP 105.000" -> 105000
     * - "Rp 105.000,00" -> 105000
     * - "RP105.000" -> 105000
     * - "105.000" -> 105000
     *
     * @param text The text containing Rupiah amount
     * @return Long value extracted from the text, 0 if parsing fails
     */
    fun extractNumberFromRupiahText(text: String): Long {
        // Remove common prefixes and suffixes
        val cleaned = text.uppercase()
            .replace("RP", "")
            .replace("IDR", "")
            .replace("RUPIAH", "")
            .trim()

        // Remove all non-digit characters except dots and commas
        val numbersOnly = cleaned.replace(Regex("[^0-9.,]"), "")

        // Handle Indonesian formatting: dots as thousand separators, commas as decimal
        // Convert to standard format by removing thousand separators and handling decimals
        val standardFormat = if (numbersOnly.contains(",")) {
            // If there's a comma, treat everything before the first comma as the integer part
            // This handles malformed cases like "1.000,00,00" by taking only "1.000"
            val integerPart = numbersOnly.substringBefore(",")
            integerPart.replace(".", "")
        } else {
            // No comma, so dots are thousand separators
            numbersOnly.replace(".", "")
        }

        return standardFormat.toLongOrNull() ?: 0L
    }

    /**
     * Formats a Long amount as Indonesian Rupiah display format
     * Uses Indonesian locale for thousand separators (dots)
     *
     * @param amount The amount to format
     * @return Formatted string with Indonesian thousand separators, empty string if amount is 0
     */
    fun formatRupiahDisplay(amount: Long): String {
        if (amount == 0L) return ""
        val formatter = NumberFormat.getNumberInstance(Locale.forLanguageTag("id-ID"))
        return formatter.format(amount)
    }

    /**
     * Parses a display formatted text back to Long value
     * Removes thousand separators (dots) from Indonesian format
     *
     * @param displayText The formatted display text
     * @return Long value parsed from display text, 0 if parsing fails
     */
    fun parseDisplayToLong(displayText: String): Long {
        if (displayText.isEmpty()) return 0L
        val numbersOnly = displayText.replace(".", "")
        return numbersOnly.toLongOrNull() ?: 0L
    }

    /**
     * Validates if a text contains a valid Rupiah amount
     *
     * @param text The text to validate
     * @return true if the text contains a valid amount, false otherwise
     */
    fun isValidRupiahText(text: String): Boolean {
        return extractNumberFromRupiahText(text) > 0L
    }

    /**
     * Formats amount with "Rp" prefix for display
     *
     * @param amount The amount to format
     * @return Formatted string with "Rp" prefix and Indonesian formatting
     */
    fun formatWithRupiahPrefix(amount: Long): String {
        if (amount == 0L) return "Rp 0"
        return "Rp ${formatRupiahDisplay(amount)}"
    }
}
