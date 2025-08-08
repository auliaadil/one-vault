package com.adilstudio.project.onevault.domain.usecase

import com.adilstudio.project.onevault.domain.repository.BillRepository

class DeleteBillUseCase(
    private val billRepository: BillRepository
) {
    suspend operator fun invoke(billId: Long) {
        billRepository.deleteBill(billId)
    }
}
