package com.adilstudio.project.onevault.domain.usecase

import com.adilstudio.project.onevault.domain.model.Transaction
import com.adilstudio.project.onevault.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow

class GetTransactionsUseCase(private val repository: TransactionRepository) {
    operator fun invoke(): Flow<List<Transaction>> = repository.getTransactions()
}

