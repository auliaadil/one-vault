package com.adilstudio.project.onevault.domain.usecase

import com.adilstudio.project.onevault.domain.model.Credential
import com.adilstudio.project.onevault.domain.repository.CredentialRepository

class AddCredentialUseCase(private val repository: CredentialRepository) {
    suspend operator fun invoke(credential: Credential) = repository.addCredential(credential)
}

