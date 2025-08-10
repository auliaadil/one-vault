package com.adilstudio.project.onevault.data.source

import android.content.Context
import android.util.JsonReader
import com.adilstudio.project.onevault.data.tokenization.GPT2Tokenizer
import com.adilstudio.project.onevault.domain.model.GenerationConfig
import com.adilstudio.project.onevault.domain.model.GenerationStrategy
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import kotlinx.coroutines.yield
import org.tensorflow.lite.Interpreter
import java.io.BufferedReader
import java.io.FileInputStream
import java.io.InputStreamReader
import java.nio.channels.FileChannel
import kotlin.math.exp
import kotlin.random.Random

private typealias Predictions = Array<Array<FloatArray>>

class GPT2ModelDataSource(
    private val context: Context
) {
    private lateinit var tokenizer: GPT2Tokenizer
    private lateinit var tflite: Interpreter
    private var initialized = false

    suspend fun initialize() = withContext(Dispatchers.IO) {
        if (!initialized) {
            val encoder = loadEncoder()
            val decoder = encoder.entries.associateBy({ it.value }, { it.key })
            val bpeRanks = loadBpeRanks()

            tokenizer = GPT2Tokenizer(encoder, decoder, bpeRanks)
            tflite = loadModel()
            initialized = true
        }
    }

    fun isInitialized(): Boolean = initialized

    fun generateText(prompt: String, config: GenerationConfig): Flow<String> = flow {
        if (!initialized) {
            throw IllegalStateException("Model not initialized")
        }

        val tokens = tokenizer.encode(prompt)
        repeat(config.maxTokens) {
            val maxTokens = tokens.takeLast(SEQUENCE_LENGTH).toIntArray()
            val paddedTokens = maxTokens + IntArray(SEQUENCE_LENGTH - maxTokens.size)
            val inputIds = Array(1) { paddedTokens }

            val predictions: Predictions = Array(1) { Array(SEQUENCE_LENGTH) { FloatArray(VOCAB_SIZE) } }
            val outputs = mutableMapOf<Int, Any>(0 to predictions)

            tflite.runForMultipleInputsOutputs(arrayOf(inputIds), outputs)
            val outputLogits = predictions[0][maxTokens.size - 1]

            val nextToken: Int = when (config.strategy) {
                GenerationStrategy.TOP_K -> {
                    val filteredLogitsWithIndexes = outputLogits
                        .mapIndexed { index, fl -> (index to fl) }
                        .sortedByDescending { it.second }
                        .take(config.topK)

                    val filteredLogits = filteredLogitsWithIndexes.map { it.second }
                    val maxLogitValue = filteredLogits.maxOrNull()!!
                    val logitsExp = filteredLogits.map { exp(it - maxLogitValue) }
                    val sumExp = logitsExp.sum()
                    val probs = logitsExp.map { it.div(sumExp) }

                    val logitsIndexes = filteredLogitsWithIndexes.map { it.first }
                    sample(logitsIndexes, probs)
                }
                else -> outputLogits.argmax()
            }

            tokens.add(nextToken)
            val decodedToken = tokenizer.decode(listOf(nextToken))
            emit(decodedToken)

            yield()
        }
    }.flowOn(Dispatchers.Default)

    private suspend fun loadModel(): Interpreter = withContext(Dispatchers.IO) {
        val assetFileDescriptor = context.assets.openFd(MODEL_PATH)
        assetFileDescriptor.use {
            val fileChannel = FileInputStream(assetFileDescriptor.fileDescriptor).channel
            val modelBuffer = fileChannel.map(FileChannel.MapMode.READ_ONLY, it.startOffset, it.declaredLength)

            val opts = Interpreter.Options()
            opts.setNumThreads(NUM_LITE_THREADS)
            return@use Interpreter(modelBuffer, opts)
        }
    }

    private suspend fun loadEncoder(): Map<String, Int> = withContext(Dispatchers.IO) {
        hashMapOf<String, Int>().apply {
            val vocabStream = context.assets.open(VOCAB_PATH)
            vocabStream.use {
                val vocabReader = JsonReader(InputStreamReader(it, "UTF-8"))
                vocabReader.beginObject()
                while (vocabReader.hasNext()) {
                    val key = vocabReader.nextName()
                    val value = vocabReader.nextInt()
                    put(key, value)
                }
                vocabReader.close()
            }
        }
    }

    private suspend fun loadBpeRanks(): Map<Pair<String, String>, Int> = withContext(Dispatchers.IO) {
        hashMapOf<Pair<String, String>, Int>().apply {
            val mergesStream = context.assets.open(MERGES_PATH)
            mergesStream.use { stream ->
                val mergesReader = BufferedReader(InputStreamReader(stream))
                mergesReader.useLines { seq ->
                    seq.drop(1).forEachIndexed { i, s ->
                        val list = s.trim().split(" ")
                        // Only process lines that have exactly 2 tokens
                        if (list.size >= 2) {
                            val keyTuple = list[0] to list[1]
                            put(keyTuple, i)
                        }
                        // Skip malformed lines silently
                    }
                }
            }
        }
    }

    companion object {
        private const val SEQUENCE_LENGTH  = 64
        private const val VOCAB_SIZE       = 50257
        private const val NUM_LITE_THREADS = 4
        private const val MODEL_PATH       = "models/gpt2-64-fp16.tflite"
        private const val VOCAB_PATH       = "tokenizer/vocab.json"
        private const val MERGES_PATH      = "tokenizer/merges.txt"
    }
}

private fun randomIndex(probs: List<Float>): Int {
    val rnd = probs.sum() * Random.nextFloat()
    var acc = 0f

    probs.forEachIndexed { i, fl ->
        acc += fl
        if (rnd < acc) {
            return i
        }
    }

    return probs.size - 1
}

private fun sample(indexes: List<Int>, probs: List<Float>): Int {
    val i = randomIndex(probs)
    return indexes[i]
}

private fun FloatArray.argmax(): Int {
    var bestIndex = 0
    repeat(size) {
        if (this[it] > this[bestIndex]) {
            bestIndex = it
        }
    }
    return bestIndex
}