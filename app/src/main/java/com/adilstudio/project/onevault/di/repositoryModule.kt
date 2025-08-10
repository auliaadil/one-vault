package com.adilstudio.project.onevault.di

import android.app.Application
import com.adilstudio.project.onevault.data.BillCategoryRepositoryImpl
import com.adilstudio.project.onevault.data.BillRepositoryImpl
import com.adilstudio.project.onevault.data.CredentialRepositoryImpl
import com.adilstudio.project.onevault.data.SettingsRepositoryImpl
import com.adilstudio.project.onevault.data.VaultFileRepositoryImpl
import com.adilstudio.project.onevault.data.local.PreferenceManager
import com.adilstudio.project.onevault.data.repository.GPT2RepositoryImpl
import com.adilstudio.project.onevault.data.repository.PasswordGenerationRepositoryImpl
import com.adilstudio.project.onevault.data.security.SecurityManager
import com.adilstudio.project.onevault.data.source.GPT2ModelDataSource
import com.adilstudio.project.onevault.domain.repository.BillCategoryRepository
import com.adilstudio.project.onevault.domain.repository.BillRepository
import com.adilstudio.project.onevault.domain.repository.CredentialRepository
import com.adilstudio.project.onevault.domain.repository.GPT2Repository
import com.adilstudio.project.onevault.domain.repository.PasswordGenerationRepository
import com.adilstudio.project.onevault.domain.repository.SettingsRepository
import com.adilstudio.project.onevault.domain.repository.VaultFileRepository
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val repositoryModule = module {
    single { SecurityManager(androidContext()) }
    single { PreferenceManager(androidContext()) }
    single { GPT2ModelDataSource(androidContext()) }

    single<BillRepository> { BillRepositoryImpl(get()) }
    single<CredentialRepository> { CredentialRepositoryImpl(get(), get()) }
    single<VaultFileRepository> { VaultFileRepositoryImpl(get()) }
    single<BillCategoryRepository> { BillCategoryRepositoryImpl(get()) }
    single<SettingsRepository> { SettingsRepositoryImpl(get()) }
    single<GPT2Repository> { GPT2RepositoryImpl(get()) }
    single<PasswordGenerationRepository> { PasswordGenerationRepositoryImpl(get(), get()) }
}
