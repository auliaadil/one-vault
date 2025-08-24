package com.adilstudio.project.onevault.presentation.settings

import android.app.Application
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.adilstudio.project.onevault.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

private val Application.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingsViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val _biometricEnabled = MutableStateFlow(false)
    val biometricEnabled: StateFlow<Boolean> = _biometricEnabled.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val BIOMETRIC_ENABLED_KEY = booleanPreferencesKey("biometric_enabled")

    init {
        loadSettings()
    }

    private fun loadSettings() {
        viewModelScope.launch {
            getApplication<Application>().dataStore.data.map { preferences ->
                preferences[BIOMETRIC_ENABLED_KEY] ?: false
            }.collect { enabled ->
                _biometricEnabled.value = enabled
            }
        }
    }

    fun enableBiometric(activity: FragmentActivity) {
        val biometricManager = BiometricManager.from(activity)
        when (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_WEAK)) {
            BiometricManager.BIOMETRIC_SUCCESS -> {
                // Show biometric prompt to verify user can authenticate
                showBiometricPrompt(activity) { success ->
                    if (success) {
                        setBiometricEnabled(true)
                    }
                }
            }
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
                _errorMessage.value = activity.getString(R.string.biometric_error_hw_unavailable)
            }
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                _errorMessage.value = activity.getString(R.string.biometric_error_hw_unavailable)
            }
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                _errorMessage.value = activity.getString(R.string.biometric_error_no_biometrics)
            }
        }
    }

    fun disableBiometric() {
        setBiometricEnabled(false)
    }

    private fun setBiometricEnabled(enabled: Boolean) {
        viewModelScope.launch {
            getApplication<Application>().dataStore.edit { preferences ->
                preferences[BIOMETRIC_ENABLED_KEY] = enabled
            }
            _biometricEnabled.value = enabled
        }
    }

    private fun showBiometricPrompt(
        activity: FragmentActivity,
        onResult: (Boolean) -> Unit
    ) {
        val executor = ContextCompat.getMainExecutor(activity)
        val biometricPrompt = BiometricPrompt(activity, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    onResult(false)
                    _errorMessage.value = when (errorCode) {
                        BiometricPrompt.ERROR_USER_CANCELED -> activity.getString(R.string.biometric_error_user_cancel)
                        else -> activity.getString(R.string.biometric_error_generic)
                    }
                }

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    onResult(true)
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    // Don't call onResult(false) here, as this is called for each failed attempt
                    // The user can try again until they cancel or succeed
                }
            })

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(activity.getString(R.string.biometric_prompt_title))
            .setSubtitle(activity.getString(R.string.biometric_prompt_subtitle))
            .setDescription(activity.getString(R.string.biometric_prompt_description))
            .setNegativeButtonText(activity.getString(R.string.cancel))
            .build()

        biometricPrompt.authenticate(promptInfo)
    }

    fun clearErrorMessage() {
        _errorMessage.value = null
    }

    fun setErrorMessage(message: String) {
        _errorMessage.value = message
    }
}
