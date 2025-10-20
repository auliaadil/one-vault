package com.adilstudio.project.onevault.di

import com.adilstudio.project.onevault.data.repository.AccountRepositoryImpl
import com.adilstudio.project.onevault.data.repository.TransactionCategoryRepositoryImpl
import com.adilstudio.project.onevault.data.repository.TransactionRepositoryImpl
import com.adilstudio.project.onevault.data.repository.CredentialRepositoryImpl
import com.adilstudio.project.onevault.data.repository.SettingsRepositoryImpl
import com.adilstudio.project.onevault.data.repository.VaultFileRepositoryImpl
import com.adilstudio.project.onevault.data.repository.SplitBillRepositoryImpl
import com.adilstudio.project.onevault.data.repository.BackupRepositoryImpl
import com.adilstudio.project.onevault.data.local.PreferenceManager
import com.adilstudio.project.onevault.data.util.EncryptionUtil
import com.adilstudio.project.onevault.core.security.CryptoService
import com.adilstudio.project.onevault.core.security.CryptoProvider
import com.adilstudio.project.onevault.domain.repository.AccountRepository
import com.adilstudio.project.onevault.domain.repository.TransactionCategoryRepository
import com.adilstudio.project.onevault.domain.repository.TransactionRepository
import com.adilstudio.project.onevault.domain.repository.CredentialRepository
import com.adilstudio.project.onevault.domain.repository.SettingsRepository
import com.adilstudio.project.onevault.domain.repository.VaultFileRepository
import com.adilstudio.project.onevault.domain.repository.SplitBillRepository
import com.adilstudio.project.onevault.domain.repository.BackupRepository
import com.adilstudio.project.onevault.domain.manager.SplitCalculator
import com.adilstudio.project.onevault.domain.manager.OcrManager
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val repositoryModule = module {
    single { PreferenceManager(androidContext()) }
    single<CryptoProvider> { CryptoService() }
    single { EncryptionUtil() }

    single<TransactionRepository> { TransactionRepositoryImpl(get()) }
    single<CredentialRepository> { CredentialRepositoryImpl(get(), get(), get(), get()) }
    single<VaultFileRepository> { VaultFileRepositoryImpl(get()) }
    single<TransactionCategoryRepository> { TransactionCategoryRepositoryImpl(get()) }
    single<SettingsRepository> { SettingsRepositoryImpl(get()) }
    single<AccountRepository> { AccountRepositoryImpl(get()) }

    // Split Bill
    single<SplitBillRepository> { SplitBillRepositoryImpl(get(), get()) }
    single { SplitCalculator() }
    single { OcrManager(androidContext()) }

    // Backup & Restore
    single<BackupRepository> {
        BackupRepositoryImpl(
            transactionRepository = get(),
            credentialRepository = get(),
            accountRepository = get(),
            transactionCategoryRepository = get(),
            vaultFileRepository = get(),
            splitBillRepository = get(),
            encryptionUtil = get()
        )
    }
}
