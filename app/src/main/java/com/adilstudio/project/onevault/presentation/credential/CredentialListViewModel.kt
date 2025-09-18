package com.adilstudio.project.onevault.presentation.credential

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adilstudio.project.onevault.domain.model.Credential
import com.adilstudio.project.onevault.domain.usecase.DeleteCredentialUseCase
import com.adilstudio.project.onevault.domain.usecase.GetCredentialsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CredentialListViewModel(
    private val deleteCredentialUseCase: DeleteCredentialUseCase,
    private val getCredentialsUseCase: GetCredentialsUseCase
) : ViewModel() {
    private val _credentials = MutableStateFlow<List<Credential>>(emptyList())
    val credentials: StateFlow<List<Credential>> = _credentials.asStateFlow()

    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun loadCredentials() {
        viewModelScope.launch {
            try {
                getCredentialsUseCase().collect { _credentials.value = it }
            } catch (e: Exception) {
                _error.value = "Failed to load credentials: ${e.message}"
            }
        }
    }

    fun deleteCredential(id: Long) {
        viewModelScope.launch {
            try {
                deleteCredentialUseCase(id)
                _successMessage.value = "Credential deleted successfully"
                loadCredentials()
            } catch (e: Exception) {
                _error.value = "Failed to delete credential: ${e.message}"
            }
        }
    }

    fun clearSuccessMessage() {
        _successMessage.value = null
    }

    fun clearError() {
        _error.value = null
    }
}
