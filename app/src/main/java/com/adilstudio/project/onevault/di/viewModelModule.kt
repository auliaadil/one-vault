package com.adilstudio.project.onevault.di

import com.adilstudio.project.onevault.presentation.bill.BillTrackerViewModel
import com.adilstudio.project.onevault.presentation.bill.category.BillCategoryViewModel
import com.adilstudio.project.onevault.presentation.credential.PasswordManagerViewModel
import com.adilstudio.project.onevault.presentation.filevault.FileVaultViewModel
import com.adilstudio.project.onevault.presentation.gpt2.GPT2ViewModel
import com.adilstudio.project.onevault.presentation.settings.SettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { BillTrackerViewModel(get(), get(), get(), get()) }
    viewModel { BillCategoryViewModel(get(), get(), get(), get(), get()) }
    viewModel { PasswordManagerViewModel(get(), get(), get(), get()) }
    viewModel { FileVaultViewModel() }
    viewModel { SettingsViewModel(get()) }
    viewModel { GPT2ViewModel(get(), get(), get()) }
}
