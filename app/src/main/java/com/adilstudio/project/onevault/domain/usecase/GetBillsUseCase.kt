package com.adilstudio.project.onevault.domain.usecase

import com.adilstudio.project.onevault.domain.model.Bill
import com.adilstudio.project.onevault.domain.repository.BillRepository
import kotlinx.coroutines.flow.Flow

class GetBillsUseCase(private val repository: BillRepository) {
    operator fun invoke(): Flow<List<Bill>> = repository.getBills()
}

