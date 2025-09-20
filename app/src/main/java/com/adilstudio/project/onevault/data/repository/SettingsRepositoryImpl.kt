package com.adilstudio.project.onevault.data.repository

import com.adilstudio.project.onevault.data.local.PreferenceManager
import com.adilstudio.project.onevault.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow

class SettingsRepositoryImpl(
    private val preferenceManager: PreferenceManager
) : SettingsRepository {

    override suspend fun setBiometricEnabled(enabled: Boolean) {
        preferenceManager.setBiometricEnabled(enabled)
    }

    override fun getBiometricEnabled(): Flow<Boolean> {
        return preferenceManager.getBiometricEnabledFlow()
    }

    override suspend fun setAppLockTimeout(timeoutMs: Long) {
        preferenceManager.setAppLockTimeout(timeoutMs)
    }

    override fun getAppLockTimeout(): Flow<Long> {
        return preferenceManager.getAppLockTimeoutFlow()
    }
}
