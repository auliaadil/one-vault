package com.adilstudio.project.onevault.domain.usecase

import com.adilstudio.project.onevault.domain.model.SplitBill
import com.adilstudio.project.onevault.domain.model.SplitParticipant
import com.adilstudio.project.onevault.domain.repository.SplitBillRepository

class ExportParticipantToTransactionUseCase(private val repository: SplitBillRepository) {
    suspend operator fun invoke(splitBill: SplitBill, participant: SplitParticipant): Boolean {
        return repository.exportParticipantToTransaction(splitBill, participant)
    }
}

