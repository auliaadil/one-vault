package com.adilstudio.project.onevault.domain.repository

import com.adilstudio.project.onevault.domain.model.LLMRequest
import com.adilstudio.project.onevault.domain.model.LLMResponse
import kotlinx.coroutines.flow.Flow

interface LLMRepository {
    suspend fun initializeModel(modelPath: String): Boolean
    suspend fun generateText(request: LLMRequest): Flow<LLMResponse>
    suspend fun isModelLoaded(): Boolean
    suspend fun unloadModel()
    suspend fun getAvailableModels(): List<String>
}
