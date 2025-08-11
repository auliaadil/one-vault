package com.adilstudio.project.onevault.domain.usecase

import com.adilstudio.project.onevault.domain.repository.AccountRepository

class GetAccountsCountUseCase(private val repository: AccountRepository) {
    suspend operator fun invoke(): Int = repository.getAccountsCount()
}
