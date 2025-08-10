package com.adilstudio.project.onevault.presentation.passwordgenerator

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adilstudio.project.onevault.domain.model.GeneratedPassword
import com.adilstudio.project.onevault.domain.model.PasswordGenerationRequest
import com.adilstudio.project.onevault.domain.usecase.GeneratePasswordUseCase
import com.adilstudio.project.onevault.domain.usecase.GetDefaultPasswordPromptUseCase
import com.adilstudio.project.onevault.domain.usecase.InitializeGPT2UseCase
import com.adilstudio.project.onevault.domain.usecase.SaveDefaultPasswordPromptUseCase
import com.adilstudio.project.onevault.domain.usecase.SaveGeneratedPasswordUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class PasswordGeneratorViewModel(
    private val initializeGPT2UseCase: InitializeGPT2UseCase,
    private val generatePasswordUseCase: GeneratePasswordUseCase,
    private val saveGeneratedPasswordUseCase: SaveGeneratedPasswordUseCase,
    private val getDefaultPromptUseCase: GetDefaultPasswordPromptUseCase,
    private val saveDefaultPromptUseCase: SaveDefaultPasswordPromptUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(PasswordGeneratorUiState())
    val uiState: StateFlow<PasswordGeneratorUiState> = _uiState.asStateFlow()

    private var generationJob: Job? = null

    init {
        initializeModel()
        loadDefaultPrompt()
    }

    fun onServiceNameChanged(serviceName: String) {
        _uiState.value = _uiState.value.copy(
            serviceName = serviceName,
            isServiceNameValid = serviceName.isNotBlank(),
            generatedPassword = "", // Clear previous password
            error = null
        )
        updateCanGenerate()
    }

    fun onUserNameChanged(userName: String) {
        _uiState.value = _uiState.value.copy(
            userName = userName,
            isUserNameValid = userName.isNotBlank(),
            generatedPassword = "", // Clear previous password
            error = null
        )
        updateCanGenerate()
    }

    fun onPromptChanged(prompt: String) {
        _uiState.value = _uiState.value.copy(
            prompt = prompt,
            isPromptValid = prompt.isNotBlank(),
            generatedPassword = "", // Clear previous password
            error = null
        )
        updateCanGenerate()
    }

    fun onGeneratePassword() {
        val currentState = _uiState.value
        if (currentState.isGenerating || !currentState.canGenerate) return

        generationJob?.cancel()

        _uiState.value = currentState.copy(
            generatedPassword = "",
            isGenerating = true,
            error = null,
            canSave = false
        )

        val request = PasswordGenerationRequest(
            serviceName = currentState.serviceName,
            userName = currentState.userName,
            prompt = currentState.prompt
        )

        generationJob = viewModelScope.launch {
            generatePasswordUseCase(request)
                .catch { throwable ->
                    _uiState.value = _uiState.value.copy(
                        isGenerating = false,
                        error = throwable.message ?: "Unknown error occurred"
                    )
                }
                .collect { token ->
                    val newPassword = _uiState.value.generatedPassword + token
                    _uiState.value = _uiState.value.copy(
                        generatedPassword = newPassword.trim(),
                        canSave = newPassword.trim().isNotBlank()
                    )
                }

            _uiState.value = _uiState.value.copy(
                isGenerating = false,
                canSave = _uiState.value.generatedPassword.isNotBlank()
            )
        }
    }

    fun onSavePassword() {
        val currentState = _uiState.value
        if (!currentState.canSave) return

        viewModelScope.launch {
            try {
                val generatedPassword = GeneratedPassword(
                    password = currentState.generatedPassword,
                    serviceName = currentState.serviceName,
                    userName = currentState.userName,
                    prompt = currentState.prompt
                )

                saveGeneratedPasswordUseCase(generatedPassword)

                // Save the prompt as default for future use
                saveDefaultPromptUseCase(currentState.prompt)

                _uiState.value = currentState.copy(
                    error = null
                )

                // You might want to show a success message or navigate back
            } catch (throwable: Throwable) {
                _uiState.value = currentState.copy(
                    error = "Failed to save password: ${throwable.message}"
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

    private fun initializeModel() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isInitialized = false)
            initializeGPT2UseCase()
            _uiState.value = _uiState.value.copy(
                isInitialized = true,
                error = null
            )
            updateCanGenerate()
//            try {
//                _uiState.value = _uiState.value.copy(isInitialized = false)
//                initializeGPT2UseCase()
//                _uiState.value = _uiState.value.copy(
//                    isInitialized = true,
//                    error = null
//                )
//                updateCanGenerate()
//            } catch (throwable: Throwable) {
//                _uiState.value = _uiState.value.copy(
//                    isInitialized = false,
//                    error = "Failed to initialize model: ${throwable.message}"
//                )
//            }
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
                }
            } catch (throwable: Throwable) {
                // Silently handle error, use empty prompt
            }
        }
    }

    private fun updateCanGenerate() {
        val currentState = _uiState.value
        val canGenerate = currentState.isInitialized &&
                !currentState.isGenerating &&
                currentState.isServiceNameValid &&
                currentState.isUserNameValid &&
                currentState.isPromptValid &&
                currentState.serviceName.isNotBlank() &&
                currentState.userName.isNotBlank() &&
                currentState.prompt.isNotBlank()

        _uiState.value = currentState.copy(canGenerate = canGenerate)
    }

    override fun onCleared() {
        super.onCleared()
        generationJob?.cancel()
    }
}
