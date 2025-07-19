package com.adilstudio.project.onevault.domain.usecase

import com.adilstudio.project.onevault.domain.model.Bill
import com.adilstudio.project.onevault.domain.repository.BillRepository

class AddBillUseCase(private val repository: BillRepository) {
    suspend operator fun invoke(bill: Bill) = repository.addBill(bill)
}

