package com.adilstudio.project.onevault.di

import com.adilstudio.project.onevault.presentation.bill.BillTrackerViewModel
import com.adilstudio.project.onevault.presentation.credential.PasswordManagerViewModel
import com.adilstudio.project.onevault.presentation.filevault.FileVaultViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    // Example binding, add other ViewModels as you implement them
    viewModel { BillTrackerViewModel(get(), get()) }
    viewModel { PasswordManagerViewModel(get(), get()) }
    viewModel { FileVaultViewModel() }
}
