package com.adilstudio.project.onevault.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOneOrNull
import com.adilstudio.project.onevault.Database
import com.adilstudio.project.onevault.domain.model.Transaction
import com.adilstudio.project.onevault.domain.model.TransactionType
import com.adilstudio.project.onevault.domain.repository.TransactionRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.single

class TransactionRepositoryImpl(private val database: Database) : TransactionRepository {

    private val transactionQueries = database.transactionEntityQueries
    private val accountQueries = database.accountEntityQueries

    override fun getTransactions(): Flow<List<Transaction>> {
        return transactionQueries.selectAll()
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { entities ->
                entities.map { entity ->
                    Transaction(
                        id = entity.id,
                        title = entity.title,
                        categoryId = entity.categoryId,
                        amount = entity.amount,
                        merchant = entity.merchant,
                        date = entity.date,
                        type = TransactionType.valueOf(entity.type),
                        imagePath = entity.imagePath,
                        accountId = entity.accountId
                    )
                }
            }
    }

    override suspend fun getTransactionById(id: Long): Transaction? {
        return transactionQueries.selectById(id)
            .asFlow()
            .mapToOneOrNull(Dispatchers.IO)
            .map { entity ->
                entity?.let {
                    Transaction(
                        id = it.id,
                        title = it.title,
                        categoryId = it.categoryId,
                        amount = it.amount,
                        merchant = it.merchant,
                        date = it.date,
                        type = TransactionType.valueOf(it.type),
                        imagePath = it.imagePath,
                        accountId = it.accountId
                    )
                }
            }.single()
    }

    override suspend fun addTransaction(transaction: Transaction) {
        // Add the transaction using direct query
        transactionQueries.insertTransaction(
            title = transaction.title,
            categoryId = transaction.categoryId,
            amount = transaction.amount,
            merchant = transaction.merchant,
            date = transaction.date,
            type = transaction.type.name,
            imagePath = transaction.imagePath,
            accountId = transaction.accountId
        )
        // Deduct from account if specified
        transaction.accountId?.let { accountId ->
            deductFromAccount(accountId, transaction.amount)
        }
    }

    override suspend fun updateTransaction(transaction: Transaction) {
        // Get existing transaction for comparison using direct query
        val existingTransaction = transactionQueries.selectById(transaction.id).executeAsOneOrNull()?.let { entity ->
            Transaction(
                id = entity.id,
                title = entity.title,
                categoryId = entity.categoryId,
                amount = entity.amount,
                merchant = entity.merchant,
                date = entity.date,
                type = TransactionType.valueOf(entity.type),
                imagePath = entity.imagePath,
                accountId = entity.accountId
            )
        }

        if (existingTransaction != null) {
            // Handle account balance adjustments
            when {
                // Account changed (including from null to account or account to null)
                existingTransaction.accountId != transaction.accountId -> {
                    // Restore previous account balance if it existed
                    existingTransaction.accountId?.let { oldAccountId ->
                        restoreToAccount(oldAccountId, existingTransaction.amount)
                    }
                    // Deduct from new account if it exists
                    transaction.accountId?.let { newAccountId ->
                        deductFromAccount(newAccountId, transaction.amount)
                    }
                }
                // Same account but amount changed
                existingTransaction.accountId == transaction.accountId && transaction.accountId != null -> {
                    val amountDifference = transaction.amount - existingTransaction.amount
                    if (amountDifference != 0.0) {
                        deductFromAccount(transaction.accountId, amountDifference)
                    }
                }
                // No account involved, just update the transaction
            }
        }

        // Update the transaction using direct query
        transactionQueries.updateTransaction(
            title = transaction.title,
            categoryId = transaction.categoryId,
            amount = transaction.amount,
            merchant = transaction.merchant,
            date = transaction.date,
            type = transaction.type.name,
            imagePath = transaction.imagePath,
            accountId = transaction.accountId,
            id = transaction.id
        )
    }

    override suspend fun deleteTransaction(id: Long) {
        // Get the transaction to restore account balance using direct query
        val transaction = transactionQueries.selectById(id).executeAsOneOrNull()?.let { entity ->
            Transaction(
                id = entity.id,
                title = entity.title,
                categoryId = entity.categoryId,
                amount = entity.amount,
                merchant = entity.merchant,
                date = entity.date,
                type = TransactionType.valueOf(entity.type),
                imagePath = entity.imagePath,
                accountId = entity.accountId
            )
        }

        // Delete the transaction using direct query
        transactionQueries.deleteTransaction(id)

        // Restore to account if accountId was specified
        transaction?.accountId?.let { accountId ->
            restoreToAccount(accountId, transaction.amount)
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
