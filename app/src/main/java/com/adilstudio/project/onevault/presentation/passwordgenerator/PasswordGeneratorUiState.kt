package com.adilstudio.project.onevault.presentation.passwordgenerator

data class PasswordGeneratorUiState(
    val serviceName: String = "",
    val userName: String = "",
    val prompt: String = "",
    val generatedPassword: String = "",
    val isGenerating: Boolean = false,
    val isInitialized: Boolean = false,
    val error: String? = null,
    val isServiceNameValid: Boolean = true,
    val isUserNameValid: Boolean = true,
    val isPromptValid: Boolean = true,
    val canGenerate: Boolean = false,
    val canSave: Boolean = false
)
