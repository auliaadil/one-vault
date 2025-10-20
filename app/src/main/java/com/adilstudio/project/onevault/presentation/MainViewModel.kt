package com.adilstudio.project.onevault.presentation

import androidx.compose.foundation.layout.RowScope
import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    private val _title = MutableStateFlow("Home")
    val title: StateFlow<String> = _title.asStateFlow()

    private val _showNavigationIcon = MutableStateFlow(false)
    val showNavigationIcon: StateFlow<Boolean> = _showNavigationIcon.asStateFlow()

    private val _actions = MutableStateFlow<@Composable RowScope.() -> Unit>({})
    val actions: StateFlow<@Composable RowScope.() -> Unit> = _actions.asStateFlow()

    private val _snackbarMessage = MutableSharedFlow<String>(extraBufferCapacity = 1)
    val snackbarMessage: SharedFlow<String> = _snackbarMessage

    fun updateTopBar(
        title: String,
        showNavigationIcon: Boolean, actions: @Composable RowScope.() -> Unit = {}
    ) {
        _title.value = title
        _showNavigationIcon.value = showNavigationIcon
        _actions.value = actions
    }

    fun showSnackbar(message: String) {
        // Use viewModelScope for safe emission
        viewModelScope.launch {
            _snackbarMessage.emit(message)
        }
    }
}