package com.adilstudio.project.onevault.domain.usecase

import com.adilstudio.project.onevault.domain.model.Account
import com.adilstudio.project.onevault.domain.repository.AccountRepository
import kotlinx.coroutines.flow.Flow

class GetAccountsUseCase(private val repository: AccountRepository) {
    operator fun invoke(): Flow<List<Account>> = repository.getAccounts()
}
