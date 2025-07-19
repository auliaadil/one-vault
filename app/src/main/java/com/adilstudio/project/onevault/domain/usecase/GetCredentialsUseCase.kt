package com.adilstudio.project.onevault.domain.usecase

import com.adilstudio.project.onevault.domain.model.Credential
import com.adilstudio.project.onevault.domain.repository.CredentialRepository
import kotlinx.coroutines.flow.Flow

class GetCredentialsUseCase(private val repository: CredentialRepository) {
    operator fun invoke(): Flow<List<Credential>> = repository.getCredentials()
}

