package com.adilstudio.project.onevault.data

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.adilstudio.project.onevault.Database
import com.adilstudio.project.onevault.domain.model.Credential
import com.adilstudio.project.onevault.domain.repository.CredentialRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class CredentialRepositoryImpl(
    database: Database
) : CredentialRepository {

    private val queries = database.credentialEntityQueries

    override fun getCredentials(): Flow<List<Credential>> {
        return queries.selectAll()
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { entities ->
                entities.map { entity ->
                    Credential(
                        id = entity.id,
                        serviceName = entity.serviceName,
                        username = entity.userName,
                        password = entity.encryptedPassword, // Store as plain text now
                        passwordTemplate = entity.passwordTemplate,
                        createdAt = entity.createdAt,
                        updatedAt = entity.updatedAt
                    )
                }
            }
    }

    override suspend fun addCredential(credential: Credential) {
        queries.insertCredential(
            serviceName = credential.serviceName,
            userName = credential.username,
            encryptedPassword = credential.password, // Store as plain text
            passwordTemplate = credential.passwordTemplate
        )
    }

    override suspend fun updateCredential(credential: Credential) {
        queries.updateCredential(
            serviceName = credential.serviceName,
            userName = credential.username,
            encryptedPassword = credential.password, // Store as plain text
            passwordTemplate = credential.passwordTemplate,
            id = credential.id
        )
    }

    override suspend fun deleteCredential(id: Long) {
        queries.deleteCredential(id)
    }
}
