package com.adilstudio.project.onevault.domain.usecase

import com.adilstudio.project.onevault.domain.repository.GPT2Repository

class GetRandomPromptUseCase(
    private val repository: GPT2Repository
) {
    operator fun invoke(): String {
        return repository.getRandomPrompt()
    }
}