package com.adilstudio.project.onevault.domain.usecase

import com.adilstudio.project.onevault.domain.repository.CredentialRepository

class DeleteCredentialUseCase(private val repository: CredentialRepository) {
    suspend operator fun invoke(id: Long) = repository.deleteCredential(id)
}
