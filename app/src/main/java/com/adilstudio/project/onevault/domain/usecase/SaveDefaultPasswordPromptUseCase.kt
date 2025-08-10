package com.adilstudio.project.onevault.domain.usecase

import com.adilstudio.project.onevault.domain.repository.PasswordGenerationRepository

class SaveDefaultPasswordPromptUseCase(
    private val repository: PasswordGenerationRepository
) {
    suspend operator fun invoke(prompt: String) {
        repository.saveDefaultPrompt(prompt)
    }
}
