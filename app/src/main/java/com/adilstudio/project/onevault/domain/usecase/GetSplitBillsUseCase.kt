package com.adilstudio.project.onevault.domain.usecase

import com.adilstudio.project.onevault.domain.repository.SplitBillRepository
import kotlinx.coroutines.flow.Flow
import com.adilstudio.project.onevault.domain.model.SplitBill

class GetSplitBillsUseCase(private val repository: SplitBillRepository) {
    operator fun invoke(): Flow<List<SplitBill>> = repository.getSplitBills()
}
