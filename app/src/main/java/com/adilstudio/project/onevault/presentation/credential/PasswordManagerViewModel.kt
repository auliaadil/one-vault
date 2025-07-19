package com.adilstudio.project.onevault.presentation.credential

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adilstudio.project.onevault.domain.model.Credential
import com.adilstudio.project.onevault.domain.usecase.AddCredentialUseCase
import com.adilstudio.project.onevault.domain.usecase.GetCredentialsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PasswordManagerViewModel(
    private val addCredentialUseCase: AddCredentialUseCase,
    private val getCredentialsUseCase: GetCredentialsUseCase
) : ViewModel() {
    private val _credentials = MutableStateFlow<List<Credential>>(emptyList())
    val credentials: StateFlow<List<Credential>> = _credentials.asStateFlow()

    fun loadCredentials() {
        viewModelScope.launch {
            getCredentialsUseCase().collect { _credentials.value = it }
        }
    }

    fun addCredential(credential: Credential) {
        viewModelScope.launch {
            addCredentialUseCase(credential)
            loadCredentials()
        }
    }
}
