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

    // Credential Use Cases
    single { AddCredentialUseCase(get()) }
    single { UpdateCredentialUseCase(get()) }
    single { DeleteCredentialUseCase(get()) }
    single { GetCredentialsUseCase(get()) }

    // GPT2 Use Cases
    single { InitializeGPT2UseCase(get()) }
    single { GenerateGPT2TextUseCase(get()) }
    single { GetRandomPromptUseCase(get()) }
}
