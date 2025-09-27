package com.adilstudio.project.onevault.presentation.bill

import com.adilstudio.project.onevault.core.util.RupiahFormatter
import com.adilstudio.project.onevault.domain.model.Bill
import java.time.LocalDate

/**
 * Utility functions for BillForm operations
 */
object BillFormUtils {

    /**
     * Creates a Bill object with scanned data pre-populated
     */
    fun createBillFromScannedData(scannedData: ScannedBillData): Bill {
        // Extract amount from scanned text - look for numbers that could be amounts
        val amount = extractAmountFromText(scannedData.amount)

        return Bill(
            id = System.currentTimeMillis(),
            title = scannedData.title.ifBlank { "Scanned Bill" },
            category = null, // Let user select category
            amount = amount,
            vendor = scannedData.vendor,
            billDate = com.adilstudio.project.onevault.core.util.DateUtil.localDateToIsoString(LocalDate.now()),
            imagePath = null,
            accountId = null
        )
    }

    /**
     * Extracts numeric amount from scanned text
     */
    fun extractAmountFromText(text: String): Double {
        // Remove common currency symbols and formatting
        val cleanText = text
            .replace("Rp", "", ignoreCase = true)
            .replace("IDR", "", ignoreCase = true)
            .replace(".", "") // Remove thousand separators
            .replace(",", "") // Remove thousand separators
            .replace(" ", "")

        // Extract numbers from the cleaned text
        val numbers = Regex("\\d+").findAll(cleanText).map { it.value }.toList()

        // Try to find the largest number (likely to be the amount)
        return numbers.maxOfOrNull { it.toLongOrNull() ?: 0L }?.toDouble() ?: 0.0
    }

    /**
     * Formats display values for form fields from scanned data
     */
    fun getFormattedAmountDisplay(amount: Double): String {
        return if (amount > 0) {
            RupiahFormatter.formatRupiahDisplay(amount.toLong())
        } else {
            ""
        }
    }
}
