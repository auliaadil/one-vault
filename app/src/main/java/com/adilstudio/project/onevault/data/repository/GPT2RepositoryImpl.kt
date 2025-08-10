package com.adilstudio.project.onevault.data.repository

import com.adilstudio.project.onevault.data.source.GPT2ModelDataSource
import com.adilstudio.project.onevault.domain.model.GenerationConfig
import com.adilstudio.project.onevault.domain.repository.GPT2Repository
import kotlinx.coroutines.flow.Flow

class GPT2RepositoryImpl(
    private val modelDataSource: GPT2ModelDataSource
) : GPT2Repository {

    private val prompts = arrayOf(
        "Before boarding your rocket to Mars, remember to pack these items",
        "In a shocking finding, scientist discovered a herd of unicorns living in a remote, previously unexplored valley, in the Andes Mountains. Even more surprising to the researchers was the fact that the unicorns spoke perfect English.",
        "Legolas and Gimli advanced on the orcs, raising their weapons with a harrowing war cry.",
        "Today, scientists confirmed the worst possible outcome: the massive asteroid will collide with Earth",
        "Hugging Face is a company that releases awesome projects in machine learning because"
    )

    override suspend fun initialize() {
        modelDataSource.initialize()
    }

    override fun generateText(prompt: String, config: GenerationConfig): Flow<String> {
        return modelDataSource.generateText(prompt, config)
    }

    override fun getRandomPrompt(): String {
        return prompts.random()
    }

    override fun isInitialized(): Boolean {
        return modelDataSource.isInitialized()
    }
}