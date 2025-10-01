package com.adilstudio.project.onevault.domain.repository

import com.adilstudio.project.onevault.domain.model.SplitBill
import kotlinx.coroutines.flow.Flow

interface SplitBillRepository {
    fun getSplitBills(): Flow<List<SplitBill>>
    suspend fun getSplitBillById(id: Long): SplitBill?
    suspend fun addSplitBill(splitBill: SplitBill): Long
    suspend fun updateSplitBill(splitBill: SplitBill)
    suspend fun deleteSplitBill(id: Long)
}
