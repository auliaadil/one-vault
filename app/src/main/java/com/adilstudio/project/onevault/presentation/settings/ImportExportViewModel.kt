package com.adilstudio.project.onevault.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adilstudio.project.onevault.domain.usecase.ExportVaultUseCase
import com.adilstudio.project.onevault.domain.usecase.ImportVaultUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File

class ImportExportViewModel(
    private val exportVaultUseCase: ExportVaultUseCase,
    private val importVaultUseCase: ImportVaultUseCase
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage.asStateFlow()

    private val _exportProgress = MutableStateFlow<String?>(null)
    val exportProgress: StateFlow<String?> = _exportProgress.asStateFlow()

    private val _importProgress = MutableStateFlow<String?>(null)
    val importProgress: StateFlow<String?> = _importProgress.asStateFlow()

    fun exportVault(outputFile: File) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            _successMessage.value = null
            _exportProgress.value = "Preparing backup..."

            try {
                _exportProgress.value = "Collecting vault data..."
                exportVaultUseCase(outputFile).fold(
                    onSuccess = {
                        _exportProgress.value = "Backup completed successfully"
                        _successMessage.value = "Vault data exported successfully to ${outputFile.name}"
                    },
                    onFailure = { error ->
                        _errorMessage.value = "Export failed: ${error.message}"
                        _exportProgress.value = null
                    }
                )
            } catch (e: Exception) {
                _errorMessage.value = "Export failed: ${e.message}"
                _exportProgress.value = null
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun importVault(inputFile: File) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            _successMessage.value = null
            _importProgress.value = "Validating backup file..."

            try {
                _importProgress.value = "Loading backup data..."
                importVaultUseCase(inputFile).fold(
                    onSuccess = {
                        _importProgress.value = "Import completed successfully"
                        _successMessage.value = "Vault data imported successfully from ${inputFile.name}"
                    },
                    onFailure = { error ->
                        _errorMessage.value = "Import failed: ${error.message}"
                        _importProgress.value = null
                    }
                )
            } catch (e: Exception) {
                _errorMessage.value = "Import failed: ${e.message}"
                _importProgress.value = null
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearErrorMessage() {
        _errorMessage.value = null
    }

    fun clearSuccessMessage() {
        _successMessage.value = null
    }

    fun clearProgress() {
        _exportProgress.value = null
        _importProgress.value = null
    }
}
