package com.adilstudio.project.onevault.domain.repository

import com.adilstudio.project.onevault.domain.model.VaultBackup
import java.io.File

interface BackupRepository {
    suspend fun exportVaultBackup(): VaultBackup
    suspend fun saveBackupToFile(backup: VaultBackup, file: File): Result<Unit>
    suspend fun loadBackupFromFile(file: File): Result<VaultBackup>
    suspend fun importVaultBackup(backup: VaultBackup): Result<Unit>
    suspend fun validateBackupFile(file: File): Result<Boolean>
}
