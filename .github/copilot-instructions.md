````markdown
# Project Context for GitHub Copilot

## 📱 App Overview
This is a **personal finance management app** with multiple modules:
- **Transactions** (records of Income & Expense, previously called Transactions).
- **Transaction Categories** (structured classification of expenses/income, e.g. Food, Transport, Salary, Investment).
- **Accounts** (e.g., Bank Savings, Investment, Cash).
- **Credentials** (password manager module with password templates).
- **Vault Files** (secure storage for documents or attachments).

The app supports both **Expense** and **Income** tracking.

---

## 📊 Core Data Models

### Transaction
```kotlin
@Serializable
data class Transaction(
    val id: Long,
    val title: String,              // Activity, e.g. "Dine In", "Salary"
    val categoryId: Long?,          // Reference to TransactionCategory
    val amount: Double,             // Positive number
    val merchant: String?,          // Where it happened, e.g. "McDonald's"
    val date: String,               // ISO format (YYYY-MM-DD)
    val imagePath: String? = null,  // Optional receipt or transaction image
    val accountId: Long? = null     // Which account is affected
)
````

### TransactionCategory

```kotlin
@Serializable
data class TransactionCategory(
    val id: Long?,
    val name: String,               // e.g., Food, Internet, Salary
    val icon: String,
    val color: String,
    val type: CategoryType,         // EXPENSE or INCOME
    val parentCategoryId: Long? = null,
    val isEditable: Boolean = true,
    val createdAt: Long,
    val updatedAt: Long
)
```

### Account

```kotlin
@Serializable
data class Account(
    val id: Long,
    val name: String,               // e.g., "Bank X Savings", "Cash"
    val amount: Double = 0.0,
    val description: String? = null,
    val isEditable: Boolean = true,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
```

### Credential

```kotlin
@Serializable
data class Credential(
    val id: Long = 0L,
    val serviceName: String,        // e.g., "Gmail"
    val username: String,
    val password: String,
    val passwordTemplate: String? = null, // JSON rules for password generation
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
```

### VaultFile

```kotlin
@Serializable
data class VaultFile(
    val id: Long,
    val name: String,                // File name
    val category: String,            // e.g., "Transactions", "Receipts"
    val filePath: String
)
```

---

## 🖌️ UI/UX Principles

* **Bottom Navigation Tabs**:

    1. **Transactions** → full transaction list with filters/search
    2. **Reports** → charts, monthly summaries, category breakdowns
    3. **Passwords** → password list, password generator
    4. **Settings** → account, sync, preferences

* **Scan & Extract Flow**:

    * User scans/loads a transaction/receipt.
    * OCR extracts text.
    * Auto-suggest fills **Activity**, **Merchant**, and **Amount**.
    * User can edit or confirm values.

* **Smart Suggestions**:

    * Use rule-based auto-suggestions (e.g., regex for amount, merchant dictionary) without relying on AI.
    * AI may be integrated later for smarter parsing.

---

## 🚀 Copilot Instructions

When generating code:

* Always use **Kotlin, Jetpack Compose, Coroutines, MVVM** as the main stack.
* Prefer **clean architecture** → domain layer, data layer, UI layer separation.
* For OCR, use **ML Kit Text Recognition**.
* Use **SQLDelight** for persistence.
* Follow **best practices for Android development (Jetpack)**.
* When suggesting names, stick to our domain terms:

    * Transaction (instead of Transaction)
    * TransactionCategory (instead of TransactionCategory)
    * Merchant (instead of Vendor)
    * Activity (instead of Title)

---

👉 Copilot should use this context whenever generating code for this repo.

```