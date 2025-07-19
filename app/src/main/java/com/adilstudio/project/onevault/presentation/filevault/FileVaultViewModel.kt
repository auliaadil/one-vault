package com.adilstudio.project.onevault.presentation.filevault

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adilstudio.project.onevault.domain.model.VaultFile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FileVaultViewModel : ViewModel() {
    private val _files = MutableStateFlow<List<VaultFile>>(emptyList())
    val files: StateFlow<List<VaultFile>> = _files.asStateFlow()

    fun loadFiles() {
        // TODO: Load files from repository
        _files.value = emptyList() // Stub
    }

    fun addFile(context: Context, uri: Uri) {
        viewModelScope.launch {
            // TODO: Copy file to internal storage and save metadata
        }
    }

    fun importData(context: Context, uri: Uri) {
        viewModelScope.launch {
            // TODO: Read JSON, deserialize, and save to repository
        }
    }

    fun exportData(context: Context, uri: Uri) {
        viewModelScope.launch {
            // TODO: Fetch data, serialize to JSON, and write to uri
        }
    }
}

