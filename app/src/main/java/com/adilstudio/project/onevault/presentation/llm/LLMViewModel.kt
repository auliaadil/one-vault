package com.adilstudio.project.onevault.presentation.llm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adilstudio.project.onevault.domain.model.LLMRequest
import com.adilstudio.project.onevault.domain.model.LLMResponse
import com.adilstudio.project.onevault.domain.usecase.CheckModelStatusUseCase
import com.adilstudio.project.onevault.domain.usecase.GenerateTextUseCase
import com.adilstudio.project.onevault.domain.usecase.GetAvailableModelsUseCase
import com.adilstudio.project.onevault.domain.usecase.InitializeLLMUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class LLMUiState(
    val isModelLoaded: Boolean = false,
    val isLoading: Boolean = false,
    val availableModels: List<String> = emptyList(),
    val selectedModel: String? = null,
    val currentResponse: String = "",
    val isGenerating: Boolean = false,
    val error: String? = null,
    val chatHistory: List<ChatMessage> = emptyList()
)

data class ChatMessage(
    val text: String,
    val isUser: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)

class LLMViewModel(
    private val generateTextUseCase: GenerateTextUseCase,
    private val initializeLLMUseCase: InitializeLLMUseCase,
    private val getAvailableModelsUseCase: GetAvailableModelsUseCase,
    private val checkModelStatusUseCase: CheckModelStatusUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(LLMUiState())
    val uiState: StateFlow<LLMUiState> = _uiState.asStateFlow()

    init {
        loadAvailableModels()
        checkModelStatus()
    }

    fun loadAvailableModels() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val models = getAvailableModelsUseCase()
                _uiState.value = _uiState.value.copy(
                    availableModels = models,
                    isLoading = false,
                    error = null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to load models"
                )
            }
        }
    }

    fun initializeModel(modelPath: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val success = initializeLLMUseCase(modelPath)
                _uiState.value = _uiState.value.copy(
                    isModelLoaded = success,
                    selectedModel = if (success) modelPath else null,
                    isLoading = false,
                    error = if (!success) "Failed to initialize model" else null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isModelLoaded = false,
                    error = e.message ?: "Failed to initialize model"
                )
            }
        }
    }

    fun generateText(prompt: String, maxTokens: Int = 256, temperature: Float = 0.7f) {
        if (prompt.isBlank()) return

        viewModelScope.launch {
            // Add user message to chat history
            val userMessage = ChatMessage(text = prompt, isUser = true)
            _uiState.value = _uiState.value.copy(
                chatHistory = _uiState.value.chatHistory + userMessage,
                isGenerating = true,
                currentResponse = "",
                error = null
            )

            try {
                val request = LLMRequest(
                    prompt = prompt,
                    maxTokens = maxTokens,
                    temperature = temperature
                )

                generateTextUseCase(request).collect { response ->
                    if (response.error != null) {
                        _uiState.value = _uiState.value.copy(
                            isGenerating = false,
                            error = response.error
                        )
                    } else {
                        _uiState.value = _uiState.value.copy(
                            currentResponse = response.text,
                            isGenerating = !response.isComplete
                        )

                        if (response.isComplete) {
                            // Add AI response to chat history
                            val aiMessage = ChatMessage(text = response.text, isUser = false)
                            _uiState.value = _uiState.value.copy(
                                chatHistory = _uiState.value.chatHistory + aiMessage,
                                currentResponse = ""
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isGenerating = false,
                    error = e.message ?: "Failed to generate text"
                )
            }
        }
    }

    fun clearChat() {
        _uiState.value = _uiState.value.copy(
            chatHistory = emptyList(),
            currentResponse = "",
            error = null
        )
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    private fun checkModelStatus() {
        viewModelScope.launch {
            try {
                val isLoaded = checkModelStatusUseCase()
                _uiState.value = _uiState.value.copy(isModelLoaded = isLoaded)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message ?: "Failed to check model status"
                )
            }
        }
    }
}
