package com.adilstudio.project.onevault.domain.repository

import com.adilstudio.project.onevault.domain.model.Bill
import kotlinx.coroutines.flow.Flow

interface BillRepository {
    fun getBills(): Flow<List<Bill>>
    suspend fun getBillById(id: Long): Bill?
    suspend fun addBill(bill: Bill)
    suspend fun updateBill(bill: Bill)
    suspend fun deleteBill(id: Long)
}

