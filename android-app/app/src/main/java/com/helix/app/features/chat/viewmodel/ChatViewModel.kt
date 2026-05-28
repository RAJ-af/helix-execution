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
            _uiState.value = uiState.value.copy(isSending = true, sources = emptyList(), followUps = emptyList(), toolOutput = "", taskSteps = emptyList())
            val sendResult = repository.sendMessage(conversationId, content)
            if (sendResult is Resource.Error) {
                _uiState.value = uiState.value.copy(isSending = false)
                _eventFlow.emit(ChatEvent.Error(sendResult.message ?: "Failed"))
                return@launch
            }

            repository.streamChat(conversationId).onEach { event ->
                when (event) {
                    is SseStreamEvent.SearchStart -> _uiState.value = uiState.value.copy(isSearching = true)
                    is SseStreamEvent.SearchSources -> _uiState.value = uiState.value.copy(sources = event.sources, isSearching = false)
                    is SseStreamEvent.ToolStart -> _uiState.value = uiState.value.copy(isExecutingTool = true, toolName = event.tool, toolInput = event.input)
                    is SseStreamEvent.ToolOutput -> _uiState.value = uiState.value.copy(toolOutput = uiState.value.toolOutput + event.output)
                    is SseStreamEvent.ToolDone -> _uiState.value = uiState.value.copy(isExecutingTool = false)

                    is SseStreamEvent.TaskCreated -> _uiState.value = uiState.value.copy(taskTitle = event.title, isExecutingTask = true)
                    is SseStreamEvent.TaskUpdated -> _uiState.value = uiState.value.copy(taskSteps = event.steps.map { TaskStepUI(it, "pending") })
                    is SseStreamEvent.TaskStepStarted -> {
                        val steps = uiState.value.taskSteps.toMutableList()
                        if (event.index < steps.size) steps[event.index] = steps[event.index].copy(status = "running")
                        _uiState.value = uiState.value.copy(taskSteps = steps)
                    }
                    is SseStreamEvent.TaskStepCompleted -> {
                        val steps = uiState.value.taskSteps.toMutableList()
                        if (event.index < steps.size) steps[event.index] = steps[event.index].copy(status = "completed")
                        _uiState.value = uiState.value.copy(taskSteps = steps)
                    }
                    is SseStreamEvent.ClarificationRequired -> _uiState.value = uiState.value.copy(clarificationQuestion = event.question, clarificationOptions = event.options)
                    is SseStreamEvent.ClarificationReceived -> _uiState.value = uiState.value.copy(clarificationQuestion = null, clarificationOptions = emptyList())
                    is SseStreamEvent.TaskCompleted -> _uiState.value = uiState.value.copy(isExecutingTask = false)

                    is SseStreamEvent.MessageStart -> _uiState.value = uiState.value.copy(isStreaming = true, streamingText = "")
                    is SseStreamEvent.MessageDelta -> _uiState.value = uiState.value.copy(streamingText = event.fullText)
                    is SseStreamEvent.FollowUpQuestions -> _uiState.value = uiState.value.copy(followUps = event.questions)
                    is SseStreamEvent.SearchDone -> {
                        _uiState.value = uiState.value.copy(isStreaming = false, streamingText = "", isSending = false)
                        getMessages()
                    }
                    is SseStreamEvent.Error -> {
                        _uiState.value = uiState.value.copy(isStreaming = false, isSending = false, isExecutingTool = false, isExecutingTask = false)
                        _eventFlow.emit(ChatEvent.Error(event.message))
                    }
                    else -> {}
                }
            }.launchIn(viewModelScope)
        }
    }
}

data class TaskStepUI(val title: String, val status: String)

data class ChatState(
    val messages: List<MessageEntity> = emptyList(),
    val streamingText: String = "",
    val sources: List<SourceDto> = emptyList(),
    val followUps: List<String> = emptyList(),
    val toolName: String = "",
    val toolInput: String = "",
    val toolOutput: String = "",
    val taskTitle: String = "",
    val taskSteps: List<TaskStepUI> = emptyList(),
    val clarificationQuestion: String? = null,
    val clarificationOptions: List<String> = emptyList(),
    val isLoading: Boolean = false,
    val isSending: Boolean = false,
    val isSearching: Boolean = false,
    val isStreaming: Boolean = false,
    val isExecutingTool: Boolean = false,
    val isExecutingTask: Boolean = false
)

sealed class ChatEvent {
    data class Error(val message: String) : ChatEvent()
}
