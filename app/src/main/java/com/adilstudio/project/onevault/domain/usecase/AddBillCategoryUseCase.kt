package com.adilstudio.project.onevault.domain.usecase

import com.adilstudio.project.onevault.domain.model.TransactionCategory
import com.adilstudio.project.onevault.domain.repository.TransactionCategoryRepository

class AddTransactionCategoryUseCase(private val repository: TransactionCategoryRepository) {
    suspend operator fun invoke(category: TransactionCategory) = repository.addCategory(category)
}
