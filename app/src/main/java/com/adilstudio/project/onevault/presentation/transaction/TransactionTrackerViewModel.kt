package com.adilstudio.project.onevault.presentation.transaction

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adilstudio.project.onevault.domain.model.Account
import com.adilstudio.project.onevault.domain.model.Transaction
import com.adilstudio.project.onevault.domain.model.TransactionCategory
import com.adilstudio.project.onevault.domain.usecase.GetTransactionsUseCase
import com.adilstudio.project.onevault.domain.usecase.AddTransactionUseCase
import com.adilstudio.project.onevault.domain.usecase.UpdateTransactionUseCase
import com.adilstudio.project.onevault.domain.usecase.DeleteTransactionUseCase
import com.adilstudio.project.onevault.domain.usecase.GetAccountsUseCase
import com.adilstudio.project.onevault.domain.usecase.GetTransactionCategoriesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TransactionTrackerViewModel(
    private val getTransactionsUseCase: GetTransactionsUseCase,
    private val addTransactionUseCase: AddTransactionUseCase,
    private val updateTransactionUseCase: UpdateTransactionUseCase,
    private val deleteTransactionUseCase: DeleteTransactionUseCase,
    private val getTransactionCategoriesUseCase: GetTransactionCategoriesUseCase,
    private val getAccountsUseCase: GetAccountsUseCase,
) : ViewModel() {
    private val _transactions = MutableStateFlow<List<Transaction>>(emptyList())
    val transactions: StateFlow<List<Transaction>> = _transactions.asStateFlow()

    private val _categories = MutableStateFlow<List<TransactionCategory>>(emptyList())
    val categories: StateFlow<List<TransactionCategory>> = _categories.asStateFlow()

    private val _accounts = MutableStateFlow<List<Account>>(emptyList())
    val accounts: StateFlow<List<Account>> = _accounts.asStateFlow()

    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun loadTransactions() {
        viewModelScope.launch {
            try {
                getTransactionsUseCase().collect { _transactions.value = it }
            } catch (e: Exception) {
                _error.value = "Failed to load transactions: ${e.message}"
            }
        }
        viewModelScope.launch {
            try {
                getTransactionCategoriesUseCase().collect { _categories.value = it }
            } catch (e: Exception) {
                _error.value = "Failed to load categories: ${e.message}"
            }
        }
        viewModelScope.launch {
            try {
                getAccountsUseCase().collect { _accounts.value = it }
            } catch (e: Exception) {
                _error.value = "Failed to load categories: ${e.message}"
            }
        }
    }

    fun addTransaction(transaction: Transaction) {
        viewModelScope.launch {
            try {
                addTransactionUseCase(transaction)
                _successMessage.value = "Transaction added successfully"
                loadTransactions() // Reload transactions to update the UI
            } catch (e: Exception) {
                _error.value = "Failed to add transaction: ${e.message}"
            }
        }
    }

    fun updateTransaction(transaction: Transaction) {
        viewModelScope.launch {
            try {
                updateTransactionUseCase(transaction)
                _successMessage.value = "Transaction updated successfully"
                loadTransactions()
            } catch (e: Exception) {
                _error.value = "Failed to update transaction: ${e.message}"
            }
        }
    }

    fun deleteTransaction(transactionId: Long) {
        viewModelScope.launch {
            try {
                deleteTransactionUseCase(transactionId)
                _successMessage.value = "Transaction deleted successfully"
                loadTransactions()
            } catch (e: Exception) {
                _error.value = "Failed to delete transaction: ${e.message}"
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
