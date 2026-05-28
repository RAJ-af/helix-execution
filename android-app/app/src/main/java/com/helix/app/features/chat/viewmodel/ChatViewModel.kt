package com.helix.app.features.chat.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.helix.app.core.common.BaseViewModel
import com.helix.app.core.common.Resource
import com.helix.app.core.data.local.db.entities.MessageEntity
import com.helix.app.core.domain.repository.ChatRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val repository: ChatRepository,
    savedStateHandle: SavedStateHandle
) : BaseViewModel<ChatState>(ChatState()) {

    private val conversationId: Int = savedStateHandle.get<String>("conversationId")?.toIntOrNull() ?: -1

    private val _eventFlow = MutableSharedFlow<ChatEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    init {
        if (conversationId != -1) {
            getMessages()
        }
    }

    fun getMessages() {
        repository.getMessages(conversationId).onEach { result ->
            when (result) {
                is Resource.Success -> {
                    _uiState.value = uiState.value.copy(
                        messages = result.data ?: emptyList(),
                        isLoading = false
                    )
                }
                is Resource.Error -> {
                    _uiState.value = uiState.value.copy(
                        messages = result.data ?: emptyList(),
                        isLoading = false
                    )
                    _eventFlow.emit(ChatEvent.Error(result.message ?: "Unknown error"))
                }
                is Resource.Loading -> {
                    _uiState.value = uiState.value.copy(isLoading = true)
                }
            }
        }.launchIn(viewModelScope)
    }

    fun sendMessage(content: String) {
        if (content.isBlank()) return

        viewModelScope.launch {
            _uiState.value = uiState.value.copy(isSending = true)
            val result = repository.sendMessage(conversationId, content)
            _uiState.value = uiState.value.copy(isSending = false)

            if (result is Resource.Success) {
                getMessages()
            } else {
                _eventFlow.emit(ChatEvent.Error(result.message ?: "Failed to send message"))
            }
        }
    }
}

data class ChatState(
    val messages: List<MessageEntity> = emptyList(),
    val isLoading: Boolean = false,
    val isSending: Boolean = false
)

sealed class ChatEvent {
    data class Error(val message: String) : ChatEvent()
}
