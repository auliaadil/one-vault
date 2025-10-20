package com.adilstudio.project.onevault.domain.repository

import com.adilstudio.project.onevault.domain.model.*
import kotlinx.coroutines.flow.Flow

interface SplitBillRepository {
    // Main SplitBill operations
    fun getSplitBills(): Flow<List<SplitBill>>
    suspend fun getSplitBillById(id: Long): SplitBill?
    suspend fun addSplitBill(splitBill: SplitBill): Long
    suspend fun updateSplitBill(splitBill: SplitBill)
    suspend fun deleteSplitBill(id: Long)

    // Items operations
    suspend fun addSplitItems(splitBillId: Long, items: List<SplitItem>)
    suspend fun updateSplitItems(splitBillId: Long, items: List<SplitItem>)
    suspend fun getSplitItems(splitBillId: Long): List<SplitItem>

    // Participants operations
    suspend fun addSplitParticipants(splitBillId: Long, participants: List<SplitParticipant>)
    suspend fun updateSplitParticipants(splitBillId: Long, participants: List<SplitParticipant>)
    suspend fun getSplitParticipants(splitBillId: Long): List<SplitParticipant>

    // Complete Split Bill with all related data
    suspend fun getSplitBillWithDetails(id: Long): SplitBill?

    // Export to Transaction
    suspend fun exportParticipantToTransaction(splitBillId: Long, participantName: String): Boolean
}
