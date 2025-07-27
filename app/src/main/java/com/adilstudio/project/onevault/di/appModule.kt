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
        AndroidSqliteDriver(
            schema = Database.Schema,
            context = androidContext(),
            name = "onevault.db",
            version = 2, // Increment version to trigger migration
            callback = AndroidSqliteDriver.Callback(
                schema = Database.Schema,
                migrateCallback = { driver, oldVersion, newVersion ->
                    if (oldVersion < 2) {
                        // Migration from version 1 to 2: Make category nullable
                        driver.execute(null, """
                            ALTER TABLE BillEntity RENAME TO BillEntity_old;
                        """.trimIndent(), 0)

                        // Create new table with nullable category
                        driver.execute(null, """
                            CREATE TABLE BillEntity (
                                id INTEGER NOT NULL PRIMARY KEY,
                                title TEXT NOT NULL,
                                category TEXT,
                                amount REAL NOT NULL,
                                vendor TEXT NOT NULL,
                                billDate TEXT NOT NULL,
                                imagePath TEXT
                            );
                        """.trimIndent(), 0)

                        // Copy data from old table
                        driver.execute(null, """
                            INSERT INTO BillEntity (id, title, category, amount, vendor, billDate, imagePath)
                            SELECT id, title, category, amount, vendor, billDate, imagePath
                            FROM BillEntity_old;
                        """.trimIndent(), 0)

                        // Drop old table
                        driver.execute(null, "DROP TABLE BillEntity_old;", 0)
                    }
                }
            )
        )
    }
    single { Database(get()) }
}
