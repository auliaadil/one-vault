package com.adilstudio.project.onevault.domain.usecase

import com.adilstudio.project.onevault.domain.model.BillCategory
import com.adilstudio.project.onevault.domain.repository.BillCategoryRepository

class UpdateBillCategoryUseCase(private val repository: BillCategoryRepository) {
    suspend operator fun invoke(category: BillCategory) = repository.updateCategory(category)
}
