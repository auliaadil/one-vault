package com.adilstudio.project.onevault.domain.usecase

import com.adilstudio.project.onevault.domain.repository.PasswordGenerationRepository

class GetDefaultPasswordPromptUseCase(
    private val repository: PasswordGenerationRepository
) {
    suspend operator fun invoke(): String? {
        return repository.getDefaultPrompt()
    }
}
