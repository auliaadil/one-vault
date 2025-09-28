package com.adilstudio.project.onevault.domain.manager

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AppSecurityManager() {
    private val _isAppLocked = MutableStateFlow(false)
    val isAppLocked: StateFlow<Boolean> = _isAppLocked.asStateFlow()

    /**
     * Checks if the app should be locked on launch.
     * Call this once at app launch.
     */
    fun checkAndLockOnLaunch() {
        // Since we only lock on launch and not based on timeout,
        // we can always lock the app if biometric is enabled
        lockApp()
    }

    fun lockApp() {
        _isAppLocked.value = true
    }

    fun unlockApp() {
        _isAppLocked.value = false
    }
}
