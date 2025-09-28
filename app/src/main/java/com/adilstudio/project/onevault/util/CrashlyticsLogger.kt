package com.adilstudio.project.onevault.util

import com.google.firebase.crashlytics.FirebaseCrashlytics

/**
 * Utility class for Firebase Crashlytics integration
 */
object CrashlyticsLogger {

    private val crashlytics = FirebaseCrashlytics.getInstance()

    /**
     * Log a non-fatal exception to Crashlytics
     */
    fun logException(exception: Throwable) {
        crashlytics.recordException(exception)
    }

    /**
     * Log a custom message to Crashlytics
     */
    fun log(message: String) {
        crashlytics.log(message)
    }

    /**
     * Set user identifier for crash reports
     */
    fun setUserId(userId: String) {
        crashlytics.setUserId(userId)
    }

    /**
     * Set custom key-value pair for crash context
     */
    fun setCustomKey(key: String, value: String) {
        crashlytics.setCustomKey(key, value)
    }

    fun setCustomKey(key: String, value: Boolean) {
        crashlytics.setCustomKey(key, value)
    }

    fun setCustomKey(key: String, value: Int) {
        crashlytics.setCustomKey(key, value)
    }

    /**
     * Force a crash (for testing purposes only)
     */
    fun forceCrash() {
        throw RuntimeException("Test crash for Firebase Crashlytics")
    }

    /**
     * Log breadcrumb for tracking user actions
     */
    fun logBreadcrumb(action: String, screen: String? = null) {
        val message = if (screen != null) {
            "Action: $action on Screen: $screen"
        } else {
            "Action: $action"
        }
        log(message)
    }
}
