package com.adilstudio.project.onevault.domain.repository

import com.adilstudio.project.onevault.domain.model.GenerationConfig
import kotlinx.coroutines.flow.Flow

interface GPT2Repository {
    suspend fun initialize()
    fun generateText(prompt: String, config: GenerationConfig): Flow<String>
    fun getRandomPrompt(): String
    fun isInitialized(): Boolean
}