package com.adilstudio.project.onevault.domain.repository

import com.adilstudio.project.onevault.domain.model.Credential
import kotlinx.coroutines.flow.Flow

interface CredentialRepository {
    fun getCredentials(): Flow<List<Credential>>
    suspend fun addCredential(credential: Credential)
    suspend fun updateCredential(credential: Credential)
    suspend fun deleteCredential(id: Long)
}

