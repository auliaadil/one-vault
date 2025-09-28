package com.adilstudio.project.onevault.domain.usecase

import com.adilstudio.project.onevault.domain.repository.BillCategoryRepository

class InitializeDefaultBillCategoriesIfEmptyUseCase(private val repository: BillCategoryRepository) {
    suspend operator fun invoke() = repository.initializeDefaultCategoriesIfEmpty()
}

