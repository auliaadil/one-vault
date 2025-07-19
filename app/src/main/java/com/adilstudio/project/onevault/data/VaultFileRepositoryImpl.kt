package com.adilstudio.project.onevault.data

import com.adilstudio.project.onevault.domain.model.VaultFile
import com.adilstudio.project.onevault.domain.repository.VaultFileRepository
import kotlinx.coroutines.flow.Flow

class VaultFileRepositoryImpl : VaultFileRepository {
    override fun getVaultFiles(): Flow<List<VaultFile>> {
        // TODO: Implement using SQLDelight
        throw NotImplementedError()
    }
    override suspend fun getVaultFileById(id: Long): VaultFile? {
        // TODO: Implement using SQLDelight
        throw NotImplementedError()
    }
    override suspend fun addVaultFile(vaultFile: VaultFile) {
        // TODO: Implement using SQLDelight
        throw NotImplementedError()
    }
    override suspend fun updateVaultFile(vaultFile: VaultFile) {
        // TODO: Implement using SQLDelight
        throw NotImplementedError()
    }
    override suspend fun deleteVaultFile(id: Long) {
        // TODO: Implement using SQLDelight
        throw NotImplementedError()
    }
}

