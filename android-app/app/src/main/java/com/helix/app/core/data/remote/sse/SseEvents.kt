package com.helix.app.core.data.remote.sse

sealed class SseStreamEvent {
    data class MessageStart(val conversationId: Int) : SseStreamEvent()
    data class MessageDelta(val delta: String, val fullText: String) : SseStreamEvent()
    data class MessageDone(val fullText: String) : SseStreamEvent()
    data class Error(val message: String) : SseStreamEvent()
    object KeepAlive : SseStreamEvent()
}
