package com.adilstudio.project.onevault.domain.repository

import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    suspend fun setBiometricEnabled(enabled: Boolean)
    fun getBiometricEnabled(): Flow<Boolean>
    suspend fun setCurrency(currencyCode: String)
    fun getCurrency(): Flow<String>
}
