package com.adilstudio.project.onevault.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Transaction(
    val id: Long,
    val title: String,
    val categoryId: Long? = null, // Reference to TransactionCategory
    val amount: Double,
    val vendor: String,
    val transactionDate: String, // Store as ISO date string (YYYY-MM-DD)
    val imagePath: String? = null, // Optional image path
    val accountId: Long? = null // Optional account where transaction is deducted from
)
