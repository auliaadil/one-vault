package com.adilstudio.project.onevault.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOneOrNull
import com.adilstudio.project.onevault.Database
import com.adilstudio.project.onevault.domain.model.Bill
import com.adilstudio.project.onevault.domain.repository.BillRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.single

class BillRepositoryImpl(database: Database) : BillRepository {

    private val queries = database.billEntityQueries

    override fun getBills(): Flow<List<Bill>> {
        return queries.selectAll()
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { entities ->
                entities.map { entity ->
                    Bill(
                        id = entity.id,
                        title = entity.title,
                        category = entity.category,
                        amount = entity.amount,
                        vendor = entity.vendor,
                        billDate = entity.billDate,
                        imagePath = entity.imagePath
                    )
                }
            }
    }

    override suspend fun getBillById(id: Long): Bill? {
        return queries.selectById(id)
            .asFlow()
            .mapToOneOrNull(Dispatchers.IO)
            .map { entity ->
                entity?.let {
                    Bill(
                        id = it.id,
                        title = it.title,
                        category = it.category,
                        amount = it.amount,
                        vendor = it.vendor,
                        billDate = it.billDate,
                        imagePath = it.imagePath
                    )
                }
            }.single()
    }

    override suspend fun addBill(bill: Bill) {
        queries.insertBill(
            id = bill.id,
            title = bill.title,
            category = bill.category,
            amount = bill.amount,
            vendor = bill.vendor,
            billDate = bill.billDate,
            imagePath = bill.imagePath
        )
    }

    override suspend fun updateBill(bill: Bill) {
        queries.updateBill(
            title = bill.title,
            category = bill.category,
            amount = bill.amount,
            vendor = bill.vendor,
            billDate = bill.billDate,
            imagePath = bill.imagePath,
            id = bill.id
        )
    }

    override suspend fun deleteBill(id: Long) {
        queries.deleteBill(id)
    }
}
