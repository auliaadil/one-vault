package com.adilstudio.project.onevault.domain.usecase

import com.adilstudio.project.onevault.domain.repository.AccountRepository

class DeleteAccountUseCase(private val repository: AccountRepository) {
    suspend operator fun invoke(accountId: Long) = repository.deleteAccount(accountId)
}
