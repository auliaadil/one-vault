package com.adilstudio.project.onevault.data

import com.adilstudio.project.onevault.data.security.SecurityManager
import com.adilstudio.project.onevault.domain.model.Credential
import com.adilstudio.project.onevault.domain.repository.CredentialRepository
import kotlinx.coroutines.flow.Flow

class CredentialRepositoryImpl(private val securityManager: SecurityManager) : CredentialRepository {
    override fun getCredentials(): Flow<List<Credential>> {
        // TODO: Implement using SQLDelight
        throw NotImplementedError()
    }
    override suspend fun getCredentialById(id: Long): Credential? {
        // TODO: Implement using SQLDelight
        throw NotImplementedError()
    }
    override suspend fun addCredential(credential: Credential) {
        // Encrypt the password before saving
        val (iv, encrypted) = securityManager.encrypt(credential.encryptedPassword)
        val encryptedPassword = android.util.Base64.encodeToString(iv + encrypted, android.util.Base64.DEFAULT)
        val securedCredential = credential.copy(encryptedPassword = encryptedPassword)
        // TODO: Save securedCredential to SQLDelight
        throw NotImplementedError()
    }
    override suspend fun updateCredential(credential: Credential) {
        // Encrypt the password before updating
        val (iv, encrypted) = securityManager.encrypt(credential.encryptedPassword)
        val encryptedPassword = android.util.Base64.encodeToString(iv + encrypted, android.util.Base64.DEFAULT)
        val securedCredential = credential.copy(encryptedPassword = encryptedPassword)
        // TODO: Update securedCredential in SQLDelight
        throw NotImplementedError()
    }
    override suspend fun deleteCredential(id: Long) {
        // TODO: Implement using SQLDelight
        throw NotImplementedError()
    }
}
