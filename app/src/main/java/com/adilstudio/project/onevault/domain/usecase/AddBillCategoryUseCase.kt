package com.adilstudio.project.onevault.domain.usecase

import com.adilstudio.project.onevault.domain.model.BillCategory
import com.adilstudio.project.onevault.domain.repository.BillCategoryRepository

class AddBillCategoryUseCase(private val repository: BillCategoryRepository) {
    suspend operator fun invoke(category: BillCategory) = repository.addCategory(category)
}
