# Multi-Currency Implementation Guide

## Overview
The OneVault app now supports multiple currencies for transaction management. Users can select their preferred currency from the settings, and all amounts will be formatted accordingly throughout the app.

## Supported Currencies
- **IDR** (Indonesian Rupiah) - Default
- **USD** (US Dollar)
- **EUR** (Euro)
- **MYR** (Malaysian Ringgit)
- **SGD** (Singapore Dollar)

## Implementation Details

### 1. Currency Model
**Location**: `domain/model/Currency.kt`

An enum class that defines all supported currencies with their properties:
```kotlin
Currency.IDR  // "Rp", decimal separator: ',', thousand separator: '.'
Currency.USD  // "$", decimal separator: '.', thousand separator: ','
Currency.EUR  // "€", decimal separator: '.', thousand separator: ','
Currency.MYR  // "RM", decimal separator: '.', thousand separator: ','
Currency.SGD  // "S$", decimal separator: '.', thousand separator: ','
```

Each currency has:
- `code`: ISO currency code (e.g., "USD")
- `symbol`: Display symbol (e.g., "$")
- `displayName`: Full name (e.g., "US Dollar")
- `decimalSeparator`: Character for decimal point
- `thousandSeparator`: Character for thousands grouping

### 2. Currency Formatter
**Location**: `core/util/CurrencyFormatter.kt`

Utility object for formatting amounts:

#### Methods:
- **`format(amount: Double, currency: Currency, showSymbol: Boolean = true): String`**
  - Formats amount with proper separators and symbol
  - IDR: No decimal places (e.g., "Rp 1.000.000")
  - Others: 2 decimal places (e.g., "$ 1,000.50")

- **`formatWithCode(amount: Double, currency: Currency): String`**
  - Formats with currency code instead of symbol
  - Example: "1,000.50 USD"

- **`parse(formattedAmount: String, currency: Currency): Double?`**
  - Parses formatted string back to double
  - Returns null for invalid input

### 3. Currency Helpers (Composable)
**Location**: `presentation/component/CurrencyHelpers.kt`

Composable functions for UI:

#### Components:
- **`CurrencyText()`**: Display formatted amount with specific currency
  ```kotlin
  CurrencyText(
      amount = 1000.0,
      currency = Currency.USD,
      style = MaterialTheme.typography.bodyLarge
  )
  ```

- **`AutoCurrencyText()`**: Automatically uses user's selected currency
  ```kotlin
  AutoCurrencyText(
      amount = transaction.amount,
      style = MaterialTheme.typography.titleMedium
  )
  ```

#### Helper Functions:
- `formatCurrency()`: Format amount to string
- `parseCurrency()`: Parse formatted string to double

### 4. Get Currency Use Case
**Location**: `domain/usecase/GetCurrencyUseCase.kt`

Retrieves the user's selected currency from settings as a Flow.

```kotlin
class GetCurrencyUseCase(
    private val settingsRepository: SettingsRepository
) {
    operator fun invoke(): Flow<Currency>
}
```

### 5. Currency Selection Dialog
**Location**: `presentation/settings/CurrencySelectionDialog.kt`

A Material3 dialog that allows users to select their preferred currency:
- Shows all available currencies with symbols and codes
- Highlights the currently selected currency
- Updates settings when user makes a selection

## Usage Examples

### In ViewModels
```kotlin
class TransactionViewModel(
    private val getCurrencyUseCase: GetCurrencyUseCase
) : ViewModel() {
    
    val currency = getCurrencyUseCase().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = Currency.IDR
    )
}
```

### In Composables
```kotlin
@Composable
fun TransactionItem(transaction: Transaction) {
    // Option 1: Auto-detect currency
    AutoCurrencyText(
        amount = transaction.amount,
        style = MaterialTheme.typography.titleLarge
    )
    
    // Option 2: Specific currency
    CurrencyText(
        amount = transaction.amount,
        currency = Currency.USD,
        showSymbol = true
    )
}
```

### In Non-Composable Code
```kotlin
val getCurrencyUseCase: GetCurrencyUseCase = get() // Koin inject

viewModelScope.launch {
    getCurrencyUseCase().collect { currency ->
        val formatted = CurrencyFormatter.format(amount, currency)
        println(formatted)
    }
}
```

## Testing

### Unit Tests Created:
1. **CurrencyFormatterTest.kt**: Tests all formatting and parsing functions
2. **GetCurrencyUseCaseTest.kt**: Tests currency retrieval use case
3. **CurrencyTest.kt**: Tests Currency enum properties and fromCode()

### Test Coverage:
- ✅ Formatting with/without symbols
- ✅ All currency types (IDR, USD, EUR, MYR, SGD)
- ✅ Parsing formatted strings
- ✅ Invalid input handling
- ✅ Zero and negative amounts
- ✅ Large numbers
- ✅ Default fallback behavior

## Migration Notes

### Updating Existing Code:
1. Replace hardcoded "Rp" formatting with `CurrencyFormatter.format()`
2. Use `AutoCurrencyText()` in Composables for automatic currency detection
3. Inject `GetCurrencyUseCase` where currency information is needed

### Example Migration:
**Before:**
```kotlin
Text(text = "Rp ${NumberFormat.format(amount)}")
```

**After:**
```kotlin
AutoCurrencyText(amount = amount)
```

## Settings Integration

Users can change their currency preference in:
**Settings → Currency → Select Currency**

The selection is persisted using `SettingsRepository` and affects:
- Transaction display
- Account balances
- Reports and analytics
- Export/Import operations

## Future Enhancements
- [ ] Currency conversion rates
- [ ] Multiple currency accounts
- [ ] Transaction currency override
- [ ] Historical exchange rates
- [ ] Auto-currency detection from location

