package com.adilstudio.project.onevault.presentation.gpt2

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adilstudio.project.onevault.domain.model.GenerationConfig
import com.adilstudio.project.onevault.domain.model.GenerationStrategy
import com.adilstudio.project.onevault.domain.usecase.GenerateGPT2TextUseCase
import com.adilstudio.project.onevault.domain.usecase.GetRandomPromptUseCase
import com.adilstudio.project.onevault.domain.usecase.InitializeGPT2UseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class GPT2ViewModel(
    private val initializeGPT2UseCase: InitializeGPT2UseCase,
    private val generateTextUseCase: GenerateGPT2TextUseCase,
    private val getRandomPromptUseCase: GetRandomPromptUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        GPT2UiState(
            prompt = getRandomPromptUseCase(),
            completion = "",
            isGenerating = false,
            isInitialized = false,
            error = null
        )
    )
    val uiState: StateFlow<GPT2UiState> = _uiState.asStateFlow()

    private var generationJob: Job? = null
    private val generationConfig = GenerationConfig(
        strategy = GenerationStrategy.TOP_K,
        topK = 40,
        maxTokens = 100
    )

    init {
        initializeModel()
    }

    fun onRefreshPrompt() {
        val newPrompt = getRandomPromptUseCase()
        _uiState.value = _uiState.value.copy(
            prompt = newPrompt,
            completion = "",
            error = null
        )
    }

    fun onGenerateText() {
        val currentState = _uiState.value
        if (currentState.isGenerating || !currentState.isInitialized) return

        generationJob?.cancel()

        _uiState.value = currentState.copy(
            completion = "",
            isGenerating = true,
            error = null
        )

        generationJob = viewModelScope.launch {
            generateTextUseCase(currentState.prompt, generationConfig)
                .catch { throwable ->
                    _uiState.value = _uiState.value.copy(
                        isGenerating = false,
                        error = throwable.message ?: "Unknown error occurred"
                    )
                }
                .collect { token ->
                    _uiState.value = _uiState.value.copy(
                        completion = _uiState.value.completion + token
                    )
                }

            _uiState.value = _uiState.value.copy(isGenerating = false)
        }
    }

    fun onStopGeneration() {
        generationJob?.cancel()
        _uiState.value = _uiState.value.copy(isGenerating = false)
    }

    private fun initializeModel() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isInitialized = false)
            initializeGPT2UseCase()
            _uiState.value = _uiState.value.copy(
                isInitialized = true,
                error = null
            )
//            try {
//                _uiState.value = _uiState.value.copy(isInitialized = false)
//                initializeGPT2UseCase()
//                _uiState.value = _uiState.value.copy(
//                    isInitialized = true,
//                    error = null
//                )
//            } catch (throwable: Throwable) {
//                _uiState.value = _uiState.value.copy(
//                    isInitialized = false,
//                    error = "Failed to initialize model: ${throwable.message}"
//                )
//            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        generationJob?.cancel()
    }
}