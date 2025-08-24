package com.adilstudio.project.onevault.presentation.credential

import com.adilstudio.project.onevault.domain.model.Credential

data class AddEditCredentialUiState(
    val credential: Credential? = null,
    val serviceName: String = "",
    val username: String = "",
    val password: String = "",
    val prompt: String = "",
    val showPasswordGenerator: Boolean = false,
    val isGenerating: Boolean = false,
    val isModelInitialized: Boolean = false,
    val isEditing: Boolean = false,
    val isSaved: Boolean = false,
    val error: String? = null,
    val isServiceNameValid: Boolean = true,
    val isUsernameValid: Boolean = true,
    val isPasswordValid: Boolean = true,
    val isPromptValid: Boolean = true,
    val canGenerate: Boolean = false
)
