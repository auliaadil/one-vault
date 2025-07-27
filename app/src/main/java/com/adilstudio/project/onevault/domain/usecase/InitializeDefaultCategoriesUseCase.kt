package com.adilstudio.project.onevault.domain.usecase

import com.adilstudio.project.onevault.domain.repository.BillCategoryRepository

class InitializeDefaultCategoriesUseCase(private val repository: BillCategoryRepository) {
    suspend operator fun invoke() = repository.initializeDefaultCategories()
}
