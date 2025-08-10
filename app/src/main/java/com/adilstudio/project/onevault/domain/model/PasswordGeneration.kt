package com.adilstudio.project.onevault.domain.model

data class PasswordGenerationRequest(
    val serviceName: String,
    val userName: String,
    val prompt: String
)

data class GeneratedPassword(
    val password: String,
    val serviceName: String,
    val userName: String,
    val prompt: String,
    val generatedAt: Long = System.currentTimeMillis()
)

data class PasswordPattern(
    val prompt: String,
    val isDefault: Boolean = false
)
