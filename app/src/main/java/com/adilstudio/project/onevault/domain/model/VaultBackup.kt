package com.adilstudio.project.onevault.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class VaultBackup(
    val version: String,
    val createdAt: Long,
    val transactions: List<Transaction>,
    val credentials: List<Credential>,
    val accounts: List<Account>,
    val transactionCategories: List<TransactionCategory>,
    val vaultFiles: List<VaultFile>,
    val splitBills: List<SplitBill> = emptyList(),
    val splitItems: List<SplitItem> = emptyList(),
    val splitParticipants: List<SplitParticipant> = emptyList()
)
