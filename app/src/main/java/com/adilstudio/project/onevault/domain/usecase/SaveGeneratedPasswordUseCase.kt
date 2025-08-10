package com.adilstudio.project.onevault.domain.usecase

import com.adilstudio.project.onevault.domain.model.GeneratedPassword
import com.adilstudio.project.onevault.domain.repository.PasswordGenerationRepository

class SaveGeneratedPasswordUseCase(
    private val repository: PasswordGenerationRepository
) {
    suspend operator fun invoke(password: GeneratedPassword) {
        repository.saveGeneratedPassword(password)
    }
}
