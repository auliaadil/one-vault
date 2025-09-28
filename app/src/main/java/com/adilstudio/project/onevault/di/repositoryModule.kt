package com.adilstudio.project.onevault.di

import com.adilstudio.project.onevault.data.repository.AccountRepositoryImpl
import com.adilstudio.project.onevault.data.repository.BillCategoryRepositoryImpl
import com.adilstudio.project.onevault.data.repository.BillRepositoryImpl
import com.adilstudio.project.onevault.data.repository.CredentialRepositoryImpl
import com.adilstudio.project.onevault.data.repository.SettingsRepositoryImpl
import com.adilstudio.project.onevault.data.repository.VaultFileRepositoryImpl
import com.adilstudio.project.onevault.data.local.PreferenceManager
import com.adilstudio.project.onevault.core.security.CryptoService
import com.adilstudio.project.onevault.core.security.CryptoProvider
import com.adilstudio.project.onevault.domain.repository.AccountRepository
import com.adilstudio.project.onevault.domain.repository.BillCategoryRepository
import com.adilstudio.project.onevault.domain.repository.BillRepository
import com.adilstudio.project.onevault.domain.repository.CredentialRepository
import com.adilstudio.project.onevault.domain.repository.SettingsRepository
import com.adilstudio.project.onevault.domain.repository.VaultFileRepository
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val repositoryModule = module {
    single { PreferenceManager(androidContext()) }
    single<CryptoProvider> { CryptoService() }

    single<BillRepository> { BillRepositoryImpl(get()) }
    single<CredentialRepository> { CredentialRepositoryImpl(get(), get(), get(), get()) }
    single<VaultFileRepository> { VaultFileRepositoryImpl(get()) }
    single<BillCategoryRepository> { BillCategoryRepositoryImpl(get()) }
    single<SettingsRepository> { SettingsRepositoryImpl(get()) }
    single<AccountRepository> { AccountRepositoryImpl(get()) }
}
