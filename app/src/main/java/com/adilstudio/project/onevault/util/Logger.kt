package com.adilstudio.project.onevault.util

/**
 * Interface for logging functionality
 */
interface Logger {
    /**
     * Log a non-fatal exception
     */
    fun logException(exception: Throwable)

    /**
     * Log a custom message
     */
    fun log(message: String)

    /**
     * Set user identifier for crash reports
     */
    fun setUserId(userId: String)

    /**
     * Set custom key-value pair for crash context
     */
    fun setCustomKey(key: String, value: String)

    fun setCustomKey(key: String, value: Boolean)

    fun setCustomKey(key: String, value: Int)

    /**
     * Force a crash (for testing purposes only)
     */
    fun forceCrash()

    /**
     * Log breadcrumb for tracking user actions
     */
    fun logBreadcrumb(action: String, screen: String? = null)
}

