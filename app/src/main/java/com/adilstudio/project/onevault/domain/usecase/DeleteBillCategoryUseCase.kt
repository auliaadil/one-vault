package com.adilstudio.project.onevault.domain.usecase

import com.adilstudio.project.onevault.domain.repository.TransactionCategoryRepository

class DeleteTransactionCategoryUseCase(private val repository: TransactionCategoryRepository) {
    suspend operator fun invoke(id: Long) = repository.deleteCategory(id)
}
