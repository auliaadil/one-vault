package com.adilstudio.project.onevault.domain.repository

import com.adilstudio.project.onevault.domain.model.GeneratedPassword
import com.adilstudio.project.onevault.domain.model.PasswordGenerationRequest
import com.adilstudio.project.onevault.domain.model.PasswordPattern
import kotlinx.coroutines.flow.Flow

interface PasswordGenerationRepository {
    suspend fun generatePassword(request: PasswordGenerationRequest): Flow<String>
    suspend fun saveGeneratedPassword(password: GeneratedPassword)
    suspend fun getPasswordHistory(serviceName: String): List<GeneratedPassword>
    suspend fun saveDefaultPrompt(prompt: String)
    suspend fun getDefaultPrompt(): String?
    suspend fun getPasswordPatterns(): List<PasswordPattern>
}
