package com.adilstudio.project.onevault.domain.usecase

import com.adilstudio.project.onevault.domain.model.Participant
import com.adilstudio.project.onevault.domain.repository.ParticipantRepository

class AddParticipantUseCase(private val repository: ParticipantRepository) {
    suspend operator fun invoke(participant: Participant): Long = repository.addParticipant(participant)
}
