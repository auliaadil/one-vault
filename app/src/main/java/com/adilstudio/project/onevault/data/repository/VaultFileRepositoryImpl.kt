package com.adilstudio.project.onevault.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOneOrNull
import com.adilstudio.project.onevault.Database
import com.adilstudio.project.onevault.domain.model.VaultFile
import com.adilstudio.project.onevault.domain.repository.VaultFileRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.single

class VaultFileRepositoryImpl(database: Database) : VaultFileRepository {

    private val queries = database.vaultFileEntityQueries

    override fun getVaultFiles(): Flow<List<VaultFile>> {
        return queries.selectAll()
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { entities ->
                entities.map { entity ->
                    VaultFile(
                        id = entity.id,
                        name = entity.name,
                        category = entity.category,
                        filePath = entity.filePath
                    )
                }
            }
    }

    override suspend fun getVaultFileById(id: Long): VaultFile? {
        return queries.selectById(id)
            .asFlow()
            .mapToOneOrNull(Dispatchers.IO)
            .map { entity ->
                entity?.let {
                    VaultFile(
                        id = it.id,
                        name = it.name,
                        category = it.category,
                        filePath = it.filePath
                    )
                }
            }.single()
    }

    override suspend fun addVaultFile(vaultFile: VaultFile) {
        queries.insertVaultFile(
            id = vaultFile.id,
            name = vaultFile.name,
            category = vaultFile.category,
            filePath = vaultFile.filePath
        )
    }

    override suspend fun updateVaultFile(vaultFile: VaultFile) {
        queries.updateVaultFile(
            name = vaultFile.name,
            category = vaultFile.category,
            filePath = vaultFile.filePath,
            id = vaultFile.id
        )
    }

    override suspend fun deleteVaultFile(id: Long) {
        queries.deleteVaultFile(id)
    }
}
