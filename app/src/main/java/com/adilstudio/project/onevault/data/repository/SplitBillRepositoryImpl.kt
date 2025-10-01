package com.adilstudio.project.onevault.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOneOrNull
import com.adilstudio.project.onevault.Database
import com.adilstudio.project.onevault.domain.model.SplitBill
import com.adilstudio.project.onevault.domain.repository.SplitBillRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.coroutines.flow.single

class SplitBillRepositoryImpl(private val database: Database) : SplitBillRepository {

    private val splitBillQueries = database.splitBillEntityQueries

    override fun getSplitBills(): Flow<List<SplitBill>> {
        return splitBillQueries.selectAll()
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { entities ->
                entities.map { entity ->
                    Json.decodeFromString<SplitBill>(entity.data_)
                }
            }
    }

    override suspend fun getSplitBillById(id: Long): SplitBill? {
        return splitBillQueries.selectById(id)
            .asFlow()
            .mapToOneOrNull(Dispatchers.IO)
            .map { entity ->
                entity?.let { Json.decodeFromString<SplitBill>(it.data_) }
            }
            .single()
    }

    override suspend fun addSplitBill(splitBill: SplitBill): Long {
        val data = Json.encodeToString(splitBill)
        splitBillQueries.insert(
            title = splitBill.title,
            vendor = splitBill.vendor,
            billDate = splitBill.billDate,
            imagePath = splitBill.imagePath,
            createdAt = splitBill.createdAt,
            updatedAt = splitBill.updatedAt,
            data_ = data
        )
        return splitBillQueries.lastInsertRowId().executeAsOne()
    }

    override suspend fun updateSplitBill(splitBill: SplitBill) {
        val data = Json.encodeToString(splitBill)
        splitBillQueries.update(
            id = splitBill.id,
            title = splitBill.title,
            vendor = splitBill.vendor,
            billDate = splitBill.billDate,
            imagePath = splitBill.imagePath,
            updatedAt = System.currentTimeMillis(),
            data_ = data
        )
    }

    override suspend fun deleteSplitBill(id: Long) {
        splitBillQueries.deleteById(id)
    }
}
