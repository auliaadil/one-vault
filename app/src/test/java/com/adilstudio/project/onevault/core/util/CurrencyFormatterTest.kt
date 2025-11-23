package com.adilstudio.project.onevault.core.util

import com.adilstudio.project.onevault.domain.model.Currency
import org.junit.Assert.*
import org.junit.Test

class CurrencyFormatterTest {

    @Test
    fun `format IDR currency with symbol`() {
        val amount = 1000000.0
        val result = CurrencyFormatter.format(amount, Currency.IDR, showSymbol = true)

        assertEquals("Rp 1.000.000", result)
    }

    @Test
    fun `format IDR currency without symbol`() {
        val amount = 1000000.0
        val result = CurrencyFormatter.format(amount, Currency.IDR, showSymbol = false)

        assertEquals("1.000.000", result)
    }

    @Test
    fun `format USD currency with symbol`() {
        val amount = 1000.50
        val result = CurrencyFormatter.format(amount, Currency.USD, showSymbol = true)

        assertEquals("$ 1,000.50", result)
    }

    @Test
    fun `format USD currency without symbol`() {
        val amount = 1000.50
        val result = CurrencyFormatter.format(amount, Currency.USD, showSymbol = false)

        assertEquals("1,000.50", result)
    }

    @Test
    fun `format EUR currency with symbol`() {
        val amount = 2500.75
        val result = CurrencyFormatter.format(amount, Currency.EUR, showSymbol = true)

        assertEquals("€ 2,500.75", result)
    }

    @Test
    fun `format MYR currency with symbol`() {
        val amount = 500.25
        val result = CurrencyFormatter.format(amount, Currency.MYR, showSymbol = true)

        assertEquals("RM 500.25", result)
    }

    @Test
    fun `format SGD currency with symbol`() {
        val amount = 750.99
        val result = CurrencyFormatter.format(amount, Currency.SGD, showSymbol = true)

        assertEquals("S$ 750.99", result)
    }

    @Test
    fun `format with code IDR`() {
        val amount = 50000.0
        val result = CurrencyFormatter.formatWithCode(amount, Currency.IDR)

        assertEquals("50.000 IDR", result)
    }

    @Test
    fun `format with code USD`() {
        val amount = 100.99
        val result = CurrencyFormatter.formatWithCode(amount, Currency.USD)

        assertEquals("100.99 USD", result)
    }

    @Test
    fun `parse IDR formatted string`() {
        val formatted = "Rp 1.000.000"
        val result = CurrencyFormatter.parse(formatted, Currency.IDR)

        assertEquals(1000000.0, result, 0.01)
    }

    @Test
    fun `parse USD formatted string`() {
        val formatted = "$ 1,000.50"
        val result = CurrencyFormatter.parse(formatted, Currency.USD)

        assertEquals(1000.50, result!!, 0.01)
    }

    @Test
    fun `parse EUR formatted string`() {
        val formatted = "€ 2,500.75"
        val result = CurrencyFormatter.parse(formatted, Currency.EUR)

        assertEquals(2500.75, result!!, 0.01)
    }

    @Test
    fun `parse formatted string without symbol`() {
        val formatted = "1.000.000"
        val result = CurrencyFormatter.parse(formatted, Currency.IDR)

        assertEquals(1000000.0, result, 0.01)
    }

    @Test
    fun `parse invalid string returns null`() {
        val formatted = "invalid"
        val result = CurrencyFormatter.parse(formatted, Currency.IDR)

        assertNull(result)
    }

    @Test
    fun `format zero amount IDR`() {
        val amount = 0.0
        val result = CurrencyFormatter.format(amount, Currency.IDR, showSymbol = true)

        assertEquals("Rp 0", result)
    }

    @Test
    fun `format zero amount USD`() {
        val amount = 0.0
        val result = CurrencyFormatter.format(amount, Currency.USD, showSymbol = true)

        assertEquals("$ 0.00", result)
    }

    @Test
    fun `format negative amount IDR`() {
        val amount = -50000.0
        val result = CurrencyFormatter.format(amount, Currency.IDR, showSymbol = true)

        assertTrue(result.contains("-50.000") || result.contains("50.000-"))
    }

    @Test
    fun `format large amount USD`() {
        val amount = 1234567.89
        val result = CurrencyFormatter.format(amount, Currency.USD, showSymbol = true)

        assertEquals("$ 1,234,567.89", result)
    }

    @Test
    fun `format small decimal USD`() {
        val amount = 0.01
        val result = CurrencyFormatter.format(amount, Currency.USD, showSymbol = true)

        assertEquals("$ 0.01", result)
    }
}

