package com.adilstudio.project.onevault.domain.model

/**
 * Supported currencies in the app
 */
enum class Currency(
    val code: String,
    val symbol: String,
    val displayName: String,
    val decimalSeparator: Char,
    val thousandSeparator: Char
) {
    IDR("IDR", "Rp", "Indonesian Rupiah", ',', '.'),
    USD("USD", "$", "US Dollar", '.', ','),
    EUR("EUR", "€", "Euro", '.', ','),
    MYR("MYR", "RM", "Malaysian Ringgit", '.', ','),
    SGD("SGD", "S$", "Singapore Dollar", '.', ',');

    companion object {
        /**
         * The currently active currency for the app.
         * This should be initialized on app startup and updated whenever the user changes currency.
         * Default to IDR if not yet initialized.
         */
        @Volatile
        var current: Currency = IDR
            private set

        /**
         * Update the current currency.
         * This should be called when the app starts or when the user changes their currency preference.
         * @param currency The new currency to set as current
         */
        fun setCurrent(currency: Currency) {
            current = currency
        }

        fun fromCode(code: String): Currency {
            return entries.find { it.code == code } ?: IDR
        }
    }
}
