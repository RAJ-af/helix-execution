package com.helix.app.features.chat.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.helix.app.core.common.BaseViewModel
import com.helix.app.core.common.Resource
import com.helix.app.core.data.local.db.entities.MessageEntity
import com.helix.app.core.data.remote.sse.SseStreamEvent
import com.helix.app.core.data.remote.sse.SourceDto
import com.helix.app.core.domain.repository.ChatRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
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
        if (conversationId != -1) getMessages()
    }

    fun getMessages() {
        repository.getMessages(conversationId).onEach { result ->
            when (result) {
                is Resource.Success -> _uiState.value = uiState.value.copy(messages = result.data ?: emptyList(), isLoading = false)
                is Resource.Error -> {
                    _uiState.value = uiState.value.copy(messages = result.data ?: emptyList(), isLoading = false)
                    _eventFlow.emit(ChatEvent.Error(result.message ?: "Error"))
                }
                is Resource.Loading -> _uiState.value = uiState.value.copy(isLoading = true)
            }
        }.launchIn(viewModelScope)
    }

    fun sendAndStream(content: String) {
        if (content.isBlank()) return
        viewModelScope.launch {
            _uiState.value = uiState.value.copy(isSending = true, sources = emptyList(), followUps = emptyList())
            val sendResult = repository.sendMessage(conversationId, content)
            if (sendResult is Resource.Error) {
                _uiState.value = uiState.value.copy(isSending = false)
                _eventFlow.emit(ChatEvent.Error(sendResult.message ?: "Failed"))
                return@launch
            }

            repository.streamChat(conversationId).onEach { event ->
                when (event) {
                    is SseStreamEvent.SearchStart -> _uiState.value = uiState.value.copy(isSearching = true)
                    is SseStreamEvent.SearchSources -> _uiState.value = uiState.value.copy(sources = event.sources, isSearching = False)
                    is SseStreamEvent.MessageStart -> _uiState.value = uiState.value.copy(isStreaming = true, streamingText = "")
                    is SseStreamEvent.MessageDelta -> _uiState.value = uiState.value.copy(streamingText = event.fullText)
                    is SseStreamEvent.FollowUpQuestions -> _uiState.value = uiState.value.copy(followUps = event.questions)
                    is SseStreamEvent.SearchDone -> {
                        _uiState.value = uiState.value.copy(isStreaming = false, streamingText = "", isSending = false)
                        getMessages()
                    }
                    is SseStreamEvent.Error -> {
                        _uiState.value = uiState.value.copy(isStreaming = false, isSending = false)
                        _eventFlow.emit(ChatEvent.Error(event.message))
                    }
                    else -> {}
                }
            }.launchIn(viewModelScope)
        }
    }
}

data class ChatState(
    val messages: List<MessageEntity> = emptyList(),
    val streamingText: String = "",
    val sources: List<SourceDto> = emptyList(),
    val followUps: List<String> = emptyList(),
    val isLoading: Boolean = false,
    val isSending: Boolean = false,
    val isSearching: Boolean = false,
    val isStreaming: Boolean = false
)

sealed class ChatEvent {
    data class Error(val message: String) : ChatEvent()
}
