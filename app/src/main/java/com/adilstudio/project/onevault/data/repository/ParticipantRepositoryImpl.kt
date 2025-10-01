package com.adilstudio.project.onevault.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOneOrNull
import com.adilstudio.project.onevault.Database
import com.adilstudio.project.onevault.domain.model.Participant
import com.adilstudio.project.onevault.domain.repository.ParticipantRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.single

class ParticipantRepositoryImpl(private val database: Database) : ParticipantRepository {

    private val participantQueries = database.participantEntityQueries

    override fun getParticipants(): Flow<List<Participant>> {
        return participantQueries.selectAll()
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { entities ->
                entities.map { entity ->
                    Participant(
                        id = entity.id,
                        name = entity.name,
                        email = entity.email,
                        phone = entity.phone,
                        ratio = entity.ratio.toFloat()
                    )
                }
            }
    }

    override suspend fun getParticipantById(id: Long): Participant? {
        return participantQueries.selectById(id)
            .asFlow()
            .mapToOneOrNull(Dispatchers.IO)
            .map { entity ->
                entity?.let {
                    Participant(
                        id = it.id,
                        name = it.name,
                        email = it.email,
                        phone = it.phone,
                        ratio = it.ratio.toFloat()
                    )
                }
            }
            .single()
    }

    override suspend fun addParticipant(participant: Participant): Long {
        participantQueries.insert(
            name = participant.name,
            email = participant.email,
            phone = participant.phone,
            ratio = participant.ratio.toDouble()
        )
        return participantQueries.lastInsertRowId().executeAsOne()
    }

    override suspend fun updateParticipant(participant: Participant) {
        participantQueries.update(
            id = participant.id,
            name = participant.name,
            email = participant.email,
            phone = participant.phone,
            ratio = participant.ratio.toDouble()
        )
    }

    override suspend fun deleteParticipant(id: Long) {
        participantQueries.deleteById(id)
    }

    override suspend fun getParticipantsByIds(ids: List<Long>): List<Participant> {
        return ids.mapNotNull { id ->
            getParticipantById(id)
        }
    }
}
