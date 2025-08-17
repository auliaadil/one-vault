package com.adilstudio.project.onevault.core.util

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.Calendar

object DateUtil {
    
    private val isoFormatter = DateTimeFormatter.ISO_LOCAL_DATE // YYYY-MM-DD
    private val displayFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy") // 01 Jan 2024
    
    /**
     * Convert LocalDate to ISO string format (YYYY-MM-DD)
     */
    fun localDateToIsoString(date: LocalDate): String {
        return date.format(isoFormatter)
    }
    
    /**
     * Convert ISO string to LocalDate
     */
    fun isoStringToLocalDate(dateString: String): LocalDate? {
        return try {
            LocalDate.parse(dateString, isoFormatter)
        } catch (e: DateTimeParseException) {
            null
        }
    }
    
    /**
     * Format LocalDate for display (dd MMM yyyy)
     */
    fun formatDateForDisplay(date: LocalDate): String {
        return date.format(displayFormatter)
    }
    
    /**
     * Get current date as ISO string
     */
    fun getCurrentDateAsIsoString(): String {
        return localDateToIsoString(getCurrentDate())
    }
    
    /**
     * Get current date
     */
    fun getCurrentDate(): LocalDate {
        val calendar = Calendar.getInstance()
        return LocalDate.of(
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH) + 1, // Calendar months are 0-based
            calendar.get(Calendar.DAY_OF_MONTH)
        )
    }
}
