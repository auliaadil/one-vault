package com.adilstudio.project.onevault.util

/**
 * Utility class for logging with dependency injection
 */
class CrashlyticsLogger(private val logger: Logger) {

    /**
     * Log a non-fatal exception to the logger
     */
    fun logException(exception: Throwable) {
        logger.logException(exception)
    }

    /**
     * Log a custom message to the logger
     */
    fun log(message: String) {
        logger.log(message)
    }

    /**
     * Set user identifier for crash reports
     */
    fun setUserId(userId: String) {
        logger.setUserId(userId)
    }

    /**
     * Set custom key-value pair for crash context
     */
    fun setCustomKey(key: String, value: String) {
        logger.setCustomKey(key, value)
    }

    fun setCustomKey(key: String, value: Boolean) {
        logger.setCustomKey(key, value)
    }

    fun setCustomKey(key: String, value: Int) {
        logger.setCustomKey(key, value)
    }

    /**
     * Force a crash (for testing purposes only)
     */
    fun forceCrash() {
        logger.forceCrash()
    }

    /**
     * Log breadcrumb for tracking user actions
     */
    fun logBreadcrumb(action: String, screen: String? = null) {
        logger.logBreadcrumb(action, screen)
    }
}
