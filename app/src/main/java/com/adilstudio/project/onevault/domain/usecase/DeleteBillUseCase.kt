package com.adilstudio.project.onevault.domain.usecase

import com.adilstudio.project.onevault.domain.repository.TransactionRepository

class DeleteTransactionUseCase(
    private val transactionRepository: TransactionRepository
) {
    suspend operator fun invoke(transactionId: Long) {
        transactionRepository.deleteTransaction(transactionId)
    }
}
