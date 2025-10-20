package com.adilstudio.project.onevault.domain.usecase

import com.adilstudio.project.onevault.domain.repository.SplitBillRepository

class DeleteSplitBillUseCase(private val repository: SplitBillRepository) {
    suspend operator fun invoke(id: Long) = repository.deleteSplitBill(id)
}

