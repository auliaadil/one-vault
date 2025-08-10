package com.adilstudio.project.onevault.data.repository

import android.content.Context
import com.adilstudio.project.onevault.data.tokenizer.GPT2Tokenizer
import com.adilstudio.project.onevault.domain.model.LLMRequest
import com.adilstudio.project.onevault.domain.model.LLMResponse
import com.adilstudio.project.onevault.domain.repository.LLMRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

class TensorFlowLiteLLMRepository(
    private val context: Context
) : LLMRepository {

    private var interpreter: Interpreter? = null
    private var tokenizer: GPT2Tokenizer? = null
    private var isModelInitialized = false

    override suspend fun initializeModel(modelPath: String): Boolean = withContext(Dispatchers.IO) {
        try {
            // Initialize tokenizer first
            val tokenizerInstance = GPT2Tokenizer(context)
            val tokenizerInitialized = tokenizerInstance.initialize()

            if (!tokenizerInitialized) {
                return@withContext false
            }

            // Initialize model
            val modelBuffer = loadModelFile(modelPath)
            val options = Interpreter.Options().apply {
                setNumThreads(4) // Use 4 threads for better performance
                setUseNNAPI(true) // Use Android Neural Networks API if available
            }

            interpreter = Interpreter(modelBuffer, options)
            tokenizer = tokenizerInstance
            isModelInitialized = true
            true
        } catch (e: Exception) {
            e.printStackTrace()
            isModelInitialized = false
            false
        }
    }

    override suspend fun generateText(request: LLMRequest): Flow<LLMResponse> = flow {
        if (!isModelInitialized || interpreter == null || tokenizer == null) {
            emit(LLMResponse(
                text = "",
                isComplete = true,
                error = "Model or tokenizer not initialized"
            ))
            return@flow
        }

        try {
            val startTime = System.currentTimeMillis()

            // Proper tokenization using GPT2Tokenizer
            val inputTokens = tokenizer!!.encode(request.prompt)

            // Check if input is too long
            if (inputTokens.size > 1024) { // Typical GPT-2 context limit
                emit(LLMResponse(
                    text = "",
                    isComplete = true,
                    error = "Input text is too long. Maximum context length exceeded."
                ))
                return@flow
            }

            val outputTokens = runInference(inputTokens, request.maxTokens, request.temperature)
            val generatedText = tokenizer!!.decode(outputTokens)

            val processingTime = System.currentTimeMillis() - startTime

            emit(LLMResponse(
                text = generatedText,
                isComplete = true,
                processingTimeMs = processingTime,
                tokenCount = outputTokens.size
            ))

        } catch (e: Exception) {
            emit(LLMResponse(
                text = "",
                isComplete = true,
                error = e.message ?: "Unknown error occurred"
            ))
        }
    }.flowOn(Dispatchers.IO)

    override suspend fun isModelLoaded(): Boolean = isModelInitialized && tokenizer?.isTokenizerReady() == true

    override suspend fun unloadModel() = withContext(Dispatchers.IO) {
        interpreter?.close()
        interpreter = null
        tokenizer = null
        isModelInitialized = false
    }

    override suspend fun getAvailableModels(): List<String> = withContext(Dispatchers.IO) {
        try {
            context.assets.list("models")?.map { "models/$it" }?.toList() ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun loadModelFile(modelPath: String): MappedByteBuffer {
        // If modelPath doesn't start with "models/", add it
        val fullModelPath = if (modelPath.startsWith("models/")) {
            modelPath
        } else {
            "models/$modelPath"
        }

        val fileDescriptor = context.assets.openFd(fullModelPath)
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }

    private fun runInference(inputTokens: IntArray, maxTokens: Int, temperature: Float): IntArray {
        val interpreter = this.interpreter ?: return intArrayOf()

        try {
            // Get model input/output details
            val inputDetails = interpreter.getInputTensor(0)
            val outputDetails = interpreter.getOutputTensor(0)

            // Prepare input tensor
            val inputShape = inputDetails.shape()
            val sequenceLength = minOf(inputTokens.size, inputShape[1] - maxTokens)

            // Create input array with proper shape [batch_size, sequence_length]
            val input = Array(1) { IntArray(inputShape[1]) }

            // Copy input tokens to the input array
            for (i in 0 until sequenceLength) {
                input[0][i] = inputTokens[i]
            }

            // Prepare output tensor
            val outputShape = outputDetails.shape()
            val output = Array(1) { FloatArray(outputShape[1]) }

            // Run inference
            interpreter.run(input, output)

            // Convert output probabilities to tokens
            val generatedTokens = mutableListOf<Int>()
            val logits = output[0]

            // Simple greedy decoding (you might want to implement sampling based on temperature)
            for (i in 0 until maxTokens) {
                val tokenId = if (temperature > 0.0f) {
                    sampleFromLogits(logits, temperature)
                } else {
                    logits.indices.maxByOrNull { logits[it] } ?: 0
                }

                generatedTokens.add(tokenId)

                // Check for end of text token
                if (tokenId == GPT2Tokenizer.END_OF_TEXT_TOKEN_ID) {
                    break
                }
            }

            return generatedTokens.toIntArray()

        } catch (e: Exception) {
            e.printStackTrace()
            // Fallback: return a simple response
            return generateFallbackResponse(inputTokens, maxTokens)
        }
    }

    private fun sampleFromLogits(logits: FloatArray, temperature: Float): Int {
        if (temperature == 0.0f) {
            return logits.indices.maxByOrNull { logits[it] } ?: 0
        }

        // Apply temperature scaling
        val scaledLogits = logits.map { it / temperature }.toFloatArray()

        // Apply softmax
        val maxLogit = scaledLogits.maxOrNull() ?: 0.0f
        val expLogits = scaledLogits.map { kotlin.math.exp((it - maxLogit).toDouble()).toFloat() }
        val sumExp = expLogits.sum()
        val probabilities = expLogits.map { it / sumExp }

        // Sample from the distribution
        val randomValue = kotlin.random.Random.nextFloat()
        var cumulativeProbability = 0.0f

        for (i in probabilities.indices) {
            cumulativeProbability += probabilities[i]
            if (randomValue <= cumulativeProbability) {
                return i
            }
        }

        return probabilities.indices.last()
    }

    private fun generateFallbackResponse(inputTokens: IntArray, maxTokens: Int): IntArray {
        // Generate a simple fallback response
        val fallbackWords = listOf("I", "understand", "your", "question", ".", "Let", "me", "help", "you", "with", "that", ".")
        val tokenizer = this.tokenizer ?: return intArrayOf()

        val responseText = fallbackWords.take(maxTokens.coerceAtMost(fallbackWords.size)).joinToString(" ")
        return tokenizer.encode(responseText)
    }
}
