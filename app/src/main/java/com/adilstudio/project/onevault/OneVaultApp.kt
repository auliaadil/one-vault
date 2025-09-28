package com.adilstudio.project.onevault

import android.app.Application
import com.adilstudio.project.onevault.di.appModule
import com.adilstudio.project.onevault.di.repositoryModule
import com.adilstudio.project.onevault.di.useCaseModule
import com.adilstudio.project.onevault.di.viewModelModule
import com.google.firebase.FirebaseApp
import com.google.firebase.crashlytics.FirebaseCrashlytics
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class OneVaultApp : Application() {
    override fun onCreate() {
        super.onCreate()

        // Initialize Firebase
        FirebaseApp.initializeApp(this)

        // Configure Firebase Crashlytics
        FirebaseCrashlytics.getInstance().isCrashlyticsCollectionEnabled = true

        startKoin {
            androidLogger()
            androidContext(this@OneVaultApp)
            modules(
                appModule,
                repositoryModule,
                useCaseModule,
                viewModelModule
            )
        }
    }
}

