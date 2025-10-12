package com.adilstudio.project.onevault.presentation

import androidx.compose.foundation.layout.RowScope
import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class TopBarViewModel : ViewModel() {
    private val _title = MutableStateFlow("Home")
    val title: StateFlow<String> = _title.asStateFlow()

    private val _showNavigationIcon = MutableStateFlow(false)
    val showNavigationIcon: StateFlow<Boolean> = _showNavigationIcon.asStateFlow()

    private val _actions = MutableStateFlow<@Composable RowScope.() -> Unit>({})
    val actions: StateFlow<@Composable RowScope.() -> Unit> = _actions.asStateFlow()

    fun updateTopBar(
        title: String,
        showNavigationIcon: Boolean, actions: @Composable RowScope.() -> Unit = {}
    ) {
        _title.value = title
        _showNavigationIcon.value = showNavigationIcon
        _actions.value = actions
    }
}