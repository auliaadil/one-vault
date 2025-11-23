package com.adilstudio.project.onevault.presentation.component

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import com.adilstudio.project.onevault.core.util.CurrencyFormatter
import com.adilstudio.project.onevault.domain.model.Currency

/**
 * Composable that displays a formatted currency amount
 * @param amount The amount to display
 * @param currency The currency to use for formatting
 * @param modifier Modifier for styling
 * @param style Text style
 * @param color Text color
 * @param showSymbol Whether to show currency symbol
 */
@Composable
fun CurrencyText(
    amount: Double,
    currency: Currency,
    modifier: Modifier = Modifier,
    style: TextStyle = TextStyle.Default,
    color: Color = Color.Unspecified,
    showSymbol: Boolean = true
) {
    val formattedAmount = CurrencyFormatter.format(
        amount = amount,
        currency = currency,
        showSymbol = showSymbol
    )

    Text(
        text = formattedAmount,
        modifier = modifier,
        style = style,
        color = color
    )
}

/**
 * Composable that displays a formatted currency amount with automatic currency detection
 * Uses the user's selected currency from Currency.current
 * @param amount The amount to display
 * @param modifier Modifier for styling
 * @param style Text style
 * @param color Text color
 * @param showSymbol Whether to show currency symbol
 */
@Composable
fun AutoCurrencyText(
    amount: Double,
    modifier: Modifier = Modifier,
    style: TextStyle = TextStyle.Default,
    color: Color = Color.Unspecified,
    showSymbol: Boolean = true
) {
    CurrencyText(
        amount = amount,
        currency = Currency.current,
        modifier = modifier,
        style = style,
        color = color,
        showSymbol = showSymbol
    )
}

/**
 * Format amount to string with currency
 * @param amount The amount to format
 * @param currency The currency to use
 * @param showSymbol Whether to show currency symbol
 * @return Formatted currency string
 */
fun formatCurrency(
    amount: Double,
    currency: Currency,
    showSymbol: Boolean = true
): String {
    return CurrencyFormatter.format(
        amount = amount,
        currency = currency,
        showSymbol = showSymbol
    )
}

/**
 * Parse currency string to double
 * @param formattedAmount The formatted string
 * @param currency The currency used in formatting
 * @return Parsed amount or null if invalid
 */
fun parseCurrency(
    formattedAmount: String,
    currency: Currency
): Double? {
    return CurrencyFormatter.parse(formattedAmount, currency)
}
