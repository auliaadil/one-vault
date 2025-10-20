package com.adilstudio.project.onevault.domain.usecase

import com.adilstudio.project.onevault.domain.model.SplitBill
import com.adilstudio.project.onevault.domain.repository.SplitBillRepository

class GetSplitBillByIdUseCase(private val repository: SplitBillRepository) {
    suspend operator fun invoke(id: Long): SplitBill? = repository.getSplitBillWithDetails(id)
}

