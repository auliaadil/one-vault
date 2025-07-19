package com.adilstudio.project.onevault.domain.repository

import com.adilstudio.project.onevault.domain.model.VaultFile
import kotlinx.coroutines.flow.Flow

interface VaultFileRepository {
    fun getVaultFiles(): Flow<List<VaultFile>>
    suspend fun getVaultFileById(id: Long): VaultFile?
    suspend fun addVaultFile(vaultFile: VaultFile)
    suspend fun updateVaultFile(vaultFile: VaultFile)
    suspend fun deleteVaultFile(id: Long)
}

