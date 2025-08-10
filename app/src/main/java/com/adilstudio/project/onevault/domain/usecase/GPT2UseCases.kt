package com.adilstudio.project.onevault.domain.usecase

import com.adilstudio.project.onevault.domain.model.GenerationConfig
import com.adilstudio.project.onevault.domain.repository.GPT2Repository
import kotlinx.coroutines.flow.Flow

class GenerateGPT2TextUseCase(
    private val repository: GPT2Repository
) {
    operator fun invoke(prompt: String, config: GenerationConfig): Flow<String> {
        return repository.generateText(prompt, config)
    }
}