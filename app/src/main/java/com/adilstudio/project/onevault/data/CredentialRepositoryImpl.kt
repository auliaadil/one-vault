package com.adilstudio.project.onevault.data

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOneOrNull
import com.adilstudio.project.onevault.Database
import com.adilstudio.project.onevault.data.security.SecurityManager
import com.adilstudio.project.onevault.domain.model.Credential
import com.adilstudio.project.onevault.domain.repository.CredentialRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.single

class CredentialRepositoryImpl(
    private val database: Database,
    private val securityManager: SecurityManager
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
                        username = entity.username,
                        encryptedPassword = entity.encryptedPassword
                    )
                }
            }
    }

    override suspend fun getCredentialById(id: Long): Credential? {
        return queries.selectById(id)
            .asFlow()
            .mapToOneOrNull(Dispatchers.IO)
            .map { entity ->
                entity?.let {
                    Credential(
                        id = it.id,
                        serviceName = it.serviceName,
                        username = it.username,
                        encryptedPassword = it.encryptedPassword
                    )
                }
            }.single()
    }

    override suspend fun addCredential(credential: Credential) {
        // Encrypt the password before saving
        val (iv, encrypted) = securityManager.encrypt(credential.encryptedPassword)
        val encryptedPassword =
            android.util.Base64.encodeToString(iv + encrypted, android.util.Base64.DEFAULT)

        queries.insertCredential(
            id = credential.id,
            serviceName = credential.serviceName,
            username = credential.username,
            encryptedPassword = encryptedPassword
        )
    }

    override suspend fun updateCredential(credential: Credential) {
        // Encrypt the password before updating
        val (iv, encrypted) = securityManager.encrypt(credential.encryptedPassword)
        val encryptedPassword =
            android.util.Base64.encodeToString(iv + encrypted, android.util.Base64.DEFAULT)

        queries.updateCredential(
            serviceName = credential.serviceName,
            username = credential.username,
            encryptedPassword = encryptedPassword,
            id = credential.id
        )
    }

    override suspend fun deleteCredential(id: Long) {
        queries.deleteCredential(id)
    }
}
