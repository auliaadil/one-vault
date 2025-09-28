package com.adilstudio.project.onevault.domain.repository

import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    suspend fun setBiometricEnabled(enabled: Boolean)
    fun getBiometricEnabled(): Flow<Boolean>
    suspend fun setAppLockTimeout(timeoutMs: Long)
    fun getAppLockTimeout(): Flow<Long>
    suspend fun setAppLockLastPauseTime(lastPauseTimeMs: Long)
    fun getAppLockLastPauseTime(): Flow<Long>
}
