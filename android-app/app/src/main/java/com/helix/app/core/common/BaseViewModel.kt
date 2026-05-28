package com.helix.app.core.common

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

abstract class BaseViewModel<T>(initialState: T) : ViewModel() {
    protected val _uiState = MutableStateFlow(initialState)
    val uiState = _uiState.asStateFlow()
}
