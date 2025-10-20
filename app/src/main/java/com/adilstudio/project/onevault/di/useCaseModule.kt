package com.adilstudio.project.onevault.di

import com.adilstudio.project.onevault.domain.usecase.*
import com.adilstudio.project.onevault.domain.usecase.GetTransactionCategoriesUseCase
import org.koin.dsl.module

val useCaseModule = module {

    // Transaction Use Cases
    single { AddTransactionUseCase(get()) }
    single { UpdateTransactionUseCase(get()) }
    single { DeleteTransactionUseCase(get()) }
    single { GetTransactionsUseCase(get()) }
    single { GetTransactionCategoriesUseCase(get()) }
    single { AddTransactionCategoryUseCase(get()) }
    single { UpdateTransactionCategoryUseCase(get()) }
    single { DeleteTransactionCategoryUseCase(get()) }
    single { GetTransactionCategoriesCountUseCase(get()) }
    single { InitializeDefaultTransactionCategoriesIfEmptyUseCase(get()) }

    // Credential Use Cases
    single { AddCredentialUseCase(get()) }
    single { UpdateCredentialUseCase(get()) }
    single { DeleteCredentialUseCase(get()) }
    single { GetCredentialsUseCase(get()) }
    single { GetDefaultCredentialTemplateUseCase(get()) }
    single { SaveDefaultCredentialTemplateUseCase(get()) }

    // Account Use Cases
    single { GetAccountsUseCase(get()) }
    single { AddAccountUseCase(get()) }
    single { UpdateAccountUseCase(get()) }
    single { DeleteAccountUseCase(get()) }
    single { GetAccountsCountUseCase(get()) }

    // Split Bill Use Cases
    single { GetSplitBillsUseCase(get()) }
    single { GetSplitBillByIdUseCase(get()) }
    single { DeleteSplitBillUseCase(get()) }
    single { ExportParticipantToTransactionUseCase(get()) }
}
