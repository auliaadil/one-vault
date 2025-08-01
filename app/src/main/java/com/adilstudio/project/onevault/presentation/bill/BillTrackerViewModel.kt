package com.adilstudio.project.onevault.presentation.bill

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adilstudio.project.onevault.domain.model.Bill
import com.adilstudio.project.onevault.domain.usecase.GetBillsUseCase
import com.adilstudio.project.onevault.domain.usecase.AddBillUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class BillTrackerViewModel(
    private val getBillsUseCase: GetBillsUseCase,
    private val addBillUseCase: AddBillUseCase
) : ViewModel() {
    private val _bills = MutableStateFlow<List<Bill>>(emptyList())
    val bills: StateFlow<List<Bill>> = _bills.asStateFlow()

    init {
        loadBills()
    }

    fun loadBills() {
        viewModelScope.launch {
            getBillsUseCase().collect { _bills.value = it }
        }
    }

    fun addBill(bill: Bill) {
        viewModelScope.launch {
            addBillUseCase(bill)
        }
    }
}
