package com.adilstudio.project.onevault.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "one_vault_preferences")

class PreferenceManager(private val context: Context) {

    companion object {
        private val KEY_BIOMETRIC_ENABLED = booleanPreferencesKey("biometric_enabled")
    }

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
}
