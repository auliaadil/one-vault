package com.adilstudio.project.onevault.domain.usecase

import com.adilstudio.project.onevault.domain.repository.ParticipantRepository
import kotlinx.coroutines.flow.Flow
import com.adilstudio.project.onevault.domain.model.Participant

class GetParticipantsUseCase(private val repository: ParticipantRepository) {
    operator fun invoke(): Flow<List<Participant>> = repository.getParticipants()
}
