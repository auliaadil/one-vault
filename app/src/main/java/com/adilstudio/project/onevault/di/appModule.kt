package com.adilstudio.project.onevault.di

import android.app.Application
import com.adilstudio.project.onevault.data.security.SecurityManager
import org.koin.dsl.module

val appModule = module {
    single { (app: Application) -> SecurityManager(app) }
}

