package com.adilstudio.project.onevault.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.adilstudio.project.onevault.Database
import com.adilstudio.project.onevault.domain.model.*
import com.adilstudio.project.onevault.domain.repository.SplitBillRepository
import com.adilstudio.project.onevault.domain.repository.TransactionRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class SplitBillRepositoryImpl(
    private val database: Database,
    private val transactionRepository: TransactionRepository
) : SplitBillRepository {

    private val splitBillQueries = database.splitBillEntityQueries
    private val splitItemQueries = database.splitItemEntityQueries
    private val splitParticipantQueries = database.splitParticipantEntityQueries

    override fun getSplitBills(): Flow<List<SplitBill>> {
        return splitBillQueries.selectAll()
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { entities ->
                entities.map { entity ->
                    SplitBill(
                        id = entity.id,
                        title = entity.title,
                        merchant = entity.merchant,
                        date = entity.date,
                        tax = entity.tax,
                        serviceFee = entity.serviceFee,
                        totalAmount = entity.totalAmount,
                        imagePath = entity.imagePath,
                        createdAt = entity.createdAt,
                        updatedAt = entity.updatedAt
                    )
                }
            }
    }

    override suspend fun getSplitBillById(id: Long): SplitBill? {
        return splitBillQueries.selectById(id)
            .executeAsOneOrNull()
            ?.let { entity ->
                SplitBill(
                    id = entity.id,
                    title = entity.title,
                    merchant = entity.merchant,
                    date = entity.date,
                    tax = entity.tax,
                    serviceFee = entity.serviceFee,
                    totalAmount = entity.totalAmount,
                    imagePath = entity.imagePath,
                    createdAt = entity.createdAt,
                    updatedAt = entity.updatedAt
                )
            }
    }

    override suspend fun addSplitBill(splitBill: SplitBill): Long {
        splitBillQueries.insertSplitBill(
            title = splitBill.title,
            merchant = splitBill.merchant,
            date = splitBill.date,
            tax = splitBill.tax,
            serviceFee = splitBill.serviceFee,
            totalAmount = splitBill.totalAmount,
            imagePath = splitBill.imagePath,
            createdAt = splitBill.createdAt,
            updatedAt = splitBill.updatedAt
        )

        return splitBillQueries.lastInsertRowId().executeAsOne()
    }

    override suspend fun updateSplitBill(splitBill: SplitBill) {
        splitBillQueries.updateSplitBill(
            title = splitBill.title,
            merchant = splitBill.merchant,
            date = splitBill.date,
            tax = splitBill.tax,
            serviceFee = splitBill.serviceFee,
            totalAmount = splitBill.totalAmount,
            imagePath = splitBill.imagePath,
            updatedAt = System.currentTimeMillis(),
            id = splitBill.id
        )
    }

    override suspend fun deleteSplitBill(id: Long) {
        database.transaction {
            splitItemQueries.deleteBySplitBillId(id)
            splitParticipantQueries.deleteBySplitBillId(id)
            splitBillQueries.deleteSplitBill(id)
        }
    }

    override suspend fun addSplitItems(splitBillId: Long, items: List<SplitItem>) {
        database.transaction {
            items.forEach { item ->
                splitItemQueries.insertSplitItem(
                    splitBillId = splitBillId,
                    description = item.description,
                    price = item.price,
                    assignedTo = Json.encodeToString(item.assignedQuantities)
                )
            }
        }
    }

    override suspend fun updateSplitItems(splitBillId: Long, items: List<SplitItem>) {
        database.transaction {
            splitItemQueries.deleteBySplitBillId(splitBillId)
            items.forEach { item ->
                splitItemQueries.insertSplitItem(
                    splitBillId = splitBillId,
                    description = item.description,
                    price = item.price,
                    assignedTo = Json.encodeToString(item.assignedQuantities)
                )
            }
        }
    }

    override suspend fun getSplitItems(splitBillId: Long): List<SplitItem> {
        return splitItemQueries.selectAllBySplitBillId(splitBillId)
            .executeAsList()
            .map { entity ->
                SplitItem(
                    id = entity.id,
                    splitBillId = entity.splitBillId,
                    description = entity.description,
                    price = entity.price,
                    assignedQuantities = try {
                        Json.decodeFromString<Map<String, Int>>(entity.assignedTo)
                    } catch (e: Exception) {
                        // Handle legacy data that might still be List<String>
                        try {
                            val legacyList = Json.decodeFromString<List<String>>(entity.assignedTo)
                            // Convert legacy assignedTo list to assignedQuantities map (each person gets quantity 1)
                            legacyList.associateWith { 1 }
                        } catch (e2: Exception) {
                            emptyMap()
                        }
                    }
                )
            }
    }

    override suspend fun addSplitParticipants(splitBillId: Long, participants: List<SplitParticipant>) {
        database.transaction {
            participants.forEach { participant ->
                splitParticipantQueries.insertSplitParticipant(
                    splitBillId = splitBillId,
                    name = participant.name,
                    shareAmount = participant.shareAmount,
                    note = participant.note
                )
            }
        }
    }

    override suspend fun updateSplitParticipants(splitBillId: Long, participants: List<SplitParticipant>) {
        database.transaction {
            splitParticipantQueries.deleteBySplitBillId(splitBillId)
            // Fix: Call with proper parameters within transaction
            participants.forEach { participant ->
                splitParticipantQueries.insertSplitParticipant(
                    splitBillId = splitBillId,
                    name = participant.name,
                    shareAmount = participant.shareAmount,
                    note = participant.note
                )
            }
        }
    }

    override suspend fun getSplitParticipants(splitBillId: Long): List<SplitParticipant> {
        return splitParticipantQueries.selectAllBySplitBillId(splitBillId)
            .executeAsList()
            .map { entity ->
                SplitParticipant(
                    id = entity.id,
                    splitBillId = entity.splitBillId,
                    name = entity.name,
                    shareAmount = entity.shareAmount,
                    note = entity.note
                )
            }
    }

    override suspend fun getSplitBillWithDetails(id: Long): SplitBill? {
        val splitBill = getSplitBillById(id) ?: return null
        val items = getSplitItems(id)
        val participants = getSplitParticipants(id)

        return splitBill.copy(
            items = items,
            participants = participants
        )
    }

    override suspend fun exportParticipantToTransaction(splitBillId: Long, participantName: String): Boolean {
        return try {
            val splitBill = getSplitBillWithDetails(splitBillId) ?: return false
            val participant = splitBill.participants.find { it.name == participantName } ?: return false

            val transaction = Transaction(
                id = 0L, // Fix: Add missing id parameter
                title = "${splitBill.title} - ${participantName}'s share",
                categoryId = null, // Let user assign category
                amount = participant.shareAmount,
                merchant = splitBill.merchant,
                date = splitBill.date,
                type = TransactionType.EXPENSE,
                imagePath = splitBill.imagePath,
                accountId = null // Let user assign account
            )

            transactionRepository.addTransaction(transaction)
            true
        } catch (_: Exception) { // Fix: Use underscore to indicate unused parameter
            false
        }
    }
}
