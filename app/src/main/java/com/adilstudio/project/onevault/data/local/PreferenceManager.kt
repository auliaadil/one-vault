package com.adilstudio.project.onevault.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "one_vault_preferences")

class PreferenceManager(private val context: Context) {

    suspend fun setBiometricEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[KEY_BIOMETRIC_ENABLED] = enabled
        }
    }

    fun getBiometricEnabledFlow(): Flow<Boolean> {
        return context.dataStore.data.map { preferences ->
            preferences[KEY_BIOMETRIC_ENABLED] ?: false
        }
    }

    suspend fun setAppLockTimeout(timeoutMs: Long) {
        context.dataStore.edit { preferences ->
            preferences[KEY_APP_LOCK_TIMEOUT] = timeoutMs
        }
    }

    fun getAppLockTimeoutFlow(): Flow<Long> {
        return context.dataStore.data.map { preferences ->
            preferences[KEY_APP_LOCK_TIMEOUT] ?: DEFAULT_LOCK_TIMEOUT
        }
    }

    suspend fun setAppLockLastPauseTime(lastPauseTimeMs: Long) {
        context.dataStore.edit { preferences ->
            preferences[KEY_APP_LOCK_LAST_PAUSE_TIME] = lastPauseTimeMs
        }
    }

    fun getAppLockLastPauseTimeFlow(): Flow<Long> {
        return context.dataStore.data.map { preferences ->
            preferences[KEY_APP_LOCK_LAST_PAUSE_TIME] ?: 0L
        }
    }

    suspend fun saveString(key: String, value: String) {
        val prefKey = stringPreferencesKey(key)
        context.dataStore.edit { preferences ->
            preferences[prefKey] = value
        }
    }

    companion object {
        private val KEY_BIOMETRIC_ENABLED = booleanPreferencesKey("biometric_enabled")
        private val KEY_APP_LOCK_TIMEOUT = longPreferencesKey("app_lock_timeout")
        private val KEY_APP_LOCK_LAST_PAUSE_TIME = longPreferencesKey("app_lock_last_pause_time")
        const val DEFAULT_LOCK_TIMEOUT = 30000L // 30 seconds
    }
}
