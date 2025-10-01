package com.adilstudio.project.onevault.domain.usecase

import com.adilstudio.project.onevault.domain.model.SplitBill
import com.adilstudio.project.onevault.domain.repository.SplitBillRepository

class AddSplitBillUseCase(private val repository: SplitBillRepository) {
    suspend operator fun invoke(splitBill: SplitBill): Long = repository.addSplitBill(splitBill)
}
