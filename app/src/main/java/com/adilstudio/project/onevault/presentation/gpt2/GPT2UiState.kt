package com.adilstudio.project.onevault.presentation.gpt2

data class GPT2UiState(
    val prompt: String,
    val completion: String,
    val isGenerating: Boolean,
    val isInitialized: Boolean,
    val error: String?
)