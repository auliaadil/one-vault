package com.adilstudio.project.onevault.presentation.credential.credentialform

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adilstudio.project.onevault.domain.model.Credential
import com.adilstudio.project.onevault.domain.usecase.AddCredentialUseCase
import com.adilstudio.project.onevault.domain.usecase.UpdateCredentialUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CredentialFormViewModel(
    private val addCredentialUseCase: AddCredentialUseCase,
    private val updateCredentialUseCase: UpdateCredentialUseCase
) : ViewModel() {

    // Form state
    private val _serviceName = MutableStateFlow("")
    val serviceName: StateFlow<String> = _serviceName.asStateFlow()

    private val _userAccount = MutableStateFlow("")
    val userAccount: StateFlow<String> = _userAccount.asStateFlow()

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password.asStateFlow()

    private val _useTemplate = MutableStateFlow(false)
    val useTemplate: StateFlow<Boolean> = _useTemplate.asStateFlow()

    // Rules list - using mutableStateListOf for automatic Compose recomposition
    private val _rules: SnapshotStateList<CredentialRule> = mutableStateListOf()
    val rules: List<CredentialRule> = _rules

    private val _generatedPassword = MutableStateFlow("")
    val generatedPassword: StateFlow<String> = _generatedPassword.asStateFlow()

    // UI state
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage.asStateFlow()

    // For editing existing credentials
    private var currentCredentialId: Long? = null

    fun updateServiceName(name: String) {
        _serviceName.value = name
        if (_useTemplate.value) {
            generatePassword()
        }
    }

    fun updateUserAccount(account: String) {
        _userAccount.value = account
        if (_useTemplate.value) {
            generatePassword()
        }
    }

    fun updatePassword(password: String) {
        if (!_useTemplate.value) {
            _password.value = password
        }
    }

    fun toggleUseTemplate() {
        _useTemplate.value = !_useTemplate.value
        if (_useTemplate.value) {
            generatePassword()
        } else {
            _generatedPassword.value = ""
        }
    }

    fun addRule(rule: CredentialRule) {
        _rules.add(rule)
        if (_useTemplate.value) {
            generatePassword()
        }
    }

    fun removeRule(ruleId: String) {
        _rules.removeAll { it.id == ruleId }
        if (_useTemplate.value) {
            generatePassword()
        }
    }

    fun updateRule(updatedRule: CredentialRule) {
        val index = _rules.indexOfFirst { it.id == updatedRule.id }
        if (index >= 0) {
            _rules[index] = updatedRule
            if (_useTemplate.value) {
                generatePassword()
            }
        }
    }

    fun moveRule(fromIndex: Int, toIndex: Int) {
        if (fromIndex < _rules.size && toIndex < _rules.size) {
            val item = _rules.removeAt(fromIndex)
            _rules.add(toIndex, item)
            if (_useTemplate.value) {
                generatePassword()
            }
        }
    }

    fun generatePassword() {
        if (!_useTemplate.value) {
            _generatedPassword.value = ""
            return
        }

        val serviceNameValue = _serviceName.value
        val userAccountValue = _userAccount.value

        val passwordBuilder = StringBuilder()

        _rules.forEach { rule ->
            when (rule) {
                is CredentialRule.FromServiceName -> {
                    if (serviceNameValue.isNotEmpty()) {
                        val chars = serviceNameValue.take(rule.length)
                        passwordBuilder.append(chars.applyCasing(rule.casing))
                    }
                }
                is CredentialRule.FromUserName -> {
                    if (userAccountValue.isNotEmpty()) {
                        val chars = userAccountValue.take(rule.length)
                        passwordBuilder.append(chars.applyCasing(rule.casing))
                    }
                }
                is CredentialRule.FixedString -> {
                    passwordBuilder.append(rule.value)
                }
            }
        }

        _generatedPassword.value = passwordBuilder.toString()
    }

    fun getCurrentPassword(): String {
        return if (_useTemplate.value) {
            _generatedPassword.value
        } else {
            _password.value
        }
    }

    fun saveCredential() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                val finalPassword = getCurrentPassword()

                if (_serviceName.value.isBlank() || _userAccount.value.isBlank() || finalPassword.isBlank()) {
                    _error.value = "Please fill in all required fields"
                    return@launch
                }

                val passwordTemplate = if (_useTemplate.value && _rules.isNotEmpty()) {
                    PasswordTemplateHelper.serializeTemplate(_useTemplate.value, _rules.toList())
                } else null

                val credential = Credential(
                    id = currentCredentialId ?: 0L,
                    serviceName = _serviceName.value,
                    username = _userAccount.value,
                    password = finalPassword,
                    passwordTemplate = passwordTemplate
                )

                if (currentCredentialId != null) {
                    updateCredentialUseCase(credential)
                    _successMessage.value = "Credential updated successfully"
                } else {
                    addCredentialUseCase(credential)
                    _successMessage.value = "Credential saved successfully"
                }

            } catch (e: Exception) {
                _error.value = "Failed to save credential: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadCredential(credential: Credential) {
        currentCredentialId = credential.id
        _serviceName.value = credential.serviceName
        _userAccount.value = credential.username
        _password.value = credential.password

        // Load password template if exists
        val (useTemplate, rules) = PasswordTemplateHelper.deserializeTemplate(credential.passwordTemplate)
        _useTemplate.value = useTemplate
        _rules.clear()
        _rules.addAll(rules)

        if (useTemplate) {
            generatePassword()
        }
    }

    fun clearForm() {
        currentCredentialId = null
        _serviceName.value = ""
        _userAccount.value = ""
        _password.value = ""
        _useTemplate.value = false
        _generatedPassword.value = ""
        _rules.clear()
        _error.value = null
        _successMessage.value = null
    }

    fun clearError() {
        _error.value = null
    }

    fun clearSuccessMessage() {
        _successMessage.value = null
    }
}
