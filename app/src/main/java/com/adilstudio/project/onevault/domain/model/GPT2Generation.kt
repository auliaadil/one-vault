package com.adilstudio.project.onevault.domain.model

data class GPT2Generation(
    val prompt: String,
    val completion: String,
    val isGenerating: Boolean = false
)

enum class GenerationStrategy {
    GREEDY,
    TOP_K
}

data class GenerationConfig(
    val strategy: GenerationStrategy,
    val topK: Int = 40,
    val maxTokens: Int = 100
)