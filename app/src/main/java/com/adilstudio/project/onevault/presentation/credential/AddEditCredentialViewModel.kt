package com.adilstudio.project.onevault.presentation.credential

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adilstudio.project.onevault.domain.model.Credential
import com.adilstudio.project.onevault.domain.model.GeneratedPassword
import com.adilstudio.project.onevault.domain.model.PasswordGenerationRequest
import com.adilstudio.project.onevault.domain.usecase.AddCredentialUseCase
import com.adilstudio.project.onevault.domain.usecase.GeneratePasswordUseCase
import com.adilstudio.project.onevault.domain.usecase.GetDefaultPasswordPromptUseCase
import com.adilstudio.project.onevault.domain.usecase.InitializeGPT2UseCase
import com.adilstudio.project.onevault.domain.usecase.SaveDefaultPasswordPromptUseCase
import com.adilstudio.project.onevault.domain.usecase.SaveGeneratedPasswordUseCase
import com.adilstudio.project.onevault.domain.usecase.UpdateCredentialUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class AddEditCredentialViewModel(
    private val addCredentialUseCase: AddCredentialUseCase,
    private val updateCredentialUseCase: UpdateCredentialUseCase,
    private val initializeGPT2UseCase: InitializeGPT2UseCase,
    private val generatePasswordUseCase: GeneratePasswordUseCase,
    private val saveGeneratedPasswordUseCase: SaveGeneratedPasswordUseCase,
    private val getDefaultPromptUseCase: GetDefaultPasswordPromptUseCase,
    private val saveDefaultPromptUseCase: SaveDefaultPasswordPromptUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddEditCredentialUiState())
    val uiState: StateFlow<AddEditCredentialUiState> = _uiState.asStateFlow()

    private var generationJob: Job? = null

    init {
        initializeModel()
        loadDefaultPrompt()
    }

    fun onServiceNameChanged(serviceName: String) {
        _uiState.value = _uiState.value.copy(
            serviceName = serviceName,
            isServiceNameValid = serviceName.isNotBlank(),
            error = null
        )
        updateCanGenerate()
    }

    fun onUsernameChanged(username: String) {
        _uiState.value = _uiState.value.copy(
            username = username,
            isUsernameValid = username.isNotBlank(),
            error = null
        )
        updateCanGenerate()
    }

    fun onPasswordChanged(password: String) {
        _uiState.value = _uiState.value.copy(
            password = password,
            isPasswordValid = password.isNotBlank(),
            error = null
        )
    }

    fun onPromptChanged(prompt: String) {
        _uiState.value = _uiState.value.copy(
            prompt = prompt,
            isPromptValid = prompt.isNotBlank(),
            error = null
        )
        updateCanGenerate()
    }

    fun onShowPasswordGeneratorChanged(show: Boolean) {
        _uiState.value = _uiState.value.copy(
            showPasswordGenerator = show
        )
        if (show) {
            loadDefaultPrompt()
        }
    }

    fun onGeneratePassword() {
        val currentState = _uiState.value
        if (currentState.isGenerating || !currentState.canGenerate) return

        generationJob?.cancel()

        _uiState.value = currentState.copy(
            isGenerating = true,
            error = null
        )

        val request = PasswordGenerationRequest(
            serviceName = currentState.serviceName,
            userName = currentState.username,
            prompt = currentState.prompt
        )

        generationJob = viewModelScope.launch {
            try {
                var generatedText = ""
                generatePasswordUseCase(request)
                    .catch { throwable ->
                        _uiState.value = _uiState.value.copy(
                            isGenerating = false,
                            error = throwable.message ?: "Unknown error occurred"
                        )
                    }
                    .collect { token ->
                        generatedText += token
                        // Update password field with generated text
                        _uiState.value = _uiState.value.copy(
                            password = generatedText.trim(),
                            isPasswordValid = generatedText.trim().isNotBlank()
                        )
                    }

                _uiState.value = _uiState.value.copy(
                    isGenerating = false,
                    password = generatedText.trim(),
                    isPasswordValid = generatedText.trim().isNotBlank()
                )

                // Save the prompt as default for future use
                if (currentState.prompt.isNotBlank()) {
                    saveDefaultPromptUseCase(currentState.prompt)
                }
            } catch (throwable: Throwable) {
                _uiState.value = _uiState.value.copy(
                    isGenerating = false,
                    error = "Failed to generate password: ${throwable.message}"
                )
            }
        }
    }

    fun onStopGeneration() {
        generationJob?.cancel()
        _uiState.value = _uiState.value.copy(isGenerating = false)
    }

    fun onUseDefaultPrompt() {
        loadDefaultPrompt()
    }

    fun onSaveCredential() {
        val currentState = _uiState.value
        if (!currentState.canSave) return

        viewModelScope.launch {
            try {
                val credential = if (currentState.isEditing) {
                    currentState.credential?.copy(
                        serviceName = currentState.serviceName,
                        username = currentState.username,
                        encryptedPassword = currentState.password // This will be encrypted in the screen
                    )
                } else {
                    Credential(
                        id = 0L,
                        serviceName = currentState.serviceName,
                        username = currentState.username,
                        encryptedPassword = currentState.password // This will be encrypted in the screen
                    )
                }

                if (credential != null) {
                    if (currentState.isEditing) {
                        updateCredentialUseCase(credential)
                    } else {
                        addCredentialUseCase(credential)
                    }

                    _uiState.value = currentState.copy(
                        isSaved = true,
                        error = null
                    )
                }
            } catch (throwable: Throwable) {
                _uiState.value = currentState.copy(
                    error = "Failed to save credential: ${throwable.message}"
                )
            }
        }
    }

    fun initializeForEdit(credential: Credential) {
        _uiState.value = _uiState.value.copy(
            credential = credential,
            serviceName = credential.serviceName,
            username = credential.username,
            password = "", // Will be set by decryption in the screen
            isEditing = true,
            isServiceNameValid = true,
            isUsernameValid = true,
            isPasswordValid = true
        )
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    private fun initializeModel() {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isModelInitialized = false)
                initializeGPT2UseCase()
                _uiState.value = _uiState.value.copy(
                    isModelInitialized = true,
                    error = null
                )
                updateCanGenerate()
            } catch (throwable: Throwable) {
                _uiState.value = _uiState.value.copy(
                    isModelInitialized = false,
                    error = "Failed to initialize AI model: ${throwable.message}"
                )
            }
        }
    }

    private fun loadDefaultPrompt() {
        viewModelScope.launch {
            try {
                val defaultPrompt = getDefaultPromptUseCase()
                if (!defaultPrompt.isNullOrBlank()) {
                    _uiState.value = _uiState.value.copy(
                        prompt = defaultPrompt,
                        isPromptValid = true
                    )
                    updateCanGenerate()
                } else {
                    // Set a basic default prompt if none exists
                    val basicPrompt = """Generate a secure password using:
- Characters from service name: {serviceName}
- Characters from username: {userName}
- Add numbers and special characters for security"""
                    _uiState.value = _uiState.value.copy(
                        prompt = basicPrompt,
                        isPromptValid = true
                    )
                    updateCanGenerate()
                }
            } catch (throwable: Throwable) {
                // Use basic prompt if loading fails
                val basicPrompt = """Generate a secure password using:
- Characters from service name: {serviceName}
- Characters from username: {userName}
- Add numbers and special characters for security"""
                _uiState.value = _uiState.value.copy(
                    prompt = basicPrompt,
                    isPromptValid = true
                )
                updateCanGenerate()
            }
        }
    }

    private fun updateCanGenerate() {
        val currentState = _uiState.value
        val canGenerate = currentState.isModelInitialized &&
                !currentState.isGenerating &&
                currentState.showPasswordGenerator &&
                currentState.isServiceNameValid &&
                currentState.isUsernameValid &&
                currentState.isPromptValid &&
                currentState.serviceName.isNotBlank() &&
                currentState.username.isNotBlank() &&
                currentState.prompt.isNotBlank()

        _uiState.value = currentState.copy(canGenerate = canGenerate)
    }

    private val AddEditCredentialUiState.canSave: Boolean
        get() = isServiceNameValid && isUsernameValid && isPasswordValid &&
                serviceName.isNotBlank() && username.isNotBlank() && password.isNotBlank()

    override fun onCleared() {
        super.onCleared()
        generationJob?.cancel()
    }
}
