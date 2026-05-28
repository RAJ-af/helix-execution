package com.helix.app.core.data.remote.sse

sealed class SseStreamEvent {
    data class MessageStart(val conversationId: Int) : SseStreamEvent()
    data class MessageDelta(val delta: String, val fullText: String) : SseStreamEvent()
    data class MessageDone(val fullText: String) : SseStreamEvent()
    data class Error(val message: String) : SseStreamEvent()
    object KeepAlive : SseStreamEvent()

    // Search Specific
    data class SearchStart(val query: String) : SseStreamEvent()
    data class SearchSources(val sources: List<SourceDto>) : SseStreamEvent()
    data class Citation(val citationIds: List<Int>) : SseStreamEvent()
    data class FollowUpQuestions(val questions: List<String>) : SseStreamEvent()
    data class SearchDone(val fullText: String) : SseStreamEvent()

    // Tool Specific
    data class ToolStart(val tool: String, val input: String) : SseStreamEvent()
    data class ToolOutput(val output: String) : SseStreamEvent()
    data class ToolDone(val output: String) : SseStreamEvent()

    // Agent/Task Specific
    data class TaskCreated(val title: String, val conversationId: Int) : SseStreamEvent()
    data class TaskUpdated(val status: String, val steps: List<String>) : SseStreamEvent()
    data class TaskStepStarted(val index: Int, val title: String) : SseStreamEvent()
    data class TaskStepCompleted(val index: Int, val result: String) : SseStreamEvent()
    data class ClarificationRequired(val question: String, val options: List<String>) : SseStreamEvent()
    data class ClarificationReceived(val selection: String) : SseStreamEvent()
    data class TaskCompleted(val result: String) : SseStreamEvent()
}

data class SourceDto(val id: Int, val title: String, val url: String, val snippet: String)
