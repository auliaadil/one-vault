package com.adilstudio.project.onevault.di

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.adilstudio.project.onevault.Database
import com.adilstudio.project.onevault.data.local.PreferenceManager
import com.adilstudio.project.onevault.domain.manager.AppSecurityManager
import com.adilstudio.project.onevault.domain.manager.BiometricAuthManager
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val appModule = module {
    // SQLDelight Database setup
    single<SqlDriver> {
        AndroidSqliteDriver(
            schema = Database.Schema,
            context = androidContext(),
            name = "onevault.db"
        )
    }
    single { Database(get()) }

    // Preference Manager
    single { PreferenceManager(androidContext()) }

    // Security Managers
    single { AppSecurityManager() }
    single { BiometricAuthManager(get()) }
}
