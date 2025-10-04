package com.adilstudio.project.onevault.domain.usecase

import com.adilstudio.project.onevault.domain.model.Transaction
import com.adilstudio.project.onevault.domain.repository.TransactionRepository

class UpdateTransactionUseCase(
    private val transactionRepository: TransactionRepository
) {
    suspend operator fun invoke(transaction: Transaction) {
        transactionRepository.updateTransaction(transaction)
    }
}
