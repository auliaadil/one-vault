package com.adilstudio.project.onevault.presentation.splitbill.form

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adilstudio.project.onevault.domain.manager.SplitCalculator
import com.adilstudio.project.onevault.domain.manager.OcrManager
import com.adilstudio.project.onevault.domain.model.*
import com.adilstudio.project.onevault.domain.repository.SplitBillRepository
import com.adilstudio.project.onevault.domain.util.FeatureFlag
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

data class SplitBillUiState(
    val currentStep: SplitBillStep = SplitBillStep.IMAGE_CAPTURE,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val ocrResult: OcrResult? = null,
    val splitBill: SplitBill? = null,
    val items: List<SplitItem> = emptyList(),
    val suggestedItems: List<SplitItem> = emptyList(), // OCR suggested items
    val participants: List<SplitParticipant> = emptyList(),
    val calculatedParticipants: List<SplitParticipant> = emptyList(),
    val validationErrors: List<String> = emptyList(),
    val isSaveSuccessful: Boolean = false,
    val savedSplitBillId: Long? = null,
    val saveSuccessMessage: String? = null,
    val exportSuccessMessage: String? = null
)

enum class SplitBillStep {
    IMAGE_CAPTURE,
    OCR_PROCESSING,
    OCR_REVIEW,
    PARTICIPANT_INPUT,
    ITEM_ASSIGNMENT,
    SUMMARY
}

