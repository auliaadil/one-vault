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

class BillRepositoryImpl(private val database: Database) : BillRepository {

    private val billQueries = database.billEntityQueries
    private val accountQueries = database.accountEntityQueries

    override fun getBills(): Flow<List<Bill>> {
        return billQueries.selectAll()
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
                        imagePath = entity.imagePath,
                        accountId = entity.accountId
                    )
                }
            }
    }

    override suspend fun getBillById(id: Long): Bill? {
        return billQueries.selectById(id)
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
                        imagePath = it.imagePath,
                        accountId = it.accountId
                    )
                }
            }.single()
    }

    override suspend fun addBill(bill: Bill) {
        database.transaction {
            // Add the bill using direct query (non-suspend)
            billQueries.insertBill(
                id = bill.id,
                title = bill.title,
                category = bill.category,
                amount = bill.amount,
                vendor = bill.vendor,
                billDate = bill.billDate,
                imagePath = bill.imagePath,
                accountId = bill.accountId
            )
            // Deduct from account if specified
            bill.accountId?.let { accountId ->
                deductFromAccount(accountId, bill.amount)
            }
        }
    }

    override suspend fun updateBill(bill: Bill) {
        database.transaction {
            // Get existing bill for comparison using direct query (non-suspend)
            val existingBill = billQueries.selectById(bill.id).executeAsOneOrNull()?.let { entity ->
                Bill(
                    id = entity.id,
                    title = entity.title,
                    category = entity.category,
                    amount = entity.amount,
                    vendor = entity.vendor,
                    billDate = entity.billDate,
                    imagePath = entity.imagePath,
                    accountId = entity.accountId
                )
            }

            if (existingBill != null) {
                // Handle account balance adjustments
                when {
                    // Account changed (including from null to account or account to null)
                    existingBill.accountId != bill.accountId -> {
                        // Restore previous account balance if it existed
                        existingBill.accountId?.let { oldAccountId ->
                            restoreToAccount(oldAccountId, existingBill.amount)
                        }
                        // Deduct from new account if it exists
                        bill.accountId?.let { newAccountId ->
                            deductFromAccount(newAccountId, bill.amount)
                        }
                    }
                    // Same account but amount changed
                    existingBill.accountId == bill.accountId && bill.accountId != null -> {
                        val amountDifference = bill.amount - existingBill.amount
                        if (amountDifference != 0.0) {
                            deductFromAccount(bill.accountId, amountDifference)
                        }
                    }
                    // No account involved, just update the bill
                }
            }

            // Update the bill using direct query (non-suspend)
            billQueries.updateBill(
                title = bill.title,
                category = bill.category,
                amount = bill.amount,
                vendor = bill.vendor,
                billDate = bill.billDate,
                imagePath = bill.imagePath,
                accountId = bill.accountId,
                id = bill.id
            )
        }
    }

    override suspend fun deleteBill(id: Long) {
        database.transaction {
            // Get the bill to restore account balance using direct query (non-suspend)
            val bill = billQueries.selectById(id).executeAsOneOrNull()?.let { entity ->
                Bill(
                    id = entity.id,
                    title = entity.title,
                    category = entity.category,
                    amount = entity.amount,
                    vendor = entity.vendor,
                    billDate = entity.billDate,
                    imagePath = entity.imagePath,
                    accountId = entity.accountId
                )
            }

            // Delete the bill using direct query (non-suspend)
            billQueries.deleteBill(id)

            // Restore to account if accountId was specified
            bill?.accountId?.let { accountId ->
                restoreToAccount(accountId, bill.amount)
            }
        }
    }

    // Private helper methods for account balance management
    private fun deductFromAccount(accountId: Long, amount: Double) {
        val account = accountQueries.selectById(accountId).executeAsOneOrNull()
        account?.let {
            val newBalance = it.amount - amount
            accountQueries.updateAccountBalance(
                amount = newBalance,
                updatedAt = System.currentTimeMillis(),
                id = accountId
            )
        }
    }

    private fun restoreToAccount(accountId: Long, amount: Double) {
        val account = accountQueries.selectById(accountId).executeAsOneOrNull()
        account?.let {
            val newBalance = it.amount + amount
            accountQueries.updateAccountBalance(
                amount = newBalance,
                updatedAt = System.currentTimeMillis(),
                id = accountId
            )
        }
    }
}
