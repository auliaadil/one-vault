package com.adilstudio.project.onevault.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Transaction(
    val id: Long,
    val title: String, // Activity, e.g. "Dine In", "Salary"
    val categoryId: Long? = null, // Reference to TransactionCategory
    val amount: Double, // Positive number
    val merchant: String?, // Where it happened, e.g. "McDonald's" (was vendor)
    val date: String, // ISO format (YYYY-MM-DD) (was transactionDate)
    val type: TransactionType, // EXPENSE or INCOME
    val imagePath: String? = null, // Optional receipt or transaction image
    val accountId: Long? = null // Which account is affected
)
