package com.adilstudio.project.onevault.util

import com.google.firebase.crashlytics.FirebaseCrashlytics

/**
 * Firebase Crashlytics implementation of Logger interface
 */
class FirebaseCrashlyticsLogger : Logger {

    private val crashlytics = FirebaseCrashlytics.getInstance()

    override fun logException(exception: Throwable) {
        crashlytics.recordException(exception)
    }

    override fun log(message: String) {
        crashlytics.log(message)
    }

    override fun setUserId(userId: String) {
        crashlytics.setUserId(userId)
    }

    override fun setCustomKey(key: String, value: String) {
        crashlytics.setCustomKey(key, value)
    }

    override fun setCustomKey(key: String, value: Boolean) {
        crashlytics.setCustomKey(key, value)
    }

    override fun setCustomKey(key: String, value: Int) {
        crashlytics.setCustomKey(key, value)
    }

    override fun forceCrash() {
        throw RuntimeException("Test crash for Firebase Crashlytics")
    }

    override fun logBreadcrumb(action: String, screen: String?) {
        val message = if (screen != null) {
            "Action: $action on Screen: $screen"
        } else {
            "Action: $action"
        }
        log(message)
    }
}


