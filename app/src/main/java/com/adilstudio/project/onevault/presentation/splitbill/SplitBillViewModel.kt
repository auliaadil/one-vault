package com.adilstudio.project.onevault.presentation.splitbill

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adilstudio.project.onevault.domain.model.*
import com.adilstudio.project.onevault.domain.usecase.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SplitBillViewModel(
    private val addSplitBillUseCase: AddSplitBillUseCase,
    private val getSplitBillsUseCase: GetSplitBillsUseCase,
    private val updateSplitBillUseCase: UpdateSplitBillUseCase,
    private val deleteSplitBillUseCase: DeleteSplitBillUseCase,
    private val getParticipantsUseCase: GetParticipantsUseCase,
    private val addParticipantUseCase: AddParticipantUseCase,
    private val calculateSplitBillUseCase: CalculateSplitBillUseCase,
    private val parseBillFromImageUseCase: ParseBillFromImageUseCase
) : ViewModel() {

    // Split Bill State
    private val _splitBills = MutableStateFlow<List<SplitBill>>(emptyList())
    val splitBills: StateFlow<List<SplitBill>> = _splitBills.asStateFlow()

    private val _participants = MutableStateFlow<List<Participant>>(emptyList())
    val participants: StateFlow<List<Participant>> = _participants.asStateFlow()

    // Current Split Bill Creation State
    private val _currentBill = MutableStateFlow<SplitBill?>(null)
    val currentBill: StateFlow<SplitBill?> = _currentBill.asStateFlow()

    private val _billItems = MutableStateFlow<List<BillItem>>(emptyList())
    val billItems: StateFlow<List<BillItem>> = _billItems.asStateFlow()

    private val _selectedParticipants = MutableStateFlow<List<Participant>>(emptyList())
    val selectedParticipants: StateFlow<List<Participant>> = _selectedParticipants.asStateFlow()

    private val _splitMethod = MutableStateFlow(SplitMethod.EQUAL)
    val splitMethod: StateFlow<SplitMethod> = _splitMethod.asStateFlow()

    private val _taxPercentage = MutableStateFlow(0.0)
    val taxPercentage: StateFlow<Double> = _taxPercentage.asStateFlow()

    private val _servicePercentage = MutableStateFlow(0.0)
    val servicePercentage: StateFlow<Double> = _servicePercentage.asStateFlow()

    private val _taxDistributionMethod = MutableStateFlow(TaxDistributionMethod.PROPORTIONAL)
    val taxDistributionMethod: StateFlow<TaxDistributionMethod> = _taxDistributionMethod.asStateFlow()

    private val _serviceDistributionMethod = MutableStateFlow(TaxDistributionMethod.PROPORTIONAL)
    val serviceDistributionMethod: StateFlow<TaxDistributionMethod> = _serviceDistributionMethod.asStateFlow()

    private val _splitResults = MutableStateFlow<List<BillSplitResult>>(emptyList())
    val splitResults: StateFlow<List<BillSplitResult>> = _splitResults.asStateFlow()

    // UI State
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage.asStateFlow()

    init {
        loadSplitBills()
        loadParticipants()
    }

    fun loadSplitBills() {
        viewModelScope.launch {
            getSplitBillsUseCase().collect {
                _splitBills.value = it
            }
        }
    }

    fun loadParticipants() {
        viewModelScope.launch {
            getParticipantsUseCase().collect {
                _participants.value = it
            }
        }
    }

    fun startNewSplitBill() {
        _currentBill.value = null
        _billItems.value = emptyList()
        _selectedParticipants.value = emptyList()
        _splitMethod.value = SplitMethod.EQUAL
        _taxPercentage.value = 0.0
        _servicePercentage.value = 0.0
        _taxDistributionMethod.value = TaxDistributionMethod.PROPORTIONAL
        _serviceDistributionMethod.value = TaxDistributionMethod.PROPORTIONAL
        _splitResults.value = emptyList()
        clearMessages()
    }

    fun parseBillFromImage(imageUri: Uri) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val result = parseBillFromImageUseCase(imageUri)

                _billItems.value = result.items
                _taxPercentage.value = result.taxPercentage
                _servicePercentage.value = result.servicePercentage

                _currentBill.value = SplitBill(
                    title = "Split Bill - ${result.vendor}",
                    vendor = result.vendor,
                    billDate = result.billDate,
                    items = result.items,
                    participants = emptyList(),
                    splitMethod = SplitMethod.EQUAL,
                    taxPercentage = result.taxPercentage,
                    servicePercentage = result.servicePercentage
                )

                _successMessage.value = "Bill parsed successfully!"
            } catch (e: Exception) {
                _error.value = "Failed to parse bill: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateBillItems(items: List<BillItem>) {
        _billItems.value = items
        updateCurrentBill()
    }

    fun addBillItem(item: BillItem) {
        val newId = (_billItems.value.maxOfOrNull { it.id } ?: 0) + 1
        val newItem = item.copy(id = newId)
        _billItems.value = _billItems.value + newItem
        updateCurrentBill()
    }

    fun updateBillItem(item: BillItem) {
        _billItems.value = _billItems.value.map {
            if (it.id == item.id) item else it
        }
        updateCurrentBill()
    }

    fun deleteBillItem(itemId: Long) {
        _billItems.value = _billItems.value.filter { it.id != itemId }
        updateCurrentBill()
    }

    fun updateSelectedParticipants(participants: List<Participant>) {
        _selectedParticipants.value = participants
        updateCurrentBill()
    }

    fun addParticipant(participant: Participant) {
        viewModelScope.launch {
            try {
                val id = addParticipantUseCase(participant)
                val newParticipant = participant.copy(id = id)
                _participants.value = _participants.value + newParticipant
                _selectedParticipants.value = _selectedParticipants.value + newParticipant
                updateCurrentBill()
                _successMessage.value = "Participant added successfully!"
            } catch (e: Exception) {
                _error.value = "Failed to add participant: ${e.message}"
            }
        }
    }

    fun updateSplitMethod(method: SplitMethod) {
        _splitMethod.value = method
        updateCurrentBill()
        calculateSplit()
    }

    fun updateTaxPercentage(percentage: Double) {
        _taxPercentage.value = percentage
        updateCurrentBill()
        calculateSplit()
    }

    fun updateServicePercentage(percentage: Double) {
        _servicePercentage.value = percentage
        updateCurrentBill()
        calculateSplit()
    }

    fun updateTaxDistributionMethod(method: TaxDistributionMethod) {
        _taxDistributionMethod.value = method
        updateCurrentBill()
        calculateSplit()
    }

    fun updateServiceDistributionMethod(method: TaxDistributionMethod) {
        _serviceDistributionMethod.value = method
        updateCurrentBill()
        calculateSplit()
    }

    fun updateItemAssignments(itemId: Long, participantIds: List<Long>) {
        _billItems.value = _billItems.value.map { item ->
            if (item.id == itemId) {
                item.copy(assignedParticipantIds = participantIds)
            } else item
        }
        updateCurrentBill()
        calculateSplit()
    }

    private fun updateCurrentBill() {
        val current = _currentBill.value
        if (current != null) {
            _currentBill.value = current.copy(
                items = _billItems.value,
                participants = _selectedParticipants.value,
                splitMethod = _splitMethod.value,
                taxPercentage = _taxPercentage.value,
                servicePercentage = _servicePercentage.value,
                taxDistributionMethod = _taxDistributionMethod.value,
                serviceDistributionMethod = _serviceDistributionMethod.value,
                updatedAt = System.currentTimeMillis()
            )
        }
    }

    fun calculateSplit() {
        val current = _currentBill.value
        if (current != null && current.participants.isNotEmpty() && current.items.isNotEmpty()) {
            try {
                val results = calculateSplitBillUseCase(current)
                _splitResults.value = results
            } catch (e: Exception) {
                _error.value = "Failed to calculate split: ${e.message}"
            }
        } else {
            _splitResults.value = emptyList()
        }
    }

    fun saveSplitBill() {
        viewModelScope.launch {
            try {
                val current = _currentBill.value
                if (current != null) {
                    _isLoading.value = true
                    addSplitBillUseCase(current)
                    _successMessage.value = "Split bill saved successfully!"
                    loadSplitBills()
                } else {
                    _error.value = "No bill to save"
                }
            } catch (e: Exception) {
                _error.value = "Failed to save split bill: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteSplitBill(id: Long) {
        viewModelScope.launch {
            try {
                deleteSplitBillUseCase(id)
                _successMessage.value = "Split bill deleted successfully!"
                loadSplitBills()
            } catch (e: Exception) {
                _error.value = "Failed to delete split bill: ${e.message}"
            }
        }
    }

    fun clearMessages() {
        _error.value = null
        _successMessage.value = null
    }
}
