package com.adilstudio.project.onevault.domain.repository

import com.adilstudio.project.onevault.domain.model.Account
import kotlinx.coroutines.flow.Flow

interface AccountRepository {
    fun getAccounts(): Flow<List<Account>>
    suspend fun getAccountById(id: String): Account?
    suspend fun addAccount(account: Account)
    suspend fun updateAccount(account: Account)
    suspend fun deleteAccount(id: String)
    suspend fun getAccountsCount(): Int
    suspend fun updateAccountBalance(accountId: String, newBalance: Double)
}
