package com.adilstudio.project.onevault.domain.manager

import com.adilstudio.project.onevault.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first

class AppSecurityManager(
    private val settingsRepository: SettingsRepository,
) {

    private val _isAppLocked = MutableStateFlow(false)
    val isAppLocked: StateFlow<Boolean> = _isAppLocked.asStateFlow()

    // Flag to temporarily skip locking (e.g., when returning from camera/gallery)
    private var skipNextLockCheck = false

    suspend fun onAppPaused() {
        settingsRepository.setAppLockLastPauseTime(System.currentTimeMillis())
    }

    suspend fun onAppResumed() {
        // Skip lock check if we're returning from camera/gallery
        if (skipNextLockCheck) {
            skipNextLockCheck = false
            return
        }

        val currentTime = System.currentTimeMillis()
        val lastPauseTime = settingsRepository.getAppLockLastPauseTime().first()
        val timeDiff = currentTime - lastPauseTime
        val lockTimeout = settingsRepository.getAppLockTimeout().first()

        // If app was in background for more than timeout period, lock the app
        if (lastPauseTime > 0 && timeDiff > lockTimeout) {
            lockApp()
        }
    }

    fun lockApp() {
        _isAppLocked.value = true
    }

    fun unlockApp() {
        _isAppLocked.value = false
    }

    fun skipNextLockCheck() {
        skipNextLockCheck = true
    }

}
