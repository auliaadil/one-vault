package com.adilstudio.project.onevault.domain.usecase

import com.adilstudio.project.onevault.domain.model.TransactionCategory
import com.adilstudio.project.onevault.domain.repository.TransactionCategoryRepository
import kotlinx.coroutines.flow.Flow

class GetTransactionCategoriesUseCase(private val repository: TransactionCategoryRepository) {
    operator fun invoke(): Flow<List<TransactionCategory>> = repository.getCategories()
}
