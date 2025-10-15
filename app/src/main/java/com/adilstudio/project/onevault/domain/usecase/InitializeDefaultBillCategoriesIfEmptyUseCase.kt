package com.adilstudio.project.onevault.domain.usecase

import com.adilstudio.project.onevault.domain.repository.TransactionCategoryRepository

class InitializeDefaultTransactionCategoriesIfEmptyUseCase(private val repository: TransactionCategoryRepository) {
    suspend operator fun invoke() = repository.initializeDefaultCategoriesIfEmpty()
}

