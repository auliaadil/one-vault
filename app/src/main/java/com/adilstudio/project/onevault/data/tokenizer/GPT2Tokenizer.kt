package com.adilstudio.project.onevault.data.tokenizer

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.IOException

class GPT2Tokenizer(private val context: Context) {

    // Special tokens commonly used in GPT-2
    companion object {
        const val UNKNOWN_TOKEN = "<|unk|>"
        const val END_OF_TEXT_TOKEN = "<|endoftext|>"
        const val PAD_TOKEN = "<|pad|>"

        // Common token IDs (these are approximate - actual values depend on your model)
        const val UNKNOWN_TOKEN_ID = 0
        const val END_OF_TEXT_TOKEN_ID = 50256
        const val PAD_TOKEN_ID = 50257
    }

    private var encoder: Map<String, Int> = emptyMap()
    private var decoder: Map<Int, String> = emptyMap()
    private var bpeRanks: Map<Pair<String, String>, Int> = emptyMap()
    private var vocab: Set<String> = emptySet()
    private var isInitialized = false

    // Basic vocabulary for fallback if no vocab file is found
    private val basicVocab = mutableMapOf<String, Int>().apply {
        // Add basic characters and common tokens
        val chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789 .,!?;:'\"-()[]{}/@#$%^&*+=<>|\\`~_"
        chars.forEachIndexed { index, char ->
            this[char.toString()] = index + 1
        }
        this[UNKNOWN_TOKEN] = UNKNOWN_TOKEN_ID
        this[END_OF_TEXT_TOKEN] = END_OF_TEXT_TOKEN_ID
        this[PAD_TOKEN] = PAD_TOKEN_ID

        // Add some common words
        val commonWords = listOf("the", "and", "or", "is", "was", "are", "were", "a", "an", "this", "that", "in", "on", "at", "to", "for", "of", "with", "by")
        commonWords.forEachIndexed { index, word ->
            this[word] = chars.length + index + 10
        }
    }

    suspend fun initialize(): Boolean = withContext(Dispatchers.IO) {
        try {
            // Try to load vocabulary from assets
            loadVocabulary()
            isInitialized = true
            true
        } catch (e: Exception) {
            // Fallback to basic vocabulary
            initializeBasicVocab()
            isInitialized = true
            true
        }
    }

    private fun loadVocabulary() {
        try {
            // Try to load vocab.json and merges.txt from assets
            val vocabJson = context.assets.open("tokenizer/vocab.json").bufferedReader().use { it.readText() }
            val mergesText = context.assets.open("tokenizer/merges.txt").bufferedReader().use { it.readText() }

            // Parse vocabulary
            val jsonObject = JSONObject(vocabJson)
            val vocabMap = mutableMapOf<String, Int>()
            val keys = jsonObject.keys()
            while (keys.hasNext()) {
                val key = keys.next()
                vocabMap[key] = jsonObject.getInt(key)
            }

            encoder = vocabMap.toMap()
            decoder = encoder.entries.associate { it.value to it.key }
            vocab = encoder.keys.toSet()

            // Parse BPE merges
            val mergesList = mergesText.lines().drop(1) // Skip header
                .filter { it.isNotBlank() }
                .mapIndexed { index, line ->
                    val parts = line.split(" ")
                    if (parts.size == 2) {
                        Pair(parts[0], parts[1]) to index
                    } else null
                }.filterNotNull()

            bpeRanks = mergesList.toMap()

        } catch (e: IOException) {
            throw e
        }
    }

    private fun initializeBasicVocab() {
        encoder = basicVocab.toMap()
        decoder = encoder.entries.associate { it.value to it.key }
        vocab = encoder.keys.toSet()
        bpeRanks = emptyMap() // No BPE for basic vocab
    }

    fun encode(text: String): IntArray {
        if (!isInitialized) {
            return intArrayOf(UNKNOWN_TOKEN_ID)
        }

        return if (bpeRanks.isNotEmpty()) {
            encodeBPE(text)
        } else {
            encodeBasic(text)
        }
    }

    fun decode(tokens: IntArray): String {
        if (!isInitialized) {
            return UNKNOWN_TOKEN
        }

        return tokens.joinToString("") { tokenId ->
            decoder[tokenId] ?: UNKNOWN_TOKEN
        }.replace("Ġ", " ") // GPT-2 uses Ġ for spaces
    }

    private fun encodeBPE(text: String): IntArray {
        val words = text.split(Regex("\\s+"))
        val tokens = mutableListOf<Int>()

        for (word in words) {
            if (word.isEmpty()) continue

            val wordTokens = getWordTokens(word)
            val bpeTokens = bpe(wordTokens)

            for (token in bpeTokens) {
                tokens.add(encoder[token] ?: UNKNOWN_TOKEN_ID)
            }
        }

        return tokens.toIntArray()
    }

    private fun encodeBasic(text: String): IntArray {
        val tokens = mutableListOf<Int>()
        var i = 0

        while (i < text.length) {
            // Try to match the longest possible token
            var matched = false
            var maxLen = minOf(10, text.length - i) // Check up to 10 characters

            for (len in maxLen downTo 1) {
                val substr = text.substring(i, i + len)
                if (encoder.containsKey(substr)) {
                    tokens.add(encoder[substr]!!)
                    i += len
                    matched = true
                    break
                }
            }

            if (!matched) {
                // If no match found, add unknown token and move forward
                tokens.add(UNKNOWN_TOKEN_ID)
                i++
            }
        }

        return tokens.toIntArray()
    }

    private fun getWordTokens(word: String): List<String> {
        return word.map { it.toString() }
    }

    private fun bpe(word: List<String>): List<String> {
        if (word.size == 1) {
            return word
        }

        val pairs = getPairs(word)
        if (pairs.isEmpty()) {
            return word
        }

        var wordList = word.toMutableList()

        while (true) {
            val bigram = pairs.minByOrNull { bpeRanks[it] ?: Int.MAX_VALUE }
                ?: break

            if (!bpeRanks.containsKey(bigram)) {
                break
            }

            val first = bigram.first
            val second = bigram.second
            val newWord = mutableListOf<String>()
            var i = 0

            while (i < wordList.size) {
                // Find the next occurrence of 'first' starting from index i
                val j = findIndexOf(wordList, first, i)
                if (j == -1) {
                    newWord.addAll(wordList.subList(i, wordList.size))
                    break
                }

                newWord.addAll(wordList.subList(i, j))
                i = j

                if (wordList[i] == first && i < wordList.size - 1 && wordList[i + 1] == second) {
                    newWord.add(first + second)
                    i += 2
                } else {
                    newWord.add(wordList[i])
                    i += 1
                }
            }

            wordList = newWord
            if (wordList.size == 1) {
                break
            }

            val newPairs = getPairs(wordList)
            if (newPairs.isEmpty()) {
                break
            }
        }

        return wordList
    }

    private fun findIndexOf(list: List<String>, element: String, startIndex: Int): Int {
        for (i in startIndex until list.size) {
            if (list[i] == element) {
                return i
            }
        }
        return -1
    }

    private fun getPairs(word: List<String>): Set<Pair<String, String>> {
        val pairs = mutableSetOf<Pair<String, String>>()
        for (i in 0 until word.size - 1) {
            pairs.add(Pair(word[i], word[i + 1]))
        }
        return pairs
    }

    fun getVocabSize(): Int = encoder.size

    fun isTokenizerReady(): Boolean = isInitialized
}
