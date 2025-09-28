package com.adilstudio.project.onevault.domain.usecase

import com.adilstudio.project.onevault.domain.repository.CredentialRepository
import kotlinx.coroutines.flow.Flow

class GetDefaultCredentialTemplateUseCase(
    private val repository: CredentialRepository
) {
    operator fun invoke(): Flow<String?> {
        return repository.getDefaultCredentialTemplateFlow()
    }
}
