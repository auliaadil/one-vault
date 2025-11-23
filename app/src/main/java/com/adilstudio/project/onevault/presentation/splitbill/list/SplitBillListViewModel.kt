package com.adilstudio.project.onevault.presentation.splitbill.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adilstudio.project.onevault.domain.model.SplitBill
import com.adilstudio.project.onevault.domain.usecase.DeleteSplitBillUseCase
import com.adilstudio.project.onevault.domain.usecase.GetSplitBillsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SplitBillListViewModel(
    private val getSplitBillsUseCase: GetSplitBillsUseCase,
    private val deleteSplitBillUseCase: DeleteSplitBillUseCase
) : ViewModel() {
    private val _splitBills = MutableStateFlow<List<SplitBill>>(emptyList())
    val splitBills: StateFlow<List<SplitBill>> = _splitBills.asStateFlow()

    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun loadSplitBills() {
        viewModelScope.launch {
            try {
                getSplitBillsUseCase().collect { _splitBills.value = it }
            } catch (e: Exception) {
                _error.value = "Failed to load split bills: ${e.message}"
            }
        }
    }

    fun deleteSplitBill(id: Long) {
        viewModelScope.launch {
            try {
                deleteSplitBillUseCase(id)
                _successMessage.value = "Split bill deleted successfully"
                loadSplitBills()
            } catch (e: Exception) {
                _error.value = "Failed to delete split bill: ${e.message}"
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

