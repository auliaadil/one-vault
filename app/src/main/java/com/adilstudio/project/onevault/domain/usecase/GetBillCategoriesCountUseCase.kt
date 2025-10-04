package com.adilstudio.project.onevault.domain.usecase

import com.adilstudio.project.onevault.domain.repository.TransactionCategoryRepository

class GetTransactionCategoriesCountUseCase(
    private val transactionCategoryRepository: TransactionCategoryRepository
) {
    suspend operator fun invoke(): Int {
        return transactionCategoryRepository.getCategoriesCount()
    }
}
