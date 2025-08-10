package com.adilstudio.project.onevault.domain.model

data class LLMResponse(
    val text: String,
    val isComplete: Boolean = true,
    val processingTimeMs: Long = 0L,
    val tokenCount: Int = 0,
    val error: String? = null
)

data class LLMRequest(
    val prompt: String,
    val maxTokens: Int = 256,
    val temperature: Float = 0.7f,
    val topK: Int = 40,
    val topP: Float = 0.9f
)

enum class LLMModelType {
    TENSORFLOW_LITE,
    ONNX_RUNTIME
}

data class LLMModelInfo(
    val name: String,
    val modelPath: String,
    val tokenizerPath: String? = null,
    val type: LLMModelType,
    val maxContextLength: Int = 2048,
    val description: String = ""
)
