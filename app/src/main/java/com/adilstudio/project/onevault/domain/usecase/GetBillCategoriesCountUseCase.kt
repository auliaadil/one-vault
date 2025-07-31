package com.adilstudio.project.onevault.domain.usecase

import com.adilstudio.project.onevault.domain.repository.BillCategoryRepository

class GetBillCategoriesCountUseCase(
    private val billCategoryRepository: BillCategoryRepository
) {
    suspend operator fun invoke(): Int {
        return billCategoryRepository.getCategoriesCount()
    }
}
