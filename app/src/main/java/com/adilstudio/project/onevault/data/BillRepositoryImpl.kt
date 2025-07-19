package com.adilstudio.project.onevault.data

import com.adilstudio.project.onevault.domain.model.Bill
import com.adilstudio.project.onevault.domain.repository.BillRepository
import kotlinx.coroutines.flow.Flow

class BillRepositoryImpl : BillRepository {
    override fun getBills(): Flow<List<Bill>> {
        // TODO: Implement using SQLDelight
        throw NotImplementedError()
    }
    override suspend fun getBillById(id: Long): Bill? {
        // TODO: Implement using SQLDelight
        throw NotImplementedError()
    }
    override suspend fun addBill(bill: Bill) {
        // TODO: Implement using SQLDelight
        throw NotImplementedError()
    }
    override suspend fun updateBill(bill: Bill) {
        // TODO: Implement using SQLDelight
        throw NotImplementedError()
    }
    override suspend fun deleteBill(id: Long) {
        // TODO: Implement using SQLDelight
        throw NotImplementedError()
    }
}

