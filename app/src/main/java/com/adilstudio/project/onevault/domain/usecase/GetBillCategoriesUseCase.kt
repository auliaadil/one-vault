package com.adilstudio.project.onevault.domain.usecase

import com.adilstudio.project.onevault.domain.model.BillCategory
import com.adilstudio.project.onevault.domain.repository.BillCategoryRepository
import kotlinx.coroutines.flow.Flow

class GetBillCategoriesUseCase(private val repository: BillCategoryRepository) {
    operator fun invoke(): Flow<List<BillCategory>> = repository.getCategories()
}
