package com.adilstudio.project.onevault.domain.usecase

import com.adilstudio.project.onevault.domain.model.SplitBill
import com.adilstudio.project.onevault.domain.repository.SplitBillRepository

class UpdateSplitBillUseCase(private val repository: SplitBillRepository) {
    suspend operator fun invoke(splitBill: SplitBill) = repository.updateSplitBill(splitBill)
}
