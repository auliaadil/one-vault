package com.adilstudio.project.onevault.di

import com.adilstudio.project.onevault.domain.usecase.*
import com.adilstudio.project.onevault.domain.usecase.GetBillCategoriesUseCase
import org.koin.dsl.module

val useCaseModule = module {
    single { AddBillUseCase(get()) }
    single { GetBillsUseCase(get()) }
    single { AddCredentialUseCase(get()) }
    single { UpdateCredentialUseCase(get()) }
    single { DeleteCredentialUseCase(get()) }
    single { GetCredentialsUseCase(get()) }
    single { GetBillCategoriesUseCase(get()) }
    single { AddBillCategoryUseCase(get()) }
    single { UpdateBillCategoryUseCase(get()) }
    single { DeleteBillCategoryUseCase(get()) }
    single { InitializeDefaultCategoriesUseCase(get()) }
}
