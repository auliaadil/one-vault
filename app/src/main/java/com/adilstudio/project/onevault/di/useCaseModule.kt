package com.adilstudio.project.onevault.di

import com.adilstudio.project.onevault.domain.usecase.*
import org.koin.dsl.module

val useCaseModule = module {
    single { AddBillUseCase(get()) }
    single { GetBillsUseCase(get()) }
    single { AddCredentialUseCase(get()) }
    single { GetCredentialsUseCase(get()) }
}
