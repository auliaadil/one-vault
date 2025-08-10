package com.adilstudio.project.onevault.domain.usecase

import com.adilstudio.project.onevault.domain.model.LLMRequest
import com.adilstudio.project.onevault.domain.model.LLMResponse
import com.adilstudio.project.onevault.domain.repository.LLMRepository
import kotlinx.coroutines.flow.Flow

class GenerateTextUseCase(
    private val llmRepository: LLMRepository
) {
    suspend operator fun invoke(request: LLMRequest): Flow<LLMResponse> {
        return llmRepository.generateText(request)
    }
}

class InitializeLLMUseCase(
    private val llmRepository: LLMRepository
) {
    suspend operator fun invoke(modelPath: String): Boolean {
        return llmRepository.initializeModel(modelPath)
    }
}

class GetAvailableModelsUseCase(
    private val llmRepository: LLMRepository
) {
    suspend operator fun invoke(): List<String> {
        return llmRepository.getAvailableModels()
    }
}

class CheckModelStatusUseCase(
    private val llmRepository: LLMRepository
) {
    suspend operator fun invoke(): Boolean {
        return llmRepository.isModelLoaded()
    }
}
