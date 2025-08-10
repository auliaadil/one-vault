package com.adilstudio.project.onevault.domain.usecase

import com.adilstudio.project.onevault.domain.model.PasswordGenerationRequest
import com.adilstudio.project.onevault.domain.repository.GPT2Repository
import com.adilstudio.project.onevault.domain.repository.PasswordGenerationRepository
import kotlinx.coroutines.flow.Flow

class GeneratePasswordUseCase(
    private val passwordRepository: PasswordGenerationRepository,
    private val gpt2Repository: GPT2Repository
) {
    suspend operator fun invoke(request: PasswordGenerationRequest): Flow<String> {
        // Ensure GPT2 model is initialized
        if (!gpt2Repository.isInitialized()) {
            gpt2Repository.initialize()
        }

        return passwordRepository.generatePassword(request)
    }
}
