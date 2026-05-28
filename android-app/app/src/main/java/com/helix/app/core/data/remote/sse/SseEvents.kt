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
}

data class SourceDto(
    val id: Int,
    val title: String,
    val url: String,
    val snippet: String
)
