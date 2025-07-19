package com.adilstudio.project.onevault.di

import com.adilstudio.project.onevault.data.BillRepositoryImpl
import com.adilstudio.project.onevault.data.CredentialRepositoryImpl
import com.adilstudio.project.onevault.data.VaultFileRepositoryImpl
import com.adilstudio.project.onevault.data.security.SecurityManager
import com.adilstudio.project.onevault.domain.repository.BillRepository
import com.adilstudio.project.onevault.domain.repository.CredentialRepository
import com.adilstudio.project.onevault.domain.repository.VaultFileRepository
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val repositoryModule = module {
    single { SecurityManager(androidContext()) }
    single<BillRepository> { BillRepositoryImpl() }
    single<CredentialRepository> { CredentialRepositoryImpl(get()) }
    single<VaultFileRepository> { VaultFileRepositoryImpl() }
}
