package com.adilstudio.project.onevault.domain.usecase

import com.adilstudio.project.onevault.domain.model.Bill
import com.adilstudio.project.onevault.domain.repository.BillRepository

class UpdateBillUseCase(
    private val billRepository: BillRepository
) {
    suspend operator fun invoke(bill: Bill) {
        billRepository.updateBill(bill)
    }
}
