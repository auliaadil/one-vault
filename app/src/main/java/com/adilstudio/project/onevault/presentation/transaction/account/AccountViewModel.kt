package com.adilstudio.project.onevault.presentation.transaction.account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adilstudio.project.onevault.domain.model.Account
import com.adilstudio.project.onevault.domain.usecase.AddAccountUseCase
import com.adilstudio.project.onevault.domain.usecase.DeleteAccountUseCase
import com.adilstudio.project.onevault.domain.usecase.GetAccountsCountUseCase
import com.adilstudio.project.onevault.domain.usecase.GetAccountsUseCase
import com.adilstudio.project.onevault.domain.usecase.UpdateAccountUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AccountViewModel(
    private val getAccountsUseCase: GetAccountsUseCase,
    private val addAccountUseCase: AddAccountUseCase,
    private val updateAccountUseCase: UpdateAccountUseCase,
    private val deleteAccountUseCase: DeleteAccountUseCase,
    private val getAccountsCountUseCase: GetAccountsCountUseCase
) : ViewModel() {

    private val _accounts = MutableStateFlow<List<Account>>(emptyList())
    val accounts: StateFlow<List<Account>> = _accounts.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage.asStateFlow()

    init {
        loadAccounts()
    }

    private fun loadAccounts() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                getAccountsUseCase().collect { accountList ->
                    _accounts.value = accountList
                    _isLoading.value = false
                }
            } catch (e: Exception) {
                _error.value = "Failed to load accounts: ${e.message}"
                _isLoading.value = false
            }
        }
    }

    suspend fun checkAndInitializeDefaultAccounts(defaultAccounts: List<Account>) {
        try {
            val accountsCount = getAccountsCountUseCase()
            if (accountsCount == 0) {
                // No accounts exist, initialize with defaults
                defaultAccounts.forEach { account ->
                    addAccountUseCase(account)
                }
            }
        } catch (e: Exception) {
            _error.value = "Failed to initialize default accounts: ${e.message}"
        }
    }

    fun addAccount(name: String, amount: Double, description: String) {
        viewModelScope.launch {
            try {
                val currentTime = System.currentTimeMillis()
                val account = Account(
                    id = currentTime, // Use timestamp as unique Long ID
                    name = name,
                    amount = amount,
                    description = description.takeIf { it.isNotBlank() },
                    isEditable = true,
                    createdAt = currentTime,
                    updatedAt = currentTime
                )
                addAccountUseCase(account)
                _successMessage.value = "Account '$name' added successfully"
            } catch (e: Exception) {
                _error.value = "Failed to add account: ${e.message}"
            }
        }
    }

    fun updateAccount(account: Account) {
        viewModelScope.launch {
            try {
                updateAccountUseCase(account.copy(updatedAt = System.currentTimeMillis()))
                _successMessage.value = "Account '${account.name}' updated successfully"
            } catch (e: Exception) {
                _error.value = "Failed to update account: ${e.message}"
            }
        }
    }

    fun deleteAccount(accountId: Long) {
        viewModelScope.launch {
            try {
                deleteAccountUseCase(accountId)
                _successMessage.value = "Account deleted successfully"
            } catch (e: Exception) {
                _error.value = "Failed to delete account: ${e.message}"
            }
        }
    }

    fun clearError() {
        _error.value = null
    }

    fun clearSuccessMessage() {
        _successMessage.value = null
    }
}
