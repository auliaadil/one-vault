package com.adilstudio.project.onevault.presentation.biometric

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adilstudio.project.onevault.domain.manager.BiometricAuthManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class BiometricLockUiState(
    val canUseBiometric: Boolean = false,
    val isAuthenticating: Boolean = false,
    val isAuthenticated: Boolean = false,
    val errorMessage: String? = null
)

class BiometricLockViewModel(
    private val biometricAuthManager: BiometricAuthManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(BiometricLockUiState())
    val uiState: StateFlow<BiometricLockUiState> = _uiState.asStateFlow()

    fun checkBiometricAvailability(activity: FragmentActivity) {
        viewModelScope.launch {
            val canUse = biometricAuthManager.canAuthenticate(activity)
            _uiState.value = _uiState.value.copy(canUseBiometric = canUse)
        }
    }

    fun authenticateWithBiometric(activity: FragmentActivity) {
        _uiState.value = _uiState.value.copy(
            isAuthenticating = true,
            errorMessage = null
        )

        biometricAuthManager.authenticateUser(
            activity = activity,
            onSuccess = {
                _uiState.value = _uiState.value.copy(
                    isAuthenticating = false,
                    isAuthenticated = true,
                    errorMessage = null
                )
            },
            onError = { error ->
                _uiState.value = _uiState.value.copy(
                    isAuthenticating = false,
                    errorMessage = error
                )
            },
            onCancel = {
                _uiState.value = _uiState.value.copy(
                    isAuthenticating = false
                )
            }
        )
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}
