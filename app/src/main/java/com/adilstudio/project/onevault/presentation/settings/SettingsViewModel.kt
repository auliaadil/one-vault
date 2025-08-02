package com.adilstudio.project.onevault.presentation.settings

import android.content.Context
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adilstudio.project.onevault.R
import com.adilstudio.project.onevault.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val settingsRepository: SettingsRepository
) : ViewModel() {
    
    private val _biometricEnabled = MutableStateFlow(false)
    val biometricEnabled: StateFlow<Boolean> = _biometricEnabled.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    init {
        loadSettings()
    }

    private fun loadSettings() {
        viewModelScope.launch {
            settingsRepository.getBiometricEnabled().collect { enabled ->
                _biometricEnabled.value = enabled
            }
        }
    }

    fun enableBiometric(context: Context) {
        val biometricManager = BiometricManager.from(context)
        when (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_WEAK)) {
            BiometricManager.BIOMETRIC_SUCCESS -> {
                // Show biometric prompt to verify user can authenticate
                if (context is FragmentActivity) {
                    showBiometricPrompt(context) { success ->
                        if (success) {
                            setBiometricEnabled(true)
                        }
                    }
                }
            }
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
                _errorMessage.value = context.getString(R.string.biometric_error_hw_unavailable)
            }
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                _errorMessage.value = context.getString(R.string.biometric_error_hw_unavailable)
            }
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                _errorMessage.value = context.getString(R.string.biometric_error_no_biometrics)
            }
        }
    }

    fun disableBiometric() {
        setBiometricEnabled(false)
    }

    private fun setBiometricEnabled(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.setBiometricEnabled(enabled)
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
                    onResult(false)
                    _errorMessage.value = activity.getString(R.string.biometric_error_generic)
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
}