class SplitBillFormViewModel(
    private val splitBillRepository: SplitBillRepository,
    private val splitCalculator: SplitCalculator,
    private val ocrManager: OcrManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(SplitBillUiState())
    val uiState: StateFlow<SplitBillUiState> = _uiState.asStateFlow()

    var title by mutableStateOf("")
        private set
    var merchant by mutableStateOf("")
        private set
    var date: String by mutableStateOf(SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()))
        private set
    var tax by mutableDoubleStateOf(0.0)
        private set
    var serviceFee by mutableDoubleStateOf(0.0)
        private set

    fun processOcrResult(ocrResult: OcrResult) {
        val suggestedItems = ocrResult.lineItems.mapIndexed { index, lineItem ->
            SplitItem(
                id = index.toLong(),
                splitBillId = 0L,
                description = lineItem.description,
                price = lineItem.price
            )
        }

        _uiState.value = _uiState.value.copy(
            ocrResult = ocrResult,
            suggestedItems = suggestedItems
            // Note: items list remains empty, title and merchant are not auto-filled
        )

        // Only auto-fill tax and service fee from OCR (if detected)
        tax = ocrResult.tax
        serviceFee = ocrResult.serviceFee
    }

    fun updateTitle(newTitle: String) {
        title = newTitle
    }

    fun updateMerchant(newMerchant: String) {
        merchant = newMerchant
    }

    fun updateDate(newDate: String) {
        date = newDate
    }

    fun updateTax(newTax: Double) {
        tax = newTax
        recalculateShares()
    }

    fun updateServiceFee(newServiceFee: Double) {
        serviceFee = newServiceFee
        recalculateShares()
    }

    fun addParticipant(name: String) {
        if (name.isBlank()) return

        val currentParticipants = _uiState.value.participants
        if (currentParticipants.any { it.name == name }) return

        val newParticipant = SplitParticipant(
            splitBillId = 0L,
            name = name
        )

        _uiState.value = _uiState.value.copy(
            participants = currentParticipants + newParticipant
        )
    }

    fun removeParticipant(name: String) {
        _uiState.value = _uiState.value.copy(
            participants = _uiState.value.participants.filter { it.name != name },
            items = _uiState.value.items.map { item ->
                item.copy(assignedQuantities = item.assignedQuantities.filterKeys { it != name })
            }
        )
        recalculateShares()
    }

    fun updateItemAssignment(itemId: Long, participantQuantities: Map<String, Int>) {
        _uiState.value = _uiState.value.copy(
            items = _uiState.value.items.map { item ->
                if (item.id == itemId) {
                    item.copy(assignedQuantities = participantQuantities)
                } else item
            }
        )
        recalculateShares()
    }

    fun updateItemDescription(itemId: Long, description: String) {
        _uiState.value = _uiState.value.copy(
            items = _uiState.value.items.map { item ->
                if (item.id == itemId) {
                    item.copy(description = description)
                } else item
            }
        )
    }

    fun updateItemPrice(itemId: Long, price: Double) {
        _uiState.value = _uiState.value.copy(
            items = _uiState.value.items.map { item ->
                if (item.id == itemId) {
                    item.copy(price = price)
                } else item
            }
        )
        recalculateShares()
    }

    fun addItem() {
        val newId = (_uiState.value.items.maxOfOrNull { it.id } ?: 0) + 1
        val newItem = SplitItem(
            id = newId,
            splitBillId = 0L,
            description = "",
            price = 0.0
        )

        _uiState.value = _uiState.value.copy(
            items = _uiState.value.items + newItem
        )
    }

    fun removeItem(itemId: Long) {
        _uiState.value = _uiState.value.copy(
            items = _uiState.value.items.filter { it.id != itemId }
        )
        recalculateShares()
    }

    fun addSuggestedItem(suggestedItem: SplitItem) {
        val newId = (_uiState.value.items.maxOfOrNull { it.id } ?: 0) + 1
        val newItem = suggestedItem.copy(id = newId)

        _uiState.value = _uiState.value.copy(
            items = _uiState.value.items + newItem,
            suggestedItems = _uiState.value.suggestedItems.filter { it.id != suggestedItem.id }
        )
        recalculateShares()
    }

    fun removeSuggestedItem(suggestedItemId: Long) {
        _uiState.value = _uiState.value.copy(
            suggestedItems = _uiState.value.suggestedItems.filter { it.id != suggestedItemId }
        )
    }

    private fun recalculateShares() {
        val validationErrors = splitCalculator.validateAssignments(_uiState.value.items)

        val calculatedParticipants = if (validationErrors.isEmpty()) {
            splitCalculator.calculateShares(
                items = _uiState.value.items,
                participants = _uiState.value.participants,
                tax = tax,
                serviceFee = serviceFee
            )
        } else {
            _uiState.value.participants
        }

        _uiState.value = _uiState.value.copy(
            calculatedParticipants = calculatedParticipants,
            validationErrors = validationErrors
        )
    }

    fun nextStep() {
        val currentStep = _uiState.value.currentStep
        val nextStep = when (currentStep) {
            SplitBillStep.IMAGE_CAPTURE -> {
                if (FeatureFlag.isOcrSplitBillEnabled()) {
                    SplitBillStep.OCR_PROCESSING
                } else {
                    SplitBillStep.OCR_REVIEW // Skip OCR processing, go directly to manual review
                }
            }
            SplitBillStep.OCR_PROCESSING -> SplitBillStep.OCR_REVIEW
            SplitBillStep.OCR_REVIEW -> SplitBillStep.PARTICIPANT_INPUT
            SplitBillStep.PARTICIPANT_INPUT -> SplitBillStep.ITEM_ASSIGNMENT
            SplitBillStep.ITEM_ASSIGNMENT -> SplitBillStep.SUMMARY
            SplitBillStep.SUMMARY -> return // Already at final step
        }

        _uiState.value = _uiState.value.copy(currentStep = nextStep)

        if (nextStep == SplitBillStep.SUMMARY) {
            recalculateShares()
        }
    }

    fun previousStep() {
        val currentStep = _uiState.value.currentStep
        val previousStep = when (currentStep) {
            SplitBillStep.OCR_PROCESSING -> SplitBillStep.IMAGE_CAPTURE
            SplitBillStep.OCR_REVIEW -> {
                if (FeatureFlag.isOcrSplitBillEnabled()) {
                    SplitBillStep.OCR_PROCESSING
                } else {
                    SplitBillStep.IMAGE_CAPTURE // Skip OCR processing when going back
                }
            }
            SplitBillStep.PARTICIPANT_INPUT -> SplitBillStep.OCR_REVIEW
            SplitBillStep.ITEM_ASSIGNMENT -> SplitBillStep.PARTICIPANT_INPUT
            SplitBillStep.SUMMARY -> SplitBillStep.ITEM_ASSIGNMENT
            SplitBillStep.IMAGE_CAPTURE -> return // Already at first step
        }

        _uiState.value = _uiState.value.copy(currentStep = previousStep)
    }

    fun exportToTransaction(
        splitBill: SplitBill,
        splitParticipant: SplitParticipant,
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            try {
                val success = splitBillRepository.exportParticipantToTransaction(splitBill, splitParticipant)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = if (success) null else "Failed to export to transaction",
                    exportSuccessMessage = if (success) "Exported to transaction successfully!" else null // Set export success message
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message
                )
            }
        }
    }

    fun clearExportSuccess() {
        _uiState.value = _uiState.value.copy(
            exportSuccessMessage = null // Also clear export success message
        )
    }

    /**
     * Generates a SplitBill from the current UI state.
     */
    private fun generateSplitBillFromState(): SplitBill {
        val state = _uiState.value
        return SplitBill(
            title = title,
            merchant = merchant,
            date = date,
            tax = tax,
            serviceFee = serviceFee,
            totalAmount = state.items.sumOf { it.price },
            imagePath = null,
            items = state.items,
            participants = state.participants
        )
    }

    /**
     * Call this to export the current split bill for a participant.
     */
    fun exportCurrentSplitBillToTransaction(participant: SplitParticipant) {
        val splitBill = generateSplitBillFromState()
        exportToTransaction(splitBill, participant)
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    fun startImageCapture() {
        _uiState.value = _uiState.value.copy(
            currentStep = SplitBillStep.IMAGE_CAPTURE,
            isLoading = false,
            errorMessage = null
        )
    }

    fun onImageCaptured(imageUri: String) {
        if (FeatureFlag.isOcrSplitBillEnabled()) {
            _uiState.value = _uiState.value.copy(
                currentStep = SplitBillStep.OCR_PROCESSING,
                isLoading = true
            )
            // Start OCR processing
            processImageWithOCR(imageUri)
        } else {
            // Skip OCR processing, go directly to manual review
            _uiState.value = _uiState.value.copy(
                currentStep = SplitBillStep.OCR_REVIEW,
                isLoading = false
            )
        }
    }

    private fun processImageWithOCR(imageUri: String) {
        if (!FeatureFlag.isOcrSplitBillEnabled()) {
            // OCR disabled, skip processing
            _uiState.value = _uiState.value.copy(
                currentStep = SplitBillStep.OCR_REVIEW,
                isLoading = false
            )
            return
        }

        viewModelScope.launch {
            try {
                // Use real ML Kit OCR instead of mock data
                val ocrResult = ocrManager.extractTextFromImage(imageUri)

                processOcrResult(ocrResult)
                _uiState.value = _uiState.value.copy(
                    currentStep = SplitBillStep.OCR_REVIEW,
                    isLoading = false
                )

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "OCR processing failed: ${e.message}"
                )
            }
        }
    }

    fun skipImageCapture() {
        // Allow manual entry without OCR
        _uiState.value = _uiState.value.copy(
            currentStep = SplitBillStep.OCR_REVIEW,
            isLoading = false
        )
    }

    /**
     * Saves the split bill to the database
     */
    fun saveSplitBill() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            try {
                // Calculate total amount
                val totalAmount = _uiState.value.items.sumOf { item ->
                    val totalQuantity = item.assignedQuantities.values.sum()
                    item.price * totalQuantity
                } * (1 + (tax / 100.0) + (serviceFee / 100.0))

                // Create split bill entity
                val splitBill = SplitBill(
                    title = title,
                    merchant = merchant,
                    date = date,
                    tax = tax,
                    serviceFee = serviceFee,
                    totalAmount = totalAmount,
                    imagePath = null,
                    createdAt = System.currentTimeMillis(),
                    updatedAt = System.currentTimeMillis()
                )

                // Save split bill and get the ID
                val splitBillId = splitBillRepository.addSplitBill(splitBill)

                // Save items
                splitBillRepository.addSplitItems(splitBillId, _uiState.value.items)

                // Save participants with calculated amounts
                splitBillRepository.addSplitParticipants(splitBillId, _uiState.value.calculatedParticipants)

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isSaveSuccessful = true,
                    savedSplitBillId = splitBillId,
                    saveSuccessMessage = "Split bill saved successfully"
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Failed to save split bill: ${e.message}"
                )
            }
        }
    }

    /**
     * Reset save success state
     */
    fun resetSaveSuccess() {
        _uiState.value = _uiState.value.copy(
            isSaveSuccessful = false,
            savedSplitBillId = null,
            saveSuccessMessage = null
        )
    }

    /**
     * Clear success message (for backward compatibility)
     */
    fun clearSuccessMessage() {
        _uiState.value = _uiState.value.copy(
            saveSuccessMessage = null,
            exportSuccessMessage = null
        )
    }
}
