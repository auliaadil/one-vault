package com.adilstudio.project.onevault.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
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

    suspend fun saveString(key: String, value: String) {
        val prefKey = stringPreferencesKey(key)
        context.dataStore.edit { preferences ->
            preferences[prefKey] = value
        }
    }

    suspend fun setDefaultCredentialTemplate(templateJson: String) {
        context.dataStore.edit { preferences ->
            preferences[KEY_DEFAULT_CREDENTIAL_TEMPLATE] = templateJson
        }
    }

    fun getDefaultCredentialTemplateFlow(): Flow<String?> {
        return context.dataStore.data.map { preferences ->
            preferences[KEY_DEFAULT_CREDENTIAL_TEMPLATE]
        }
    }

    suspend fun setCurrency(currencyCode: String) {
        context.dataStore.edit { preferences ->
            preferences[KEY_CURRENCY] = currencyCode
        }
    }

    fun getCurrencyFlow(): Flow<String> {
        return context.dataStore.data.map { preferences ->
            preferences[KEY_CURRENCY] ?: "IDR" // Default to Indonesian Rupiah
        }
    }

    companion object {
        private val KEY_BIOMETRIC_ENABLED = booleanPreferencesKey("biometric_enabled")
        private val KEY_DEFAULT_CREDENTIAL_TEMPLATE = stringPreferencesKey("default_credential_template")
        private val KEY_CURRENCY = stringPreferencesKey("currency")
    }
}
