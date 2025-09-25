package com.adilstudio.project.onevault.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOneOrNull
import com.adilstudio.project.onevault.Database
import com.adilstudio.project.onevault.domain.model.Account
import com.adilstudio.project.onevault.domain.repository.AccountRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.single

class AccountRepositoryImpl(database: Database) : AccountRepository {

    private val queries = database.accountEntityQueries

    override fun getAccounts(): Flow<List<Account>> {
        return queries.selectAll()
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { entities ->
                entities.map { entity ->
                    Account(
                        id = entity.id,
                        name = entity.name,
                        amount = entity.amount,
                        description = entity.description,
                        isEditable = entity.isEditable == 1L,
                        createdAt = entity.createdAt,
                        updatedAt = entity.updatedAt
                    )
                }
            }
    }

    override suspend fun getAccountById(id: Long): Account? {
        return queries.selectById(id)
            .asFlow()
            .mapToOneOrNull(Dispatchers.IO)
            .map { entity ->
                entity?.let {
                    Account(
                        id = it.id,
                        name = it.name,
                        amount = it.amount,
                        description = it.description,
                        isEditable = it.isEditable == 1L,
                        createdAt = it.createdAt,
                        updatedAt = it.updatedAt
                    )
                }
            }.single()
    }

    override suspend fun addAccount(account: Account) {
        queries.insertAccount(
            id = account.id,
            name = account.name,
            amount = account.amount,
            description = account.description,
            isEditable = if (account.isEditable) 1L else 0L,
            createdAt = account.createdAt,
            updatedAt = account.updatedAt
        )
    }

    override suspend fun updateAccount(account: Account) {
        queries.updateAccount(
            name = account.name,
            amount = account.amount,
            description = account.description,
            isEditable = if (account.isEditable) 1L else 0L,
            updatedAt = account.updatedAt,
            id = account.id
        )
    }

    override suspend fun deleteAccount(id: Long) {
        queries.deleteAccount(id)
    }

    override suspend fun getAccountsCount(): Int {
        return queries.selectAll().executeAsList().size
    }

    override suspend fun updateAccountBalance(accountId: Long, newBalance: Double) {
        queries.updateAccountBalance(
            amount = newBalance,
            updatedAt = System.currentTimeMillis(),
            id = accountId
        )
    }
}
