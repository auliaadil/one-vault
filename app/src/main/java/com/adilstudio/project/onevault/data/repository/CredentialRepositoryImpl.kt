package com.adilstudio.project.onevault.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.adilstudio.project.onevault.Database
import com.adilstudio.project.onevault.core.security.CryptoProvider
import com.adilstudio.project.onevault.domain.model.Credential
import com.adilstudio.project.onevault.domain.repository.CredentialRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class CredentialRepositoryImpl(
    database: Database,
    private val cryptoService: CryptoProvider
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
                        password = try {
                            cryptoService.decrypt(entity.encryptedPassword)
                        } catch (e: Exception) {
                            // If decryption fails, return empty string or handle gracefully
                            ""
                        },
                        passwordTemplate = entity.passwordTemplate,
                        createdAt = entity.createdAt,
                        updatedAt = entity.updatedAt
                    )
                }
            }
    }

    override suspend fun addCredential(credential: Credential) {
        val encryptedPassword = cryptoService.encrypt(credential.password)
        queries.insertCredential(
            serviceName = credential.serviceName,
            userName = credential.username,
            encryptedPassword = encryptedPassword,
            passwordTemplate = credential.passwordTemplate
        )
    }

    override suspend fun updateCredential(credential: Credential) {
        val encryptedPassword = cryptoService.encrypt(credential.password)
        queries.updateCredential(
            serviceName = credential.serviceName,
            userName = credential.username,
            encryptedPassword = encryptedPassword,
            passwordTemplate = credential.passwordTemplate,
            id = credential.id
        )
    }

    override suspend fun deleteCredential(id: Long) {
        queries.deleteCredential(id)
    }
}
