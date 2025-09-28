package com.adilstudio.project.onevault.di

import com.adilstudio.project.onevault.util.FirebaseCrashlyticsLogger
import com.adilstudio.project.onevault.util.Logger
import org.koin.dsl.module

val loggingModule = module {
    // Provide the Logger implementation
    single<Logger> { FirebaseCrashlyticsLogger() }
}

