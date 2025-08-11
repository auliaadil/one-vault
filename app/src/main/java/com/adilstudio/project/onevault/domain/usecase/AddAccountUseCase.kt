package com.adilstudio.project.onevault.domain.usecase

import com.adilstudio.project.onevault.domain.model.Account
import com.adilstudio.project.onevault.domain.repository.AccountRepository

class AddAccountUseCase(private val repository: AccountRepository) {
    suspend operator fun invoke(account: Account) = repository.addAccount(account)
}
