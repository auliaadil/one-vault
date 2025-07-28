package com.adilstudio.project.onevault.di

import android.app.Application
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.adilstudio.project.onevault.Database
import com.adilstudio.project.onevault.data.security.SecurityManager
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val appModule = module {
    single { (app: Application) -> SecurityManager(app) }

    // SQLDelight Database setup with versioning
    single<SqlDriver> {
        AndroidSqliteDriver(Database.Schema, androidContext(), "onevault.db")
    }
    single { Database(get()) }
}
