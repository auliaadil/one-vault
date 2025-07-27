package com.adilstudio.project.onevault.core.util

import org.junit.Assert.*
import org.junit.Test

/**
 * Unit tests for RupiahFormatter utility class
 */
class RupiahFormatterTest {

    @Test
    fun `extractNumberFromRupiahText should handle RP prefix with spaces`() {
        val result = RupiahFormatter.extractNumberFromRupiahText("RP 105.000")
        assertEquals(105000L, result)
    }

    @Test
    fun `extractNumberFromRupiahText should handle Rp prefix with decimals`() {
        val result = RupiahFormatter.extractNumberFromRupiahText("Rp 105.000,00")
        assertEquals(105000L, result)
    }

    @Test
    fun `extractNumberFromRupiahText should handle RP prefix without spaces`() {
        val result = RupiahFormatter.extractNumberFromRupiahText("RP105.000")
        assertEquals(105000L, result)
    }

    @Test
    fun `extractNumberFromRupiahText should handle plain number with thousand separators`() {
        val result = RupiahFormatter.extractNumberFromRupiahText("105.000")
        assertEquals(105000L, result)
    }

    @Test
    fun `extractNumberFromRupiahText should handle IDR prefix`() {
        val result = RupiahFormatter.extractNumberFromRupiahText("IDR 250.500")
        assertEquals(250500L, result)
    }

    @Test
    fun `extractNumberFromRupiahText should handle RUPIAH prefix`() {
        val result = RupiahFormatter.extractNumberFromRupiahText("RUPIAH 1.000.000")
        assertEquals(1000000L, result)
    }

    @Test
    fun `extractNumberFromRupiahText should handle lowercase prefixes`() {
        val result = RupiahFormatter.extractNumberFromRupiahText("rp 75.000")
        assertEquals(75000L, result)
    }

    @Test
    fun `extractNumberFromRupiahText should handle numbers with multiple decimal places`() {
        val result = RupiahFormatter.extractNumberFromRupiahText("Rp 1.250.750,50")
        assertEquals(1250750L, result)
    }

    @Test
    fun `extractNumberFromRupiahText should handle plain number without separators`() {
        val result = RupiahFormatter.extractNumberFromRupiahText("500000")
        assertEquals(500000L, result)
    }

    @Test
    fun `extractNumberFromRupiahText should handle empty string`() {
        val result = RupiahFormatter.extractNumberFromRupiahText("")
        assertEquals(0L, result)
    }

    @Test
    fun `extractNumberFromRupiahText should handle invalid text`() {
        val result = RupiahFormatter.extractNumberFromRupiahText("invalid text")
        assertEquals(0L, result)
    }

    @Test
    fun `extractNumberFromRupiahText should handle text with only letters`() {
        val result = RupiahFormatter.extractNumberFromRupiahText("ABC XYZ")
        assertEquals(0L, result)
    }

    @Test
    fun `extractNumberFromRupiahText should handle mixed text with numbers`() {
        val result = RupiahFormatter.extractNumberFromRupiahText("Total: RP 150.000 only")
        assertEquals(150000L, result)
    }

    @Test
    fun `formatRupiahDisplay should format zero as empty string`() {
        val result = RupiahFormatter.formatRupiahDisplay(0L)
        assertEquals("", result)
    }

    @Test
    fun `formatRupiahDisplay should format small numbers without separators`() {
        val result = RupiahFormatter.formatRupiahDisplay(999L)
        assertEquals("999", result)
    }

    @Test
    fun `formatRupiahDisplay should format thousands with dots`() {
        val result = RupiahFormatter.formatRupiahDisplay(1000L)
        assertEquals("1.000", result)
    }

    @Test
    fun `formatRupiahDisplay should format large numbers with multiple dots`() {
        val result = RupiahFormatter.formatRupiahDisplay(1234567L)
        assertEquals("1.234.567", result)
    }

    @Test
    fun `formatRupiahDisplay should format millions correctly`() {
        val result = RupiahFormatter.formatRupiahDisplay(2500000L)
        assertEquals("2.500.000", result)
    }

    @Test
    fun `parseDisplayToLong should handle empty string`() {
        val result = RupiahFormatter.parseDisplayToLong("")
        assertEquals(0L, result)
    }

