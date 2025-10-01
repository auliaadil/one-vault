package com.adilstudio.project.onevault.domain.repository

import com.adilstudio.project.onevault.domain.model.Participant
import kotlinx.coroutines.flow.Flow

interface ParticipantRepository {
    fun getParticipants(): Flow<List<Participant>>
    suspend fun getParticipantById(id: Long): Participant?
    suspend fun addParticipant(participant: Participant): Long
    suspend fun updateParticipant(participant: Participant)
    suspend fun deleteParticipant(id: Long)
    suspend fun getParticipantsByIds(ids: List<Long>): List<Participant>
}
