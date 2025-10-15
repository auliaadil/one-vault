package com.adilstudio.project.onevault.domain.usecase

import com.adilstudio.project.onevault.domain.model.Transaction
import com.adilstudio.project.onevault.domain.repository.TransactionRepository

class AddTransactionUseCase(private val repository: TransactionRepository) {
    suspend operator fun invoke(transaction: Transaction) = repository.addTransaction(transaction)
}