    @Test
    fun `parseDisplayToLong should handle plain numbers`() {
        val result = RupiahFormatter.parseDisplayToLong("1000")
        assertEquals(1000L, result)
    }

    @Test
    fun `parseDisplayToLong should handle formatted numbers with dots`() {
        val result = RupiahFormatter.parseDisplayToLong("1.250.000")
        assertEquals(1250000L, result)
    }

    @Test
    fun `parseDisplayToLong should handle single dot separator`() {
        val result = RupiahFormatter.parseDisplayToLong("5.000")
        assertEquals(5000L, result)
    }

    @Test
    fun `parseDisplayToLong should handle invalid text`() {
        val result = RupiahFormatter.parseDisplayToLong("invalid")
        assertEquals(0L, result)
    }

    @Test
    fun `isValidRupiahText should return true for valid amounts`() {
        assertTrue(RupiahFormatter.isValidRupiahText("RP 100.000"))
        assertTrue(RupiahFormatter.isValidRupiahText("Rp 500"))
        assertTrue(RupiahFormatter.isValidRupiahText("1.000.000"))
    }

    @Test
    fun `isValidRupiahText should return false for invalid amounts`() {
        assertFalse(RupiahFormatter.isValidRupiahText(""))
        assertFalse(RupiahFormatter.isValidRupiahText("invalid text"))
        assertFalse(RupiahFormatter.isValidRupiahText("ABC"))
        assertFalse(RupiahFormatter.isValidRupiahText("RP 0"))
    }

    @Test
    fun `formatWithRupiahPrefix should handle zero amount`() {
        val result = RupiahFormatter.formatWithRupiahPrefix(0L)
        assertEquals("Rp 0", result)
    }

    @Test
    fun `formatWithRupiahPrefix should format small amounts`() {
        val result = RupiahFormatter.formatWithRupiahPrefix(500L)
        assertEquals("Rp 500", result)
    }

    @Test
    fun `formatWithRupiahPrefix should format large amounts with separators`() {
        val result = RupiahFormatter.formatWithRupiahPrefix(1500000L)
        assertEquals("Rp 1.500.000", result)
    }

    @Test
    fun `formatWithRupiahPrefix should format thousands correctly`() {
        val result = RupiahFormatter.formatWithRupiahPrefix(25000L)
        assertEquals("Rp 25.000", result)
    }

    // Edge cases and boundary tests
    @Test
    fun `extractNumberFromRupiahText should handle very large numbers`() {
        val result = RupiahFormatter.extractNumberFromRupiahText("RP 999.999.999.999")
        assertEquals(999999999999L, result)
    }

    @Test
    fun `extractNumberFromRupiahText should handle decimal only input`() {
        val result = RupiahFormatter.extractNumberFromRupiahText("0,50")
        assertEquals(0L, result)
    }

    @Test
    fun `extractNumberFromRupiahText should handle multiple commas`() {
        val result = RupiahFormatter.extractNumberFromRupiahText("1.000,00,00")
        assertEquals(1000L, result)
    }

    @Test
    fun `formatRupiahDisplay should handle maximum long value`() {
        val result = RupiahFormatter.formatRupiahDisplay(Long.MAX_VALUE)
        assertNotNull(result)
        assertTrue(result.isNotEmpty())
    }

    @Test
    fun `parseDisplayToLong should handle malformed input gracefully`() {
        val result = RupiahFormatter.parseDisplayToLong("1.2.3.4.5.6.7")
        assertEquals(1234567L, result)
    }

    // Integration tests combining multiple functions
    @Test
    fun `round trip test - extract then format should maintain value`() {
        val originalText = "RP 1.234.567"
        val extracted = RupiahFormatter.extractNumberFromRupiahText(originalText)
        val formatted = RupiahFormatter.formatRupiahDisplay(extracted)
        val parsed = RupiahFormatter.parseDisplayToLong(formatted)

        assertEquals(1234567L, extracted)
        assertEquals(1234567L, parsed)
    }

    @Test
    fun `round trip test - format then parse should maintain value`() {
        val originalValue = 2500000L
        val formatted = RupiahFormatter.formatRupiahDisplay(originalValue)
        val parsed = RupiahFormatter.parseDisplayToLong(formatted)

        assertEquals(originalValue, parsed)
    }
}
