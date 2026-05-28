package com.helix.app.features.auth.viewmodel

import androidx.lifecycle.viewModelScope
import com.helix.app.core.common.BaseViewModel
import com.helix.app.core.common.Resource
import com.helix.app.core.data.remote.dto.LoginRequest
import com.helix.app.core.data.remote.dto.SignupRequest
import com.helix.app.core.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: AuthRepository
) : BaseViewModel<AuthState>(AuthState()) {

    private val _eventFlow = MutableSharedFlow<AuthEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    fun onLogin(email: String, password: String) {
        viewModelScope.launch {
            repository.login(LoginRequest(email, password)).onEach { result ->
                when (result) {
                    is Resource.Success -> {
                        _uiState.value = uiState.value.copy(isLoading = false)
                        _eventFlow.emit(AuthEvent.AuthSuccess)
                    }
                    is Resource.Error -> {
                        _uiState.value = uiState.value.copy(isLoading = false)
                        _eventFlow.emit(AuthEvent.Error(result.message ?: "Login failed"))
                    }
                    is Resource.Loading -> {
                        _uiState.value = uiState.value.copy(isLoading = true)
                    }
                }
            }.launchIn(this)
        }
    }

    fun onSignup(email: String, password: String, fullName: String) {
        viewModelScope.launch {
            repository.signup(SignupRequest(email, password, fullName)).onEach { result ->
                when (result) {
                    is Resource.Success -> {
                        _uiState.value = uiState.value.copy(isLoading = false)
                        _eventFlow.emit(AuthEvent.SignupSuccess)
                    }
                    is Resource.Error -> {
                        _uiState.value = uiState.value.copy(isLoading = false)
                        _eventFlow.emit(AuthEvent.Error(result.message ?: "Signup failed"))
                    }
                    is Resource.Loading -> {
                        _uiState.value = uiState.value.copy(isLoading = true)
                    }
                }
            }.launchIn(this)
        }
    }
}

data class AuthState(
    val isLoading: Boolean = false
)

sealed class AuthEvent {
    object AuthSuccess : AuthEvent()
    object SignupSuccess : AuthEvent()
    data class Error(val message: String) : AuthEvent()
}

sealed class AuthUiEvent {
    data class EmailChanged(val email: String) : AuthUiEvent()
    data class PasswordChanged(val password: String) : AuthUiEvent()
    object Login : AuthUiEvent()
}
