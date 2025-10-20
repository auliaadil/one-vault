package com.adilstudio.project.onevault.domain.usecase

import com.adilstudio.project.onevault.domain.repository.BackupRepository
import java.io.File

class ImportVaultUseCase(
    private val backupRepository: BackupRepository
) {
    suspend operator fun invoke(inputFile: File): Result<Unit> {
        return try {
            // First validate the backup file
            backupRepository.validateBackupFile(inputFile).getOrThrow()

            // Load the backup from file
            val backup = backupRepository.loadBackupFromFile(inputFile).getOrThrow()

            // Import the backup data
            backupRepository.importVaultBackup(backup)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
