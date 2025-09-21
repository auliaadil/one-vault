package com.adilstudio.project.onevault.domain.manager

import com.adilstudio.project.onevault.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first

class AppSecurityManager(
    private val settingsRepository: SettingsRepository
) {

    private val _isAppLocked = MutableStateFlow(false)
    val isAppLocked: StateFlow<Boolean> = _isAppLocked.asStateFlow()

    private val _shouldShowBiometricPrompt = MutableStateFlow(false)
    val shouldShowBiometricPrompt: StateFlow<Boolean> = _shouldShowBiometricPrompt.asStateFlow()

    private var lastPauseTime: Long = 0

    fun onAppPaused() {
        lastPauseTime = System.currentTimeMillis()
    }

    suspend fun onAppResumed() {
        val currentTime = System.currentTimeMillis()
        val timeDiff = currentTime - lastPauseTime
        val lockTimeout = settingsRepository.getAppLockTimeout().first()

        // If app was in background for more than timeout period, lock the app
        if (lastPauseTime > 0 && timeDiff > lockTimeout) {
            lockApp()
        }
    }

    fun lockApp() {
        _isAppLocked.value = true
        _shouldShowBiometricPrompt.value = true
    }

    fun unlockApp() {
        _isAppLocked.value = false
        _shouldShowBiometricPrompt.value = false
    }

    fun dismissBiometricPrompt() {
        _shouldShowBiometricPrompt.value = false
    }

    fun shouldLockApp(): Boolean {
        return _isAppLocked.value
    }
}
