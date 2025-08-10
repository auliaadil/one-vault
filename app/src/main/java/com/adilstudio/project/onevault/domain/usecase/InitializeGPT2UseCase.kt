package com.adilstudio.project.onevault.domain.usecase

import com.adilstudio.project.onevault.domain.repository.GPT2Repository

class InitializeGPT2UseCase(
    private val repository: GPT2Repository
) {
    suspend operator fun invoke() {
        repository.initialize()
    }
}