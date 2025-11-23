package com.adilstudio.project.onevault.di

import com.adilstudio.project.onevault.presentation.MainViewModel
import com.adilstudio.project.onevault.presentation.biometric.BiometricLockViewModel
import com.adilstudio.project.onevault.presentation.credential.CredentialListViewModel
import com.adilstudio.project.onevault.presentation.credential.credentialform.CredentialFormViewModel
import com.adilstudio.project.onevault.presentation.filevault.FileVaultViewModel
import com.adilstudio.project.onevault.presentation.settings.ImportExportViewModel
import com.adilstudio.project.onevault.presentation.settings.SettingsViewModel
import com.adilstudio.project.onevault.presentation.splitbill.form.SplitBillFormViewModel
import com.adilstudio.project.onevault.presentation.splitbill.list.SplitBillListViewModel
import com.adilstudio.project.onevault.presentation.transaction.TransactionTrackerViewModel
import com.adilstudio.project.onevault.presentation.transaction.account.AccountViewModel
import com.adilstudio.project.onevault.presentation.transaction.category.TransactionCategoryViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    single { MainViewModel(get()) }
    viewModel { TransactionTrackerViewModel(get(), get(), get(), get(), get(), get()) }
    viewModel { TransactionCategoryViewModel(get(), get(), get(), get(), get(), get()) }
    viewModel { CredentialListViewModel(get(), get()) }
    viewModel { FileVaultViewModel() }
    viewModel { SettingsViewModel(get(), get(), get()) }
    viewModel { AccountViewModel(get(), get(), get(), get(), get()) }
    viewModel { CredentialFormViewModel(get(), get(), get(), get()) }
    viewModel { BiometricLockViewModel(get()) }
    viewModel { SplitBillFormViewModel(get(), get(), get()) }
    viewModel { SplitBillListViewModel(get(), get()) }
    viewModel { ImportExportViewModel(get(), get()) }
}
