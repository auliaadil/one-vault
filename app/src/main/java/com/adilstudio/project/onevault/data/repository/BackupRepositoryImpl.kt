package com.adilstudio.project.onevault.data.repository

import com.adilstudio.project.onevault.BuildConfig
import com.adilstudio.project.onevault.data.util.EncryptionUtil
import com.adilstudio.project.onevault.domain.model.VaultBackup
import com.adilstudio.project.onevault.domain.repository.*
import kotlinx.coroutines.flow.first
import kotlinx.serialization.json.Json
import java.io.File

class BackupRepositoryImpl(
    private val transactionRepository: TransactionRepository,
    private val credentialRepository: CredentialRepository,
    private val accountRepository: AccountRepository,
    private val transactionCategoryRepository: TransactionCategoryRepository,
    private val vaultFileRepository: VaultFileRepository,
    private val splitBillRepository: SplitBillRepository,
    private val encryptionUtil: EncryptionUtil
) : BackupRepository {

    private val json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
    }

    override suspend fun exportVaultBackup(): VaultBackup {
        return VaultBackup(
            version = BuildConfig.VERSION_NAME,
            createdAt = System.currentTimeMillis(),
            transactions = transactionRepository.getTransactions().first(),
            credentials = credentialRepository.getCredentials().first(),
            accounts = accountRepository.getAccounts().first(),
            transactionCategories = transactionCategoryRepository.getCategories().first(),
            vaultFiles = vaultFileRepository.getVaultFiles().first(),
            splitBills = splitBillRepository.getSplitBills().first(),
            splitItems = emptyList(), // Split items are per split bill, we'll handle them differently
            splitParticipants = emptyList() // Split participants are per split bill, we'll handle them differently
        )
    }

    override suspend fun saveBackupToFile(backup: VaultBackup, file: File): Result<Unit> {
        return try {
            val jsonString = json.encodeToString(VaultBackup.serializer(), backup)
            val encryptedData = encryptionUtil.encrypt(jsonString.toByteArray())
            file.writeBytes(encryptedData)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun loadBackupFromFile(file: File): Result<VaultBackup> {
        return try {
            val encryptedData = file.readBytes()
            val decryptedData = encryptionUtil.decrypt(encryptedData)
            val jsonString = String(decryptedData)
            val backup = json.decodeFromString(VaultBackup.serializer(), jsonString)
            Result.success(backup)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun importVaultBackup(backup: VaultBackup): Result<Unit> {
        return try {
            // Import data using existing repository methods
            backup.accounts.forEach { accountRepository.addAccount(it) }
            backup.transactionCategories.forEach { transactionCategoryRepository.addCategory(it) }
            backup.transactions.forEach { transactionRepository.addTransaction(it) }
            backup.credentials.forEach { credentialRepository.addCredential(it) }
            backup.vaultFiles.forEach { vaultFileRepository.addVaultFile(it) }
            backup.splitBills.forEach { splitBillRepository.addSplitBill(it) }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun validateBackupFile(file: File): Result<Boolean> {
        return try {
            if (!file.exists()) {
                return Result.failure(IllegalArgumentException("Backup file does not exist"))
            }

            if (!file.name.endsWith(".onevault")) {
                return Result.failure(IllegalArgumentException("Invalid backup file format. Expected .onevault extension"))
            }

            // Try to decrypt and parse the file
            val encryptedData = file.readBytes()
            val decryptedData = encryptionUtil.decrypt(encryptedData)
            val jsonString = String(decryptedData)
            json.decodeFromString(VaultBackup.serializer(), jsonString)

            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
