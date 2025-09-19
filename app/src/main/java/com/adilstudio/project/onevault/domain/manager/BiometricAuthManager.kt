package com.adilstudio.project.onevault.domain.manager

import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.adilstudio.project.onevault.R
import com.adilstudio.project.onevault.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.first

class BiometricAuthManager(
    private val settingsRepository: SettingsRepository
) {

    suspend fun isBiometricEnabled(): Boolean {
        return settingsRepository.getBiometricEnabled().first()
    }

    fun canAuthenticate(activity: FragmentActivity): Boolean {
        val biometricManager = BiometricManager.from(activity)
        return biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_WEAK) == BiometricManager.BIOMETRIC_SUCCESS
    }

    fun authenticateUser(
        activity: FragmentActivity,
        onSuccess: () -> Unit,
        onError: (String) -> Unit,
        onCancel: () -> Unit
    ) {
        val executor = ContextCompat.getMainExecutor(activity)
        val biometricPrompt = BiometricPrompt(activity, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    when (errorCode) {
                        BiometricPrompt.ERROR_USER_CANCELED -> onCancel()
                        BiometricPrompt.ERROR_NEGATIVE_BUTTON -> onCancel()
                        else -> onError(errString.toString())
                    }
                }

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    onSuccess()
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    // Don't handle failed attempts here, let user try again
                }
            })

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(activity.getString(R.string.biometric_unlock_title))
            .setSubtitle(activity.getString(R.string.biometric_unlock_subtitle))
            .setDescription(activity.getString(R.string.biometric_unlock_description))
            .setNegativeButtonText(activity.getString(R.string.cancel))
            .build()

        biometricPrompt.authenticate(promptInfo)
    }
}
