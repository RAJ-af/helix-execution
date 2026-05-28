package com.helix.app.features.home.viewmodel

import androidx.lifecycle.viewModelScope
import com.helix.app.core.common.BaseViewModel
import com.helix.app.core.common.Resource
import com.helix.app.core.data.local.db.entities.ConversationEntity
import com.helix.app.core.domain.repository.ChatRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: ChatRepository
) : BaseViewModel<HomeState>(HomeState()) {

    private val _eventFlow = MutableSharedFlow<HomeEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    init {
        getConversations()
    }

    fun getConversations() {
        repository.getConversations().onEach { result ->
            when (result) {
                is Resource.Success -> {
                    _uiState.value = uiState.value.copy(
                        conversations = result.data ?: emptyList(),
                        isLoading = false
                    )
                }
                is Resource.Error -> {
                    _uiState.value = uiState.value.copy(
                        conversations = result.data ?: emptyList(),
                        isLoading = false
                    )
                    _eventFlow.emit(HomeEvent.Error(result.message ?: "Unknown error"))
                }
                is Resource.Loading -> {
                    _uiState.value = uiState.value.copy(isLoading = true)
                }
            }
        }.launchIn(viewModelScope)
    }

    fun createConversation(title: String) {
        viewModelScope.launch {
            val result = repository.createConversation(title)
            if (result is Resource.Success) {
                _eventFlow.emit(HomeEvent.ConversationCreated(result.data!!.id))
            } else {
                _eventFlow.emit(HomeEvent.Error(result.message ?: "Failed to create conversation"))
            }
        }
    }

    fun deleteConversation(id: Int) {
        viewModelScope.launch {
            val result = repository.deleteConversation(id)
            if (result is Resource.Success) {
                getConversations()
            } else {
                _eventFlow.emit(HomeEvent.Error(result.message ?: "Failed to delete conversation"))
            }
        }
    }
}

data class HomeState(
    val conversations: List<ConversationEntity> = emptyList(),
    val isLoading: Boolean = false
)

sealed class HomeEvent {
    data class ConversationCreated(val id: Int) : HomeEvent()
    data class Error(val message: String) : HomeEvent()
}
