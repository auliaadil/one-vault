package com.adilstudio.project.onevault.domain.usecase

import com.adilstudio.project.onevault.domain.model.SplitBill
import com.adilstudio.project.onevault.domain.repository.SplitBillRepository
import kotlinx.coroutines.flow.Flow

class GetSplitBillsUseCase(private val repository: SplitBillRepository) {
    operator fun invoke(): Flow<List<SplitBill>> = repository.getSplitBills()
}

