# SetCurrencyUseCase Implementation

## Overview
Successfully implemented `SetCurrencyUseCase` to allow users to change their currency preference in the OneVault app.

## Files Implemented/Modified

### 1. **SetCurrencyUseCase.kt** ✅
**Location**: `domain/usecase/SetCurrencyUseCase.kt`

```kotlin
class SetCurrencyUseCase(
    private val settingsRepository: SettingsRepository
) {
    suspend operator fun invoke(currency: Currency)
}
```

**Purpose**: Provides a clean domain layer interface for setting the user's preferred currency.

**Usage**:
```kotlin
// In a ViewModel or other component
viewModelScope.launch {
    setCurrencyUseCase(Currency.USD)
}
```

### 2. **SetCurrencyUseCaseTest.kt** ✅
**Location**: `test/domain/usecase/SetCurrencyUseCaseTest.kt`

**Test Coverage**:
- ✅ Sets IDR currency code in repository
- ✅ Sets USD currency code in repository
- ✅ Sets EUR currency code in repository
- ✅ Sets MYR currency code in repository
- ✅ Sets SGD currency code in repository

**Total Tests**: 5 test cases covering all supported currencies

### 3. **SettingsViewModel.kt** ✅
**Location**: `presentation/settings/SettingsViewModel.kt`

**Changes Made**:
- Added `GetCurrencyUseCase` and `SetCurrencyUseCase` as constructor dependencies
- Updated `loadSettings()` to use `GetCurrencyUseCase` instead of calling repository directly
- Updated `setCurrency()` to use `SetCurrencyUseCase` instead of calling repository directly

**Before**:
```kotlin
class SettingsViewModel(
    private val settingsRepository: SettingsRepository
) {
    fun setCurrency(currency: Currency) {
        viewModelScope.launch {
            settingsRepository.setCurrency(currency.code)
            _currency.value = currency
        }
    }
}
```

**After**:
```kotlin
class SettingsViewModel(
    private val settingsRepository: SettingsRepository,
    private val getCurrencyUseCase: GetCurrencyUseCase,
    private val setCurrencyUseCase: SetCurrencyUseCase
) {
    fun setCurrency(currency: Currency) {
        viewModelScope.launch {
            setCurrencyUseCase(currency)
            _currency.value = currency
        }
    }
}
```

### 4. **viewModelModule.kt** ✅
**Location**: `di/viewModelModule.kt`

**Changes Made**:
- Updated SettingsViewModel DI registration from `get()` to `get(), get(), get()` to inject all three dependencies

```kotlin
viewModel { SettingsViewModel(get(), get(), get()) }
```

### 5. **useCaseModule.kt** ✅
**Location**: `di/useCaseModule.kt`

**Status**: Already registered! No changes needed.

```kotlin
single { GetCurrencyUseCase(get()) }
single { SetCurrencyUseCase(get()) }
```

## Architecture Pattern

The implementation follows clean architecture principles:

```
Presentation Layer (SettingsViewModel)
         ↓ uses
Domain Layer (SetCurrencyUseCase)
         ↓ uses
Data Layer (SettingsRepository)
         ↓ uses
Storage (PreferenceManager)
```

## Benefits

1. **Separation of Concerns**: Business logic separated from UI and data layers
2. **Testability**: Easy to mock and test in isolation
3. **Consistency**: Follows the same pattern as other use cases in the app
4. **Type Safety**: Uses Currency enum instead of raw strings
5. **Reusability**: Can be reused across different ViewModels if needed

## Integration Status

✅ Use case implemented
✅ Unit tests created (5 test cases)
✅ Registered in Koin DI
✅ Integrated with SettingsViewModel
✅ All files compile without errors

## How It Works

1. User selects a currency from the Settings screen
2. SettingsViewModel calls `setCurrencyUseCase(currency)`
3. Use case converts Currency enum to currency code string
4. Repository saves the currency code to PreferenceManager
5. All UI components reactively update to show amounts in the new currency

## Related Components

- **GetCurrencyUseCase**: Retrieves the current currency
- **Currency.kt**: Enum defining all supported currencies
- **CurrencyFormatter**: Formats amounts based on currency rules
- **AutoCurrencyText**: Composable that automatically displays amounts with user's currency
- **formatCurrency()**: Helper function for currency formatting

## Testing

Run the tests with:
```bash
./gradlew test --tests SetCurrencyUseCaseTest
```

All 5 test cases should pass, verifying that the use case correctly sets each supported currency.

