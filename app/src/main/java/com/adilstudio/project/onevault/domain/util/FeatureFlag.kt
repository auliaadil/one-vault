package com.adilstudio.project.onevault.domain.util

/**
 * Feature flag management for controlling app features
 * This allows for easy enabling/disabling of features during development or rollout
 */
object FeatureFlag {

    // OCR Features
    const val OCR_ENABLED = false // Set to true to enable OCR functionality
    const val OCR_SPLIT_BILL = false // Set to true to enable OCR in split bill flow

    // Future feature flags can be added here
    const val BIOMETRIC_AUTH = true
    const val CLOUD_SYNC = false
    const val ADVANCED_REPORTS = true

    /**
     * Check if OCR functionality should be available
     */
    fun isOcrEnabled(): Boolean = OCR_ENABLED

    /**
     * Check if OCR should be used in split bill flow
     */
    fun isOcrSplitBillEnabled(): Boolean = OCR_ENABLED && OCR_SPLIT_BILL

    /**
     * Check if biometric authentication is enabled
     */
    fun isBiometricAuthEnabled(): Boolean = BIOMETRIC_AUTH

    /**
     * Check if cloud sync is enabled
     */
    fun isCloudSyncEnabled(): Boolean = CLOUD_SYNC

    /**
     * Check if advanced reports are enabled
     */
    fun isAdvancedReportsEnabled(): Boolean = ADVANCED_REPORTS

    /**
     * Get all feature flags as a map (useful for debug/settings screens)
     */
    fun getAllFlags(): Map<String, Boolean> = mapOf(
        "OCR_ENABLED" to OCR_ENABLED,
        "OCR_SPLIT_BILL" to OCR_SPLIT_BILL,
        "BIOMETRIC_AUTH" to BIOMETRIC_AUTH,
        "CLOUD_SYNC" to CLOUD_SYNC,
        "ADVANCED_REPORTS" to ADVANCED_REPORTS
    )
}
