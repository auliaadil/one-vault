package com.adilstudio.project.onevault.di

import com.adilstudio.project.onevault.domain.usecase.*
import com.adilstudio.project.onevault.domain.usecase.GetBillCategoriesUseCase
import org.koin.dsl.module

val useCaseModule = module {

    // Bill Use Cases
    single { AddBillUseCase(get()) }
    single { UpdateBillUseCase(get()) }
    single { DeleteBillUseCase(get()) }
    single { GetBillsUseCase(get()) }
    single { GetBillCategoriesUseCase(get()) }
    single { AddBillCategoryUseCase(get()) }
    single { UpdateBillCategoryUseCase(get()) }
    single { DeleteBillCategoryUseCase(get()) }
    single { GetBillCategoriesCountUseCase(get()) }
    single { InitializeDefaultBillCategoriesIfEmptyUseCase(get()) }

    // Credential Use Cases
    single { AddCredentialUseCase(get()) }
    single { UpdateCredentialUseCase(get()) }
    single { DeleteCredentialUseCase(get()) }
    single { GetCredentialsUseCase(get()) }

    // Account Use Cases
    single { GetAccountsUseCase(get()) }
    single { AddAccountUseCase(get()) }
    single { UpdateAccountUseCase(get()) }
    single { DeleteAccountUseCase(get()) }
    single { GetAccountsCountUseCase(get()) }
}
