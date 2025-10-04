package com.adilstudio.project.onevault.domain.usecase

import com.adilstudio.project.onevault.domain.model.TransactionCategory
import com.adilstudio.project.onevault.domain.repository.TransactionCategoryRepository

class UpdateTransactionCategoryUseCase(private val repository: TransactionCategoryRepository) {
    suspend operator fun invoke(category: TransactionCategory) = repository.updateCategory(category)
}
