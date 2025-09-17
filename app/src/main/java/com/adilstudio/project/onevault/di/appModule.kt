package com.adilstudio.project.onevault.di

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.adilstudio.project.onevault.Database
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
}
