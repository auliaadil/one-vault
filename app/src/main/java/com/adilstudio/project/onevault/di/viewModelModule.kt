package com.adilstudio.project.onevault.di

import com.adilstudio.project.onevault.presentation.bill.account.AccountViewModel
import com.adilstudio.project.onevault.presentation.bill.BillTrackerViewModel
import com.adilstudio.project.onevault.presentation.bill.category.BillCategoryViewModel
import com.adilstudio.project.onevault.presentation.biometric.BiometricLockViewModel
import com.adilstudio.project.onevault.presentation.credential.CredentialListViewModel
import com.adilstudio.project.onevault.presentation.filevault.FileVaultViewModel
import com.adilstudio.project.onevault.presentation.credential.credentialform.CredentialFormViewModel
import com.adilstudio.project.onevault.presentation.settings.SettingsViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { BillTrackerViewModel(get(), get(), get(), get(), get(), get()) }
    viewModel { BillCategoryViewModel(get(), get(), get(), get(), get(), get()) }
    viewModel { CredentialListViewModel(get(), get()) }
    viewModel { FileVaultViewModel() }
    viewModel { SettingsViewModel(get()) }
    viewModel { AccountViewModel(get(), get(), get(), get(), get()) }
    viewModel { CredentialFormViewModel(get(), get(), get(), get()) }
    viewModel { BiometricLockViewModel(get()) }
}
