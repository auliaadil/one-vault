package com.adilstudio.project.onevault.domain.usecase

import com.adilstudio.project.onevault.domain.repository.BackupRepository
import java.io.File

class ExportVaultUseCase(
    private val backupRepository: BackupRepository
) {
    suspend operator fun invoke(outputFile: File): Result<Unit> {
        return try {
            val backup = backupRepository.exportVaultBackup()
            backupRepository.saveBackupToFile(backup, outputFile)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
