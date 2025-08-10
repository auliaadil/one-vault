package com.adilstudio.project.onevault.data.repository

import com.adilstudio.project.onevault.data.local.PreferenceManager
import com.adilstudio.project.onevault.data.source.GPT2ModelDataSource
import com.adilstudio.project.onevault.domain.model.GeneratedPassword
import com.adilstudio.project.onevault.domain.model.GenerationConfig
import com.adilstudio.project.onevault.domain.model.GenerationStrategy
import com.adilstudio.project.onevault.domain.model.PasswordGenerationRequest
import com.adilstudio.project.onevault.domain.model.PasswordPattern
import com.adilstudio.project.onevault.domain.repository.PasswordGenerationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PasswordGenerationRepositoryImpl(
    private val gpt2DataSource: GPT2ModelDataSource,
    private val preferenceManager: PreferenceManager
) : PasswordGenerationRepository {

    companion object {
        private const val DEFAULT_PROMPT_KEY = "password_generation_default_prompt"
        private const val PASSWORD_HISTORY_KEY = "password_generation_history"

        private const val DEFAULT_PROMPT = """Generate a secure password with the following pattern:
- Use characters from service name: {serviceName}
- Use characters from username: {userName}
- Mix uppercase and lowercase letters
- Add numbers and special characters
- Length should be 8-12 characters
- Make it memorable but secure

Examples of good patterns:
- Take first 3-4 letters from service name + first 3-4 letters from username + numbers + special chars
- Combine service and username creatively with secure additions

Generate one password suggestion:"""
    }

    override suspend fun generatePassword(request: PasswordGenerationRequest): Flow<String> {
        val enhancedPrompt = buildPrompt(request)

        val config = GenerationConfig(
            strategy = GenerationStrategy.TOP_K,
            topK = 30,
            maxTokens = 20 // Short output for password generation
        )

        return gpt2DataSource.generateText(enhancedPrompt, config)
            .map { token -> token.trim() }
    }

    override suspend fun saveGeneratedPassword(password: GeneratedPassword) {
        val existingHistory = getPasswordHistoryFromPrefs()
        val updatedHistory = (listOf(password) + existingHistory).take(50) // Keep last 50
        savePasswordHistoryToPrefs(updatedHistory)
    }

    override suspend fun getPasswordHistory(serviceName: String): List<GeneratedPassword> {
        return getPasswordHistoryFromPrefs()
            .filter { it.serviceName.equals(serviceName, ignoreCase = true) }
            .sortedByDescending { it.generatedAt }
    }

    override suspend fun saveDefaultPrompt(prompt: String) {
        preferenceManager.saveString(DEFAULT_PROMPT_KEY, prompt)
    }

    override suspend fun getDefaultPrompt(): String? {
        return preferenceManager.getString(DEFAULT_PROMPT_KEY) ?: DEFAULT_PROMPT
    }

    override suspend fun getPasswordPatterns(): List<PasswordPattern> {
        val defaultPrompt = getDefaultPrompt()
        val patterns = mutableListOf<PasswordPattern>()

        // Add default prompt
        defaultPrompt?.let {
            patterns.add(PasswordPattern(it, isDefault = true))
        }

        // Add some predefined patterns
        patterns.addAll(getPredefinedPatterns())

        return patterns
    }

    private fun buildPrompt(request: PasswordGenerationRequest): String {
        var prompt = request.prompt

        // Replace placeholders
        prompt = prompt.replace("{serviceName}", request.serviceName)
        prompt = prompt.replace("{userName}", request.userName)

        // Add context for better generation
        prompt += "\n\nService: ${request.serviceName}"
        prompt += "\nUsername: ${request.userName}"
        prompt += "\n\nPassword:"

        return prompt
    }

    private suspend fun getPasswordHistoryFromPrefs(): List<GeneratedPassword> {
        // For simplicity, we'll store as JSON string
        // In a real app, you might want to use a proper database
        val historyJson = preferenceManager.getString(PASSWORD_HISTORY_KEY)
        return if (historyJson.isNullOrEmpty()) {
            emptyList()
        } else {
            try {
                // Parse JSON to list of GeneratedPassword
                // For now, return empty list - you can implement JSON parsing later
                emptyList()
            } catch (e: Exception) {
                emptyList()
            }
        }
    }

    private suspend fun savePasswordHistoryToPrefs(history: List<GeneratedPassword>) {
        // Convert to JSON and save
        // For now, we'll skip this implementation
        // preferenceManager.saveString(PASSWORD_HISTORY_KEY, historyJson)
    }

    private fun getPredefinedPatterns(): List<PasswordPattern> {
        return listOf(
            PasswordPattern(
                """Generate a password with pattern:
- Use first 4 characters from service name: {serviceName}
- Use first 4 characters from username: {userName}
- Add random numbers (2-3 digits)
- Add one special character (!@#$)
- Mix uppercase and lowercase

Example format: ServiceUser123!

Generate password:""",
                isDefault = false
            ),
            PasswordPattern(
                """Create a secure password following this pattern:
- Take alternating characters from {serviceName} and {userName}
- Add current year digits
- Include special characters
- Ensure 8-12 character length

Generate password:""",
                isDefault = false
            ),
            PasswordPattern(
                """Develop a password with pattern:
- Use characters from service name: {serviceName}
- Use characters from username: {userName}
- Add default suffix 123!
- Mix case appropriately

Generate password:""",
                isDefault = false
            )
        )
    }
}
