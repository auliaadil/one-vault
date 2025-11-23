# 📱 One Vault - UI/UX Improvement Guide

This document outlines recommended **UI/UX improvements** for the One Vault app to enhance usability, clarity, and efficiency when managing both **Expenses** and **Income**.

---

## 1. Terminology Improvements

| Old Term | New Term             | Reason                                                     |
| -------- | -------------------- | ---------------------------------------------------------- |
| Transaction     | Transaction          | Handles both **Expense** & **Income**                      |
| Vendor   | Merchant / Source    | Vendor is expense-biased; “Source” works better for Income |
| Category | Transaction Category | Shared across Expense & Income                             |
| Amount   | Amount               | Keep consistent                                            |
| Title    | Activity / Title     | Clearer for context (e.g., *Dine In*, *Salary Payment*)    |

---

## 2. Data Entities

### Transaction

```kotlin
data class Transaction(
    val id: String,
    val title: String,              // e.g., "Dine In", "Salary Payment"
    val type: TransactionType,      // EXPENSE or INCOME
    val amount: Double,
    val category: TransactionCategory,
    val merchantOrSource: String?,  // Vendor (Expense) / Source (Income)
    val date: Long,                 // timestamp
    val notes: String? = null,
    val attachments: List<String> = emptyList() // image paths
)
```

### TransactionType

```kotlin
enum class TransactionType {
    EXPENSE,
    INCOME
}
```

### TransactionCategory

```kotlin
data class TransactionCategory(
    val id: String,
    val name: String,             // e.g., "Internet", "Salary"
    val type: TransactionType,    // EXPENSE or INCOME
    val icon: String? = null,
    val color: String? = null
)
```

---

## 3. Migration Map (Draft Format)

If migrating old **Transaction-only** structure to new **Transaction** structure:

| Old Field     | New Field                    | Notes                   |
| ------------- | ---------------------------- | ----------------------- |
| Transaction.title    | Transaction.title            | Keep values             |
| Transaction.vendor   | Transaction.merchantOrSource | Keep values, rename     |
| Transaction.amount   | Transaction.amount           | Same                    |
| Transaction.category | Transaction.category         | Map directly            |
| Transaction.date     | Transaction.date             | Same                    |
| Transaction.image    | Transaction.attachments      | Support multiple images |

---

## 4. User Journey Improvements

### 4.1 Add Transaction Flow

* **Step 1:** Choose type → Expense / Income toggle at top.
* **Step 2:** Fill Title (auto-suggest from past entries).
* **Step 3:** Select Category (filtered by type).
* **Step 4:** Add Amount.
* **Step 5:** Optional → Merchant/Source, Notes, Attachments.
* **Step 6:** Save → Redirect to list.

---

## 5. Smart Input Enhancements

### 5.1 Auto-Suggest (Non-AI)

* Suggest **previously used Titles, Categories, Merchants**.
* Frequency-based ranking (95% of use cases).
* Example:

    * If user types "Din", auto-suggest "Dine In".
    * If user types "Tel", suggest "Telkomsel Internet".

### 5.2 Category Shortcuts

* Show **recently used categories** on top.
* Pin favorite categories.

### 5.3 Amount Quick Inputs

* Calculator-like keypad for adding multiple amounts.
* Auto-detect if receipt total includes tax/service.

---

## 6. Reports & Analytics

### 6.1 Transaction List

* Unified list: shows both Expense & Income.
* Color coding: **Red = Expense**, **Green = Income**.
* Filter by:

    * Type (Expense / Income / All)
    * Date range
    * Category

### 6.2 Graphs

* Monthly Overview:

    * Pie chart → category breakdown.
    * Line chart → balance trend.
* Insights:

    * "Top 3 spending categories this month"
    * "You saved 20% compared to last month"

---

## 7. UI/UX Design Enhancements

* **Bottom Navigation Tabs:**

    * Transactions
    * Reports
    * Passwords
    * Settings

* **Floating Action Button (FAB):**

    * `+ Add Transaction` → Opens type selector (Expense / Income / Split Transaction).

* **Transaction Detail Screen:**

    * Show Title, Amount, Category, Merchant/Source, Date, Notes, Attachments.
    * Quick actions: Edit / Duplicate / Delete.

* **Dark Mode Support**

    * Consistent colors with accessibility in mind.

---

## 8. Future Enhancements

* Multi-currency support.
* Budgeting feature (set monthly cap per category).
* Export data (CSV, Excel, PDF).
* Backup to cloud.

---

✅ This serves as a **living design doc**. You can expand sections (e.g., split transaction UI, OCR parsing) as the app grows.

---