package com.adilstudio.project.onevault.domain.usecase

import com.adilstudio.project.onevault.domain.repository.BillCategoryRepository

class DeleteBillCategoryUseCase(private val repository: BillCategoryRepository) {
    suspend operator fun invoke(id: String) = repository.deleteCategory(id)
}
