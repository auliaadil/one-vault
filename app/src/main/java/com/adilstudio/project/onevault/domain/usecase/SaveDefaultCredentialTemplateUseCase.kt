package com.adilstudio.project.onevault.domain.usecase

import com.adilstudio.project.onevault.domain.repository.CredentialRepository

class SaveDefaultCredentialTemplateUseCase(
    private val repository: CredentialRepository
) {
    suspend operator fun invoke(templateJson: String) {
        repository.saveDefaultCredentialTemplate(templateJson)
    }
}
