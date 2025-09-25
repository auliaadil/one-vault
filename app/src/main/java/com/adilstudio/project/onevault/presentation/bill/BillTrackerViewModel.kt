package com.adilstudio.project.onevault.presentation.bill

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adilstudio.project.onevault.domain.model.Bill
import com.adilstudio.project.onevault.domain.usecase.GetBillsUseCase
import com.adilstudio.project.onevault.domain.usecase.AddBillUseCase
import com.adilstudio.project.onevault.domain.usecase.UpdateBillUseCase
import com.adilstudio.project.onevault.domain.usecase.DeleteBillUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class BillTrackerViewModel(
    private val getBillsUseCase: GetBillsUseCase,
    private val addBillUseCase: AddBillUseCase,
    private val updateBillUseCase: UpdateBillUseCase,
    private val deleteBillUseCase: DeleteBillUseCase
) : ViewModel() {
    private val _bills = MutableStateFlow<List<Bill>>(emptyList())
    val bills: StateFlow<List<Bill>> = _bills.asStateFlow()

    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun loadBills() {
        viewModelScope.launch {
            try {
                getBillsUseCase().collect { _bills.value = it }
            } catch (e: Exception) {
                _error.value = "Failed to load bills: ${e.message}"
            }
        }
    }

    fun addBill(bill: Bill) {
        viewModelScope.launch {
            try {
                addBillUseCase(bill)
                _successMessage.value = "Bill added successfully"
                loadBills() // Reload bills to update the UI
            } catch (e: Exception) {
                _error.value = "Failed to add bill: ${e.message}"
            }
        }
    }

    fun updateBill(bill: Bill) {
        viewModelScope.launch {
            try {
                updateBillUseCase(bill)
                _successMessage.value = "Bill updated successfully"
                loadBills()
            } catch (e: Exception) {
                _error.value = "Failed to update bill: ${e.message}"
            }
        }
    }

    fun deleteBill(billId: Long) {
        viewModelScope.launch {
            try {
                deleteBillUseCase(billId)
                _successMessage.value = "Bill deleted successfully"
                loadBills()
            } catch (e: Exception) {
                _error.value = "Failed to delete bill: ${e.message}"
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
