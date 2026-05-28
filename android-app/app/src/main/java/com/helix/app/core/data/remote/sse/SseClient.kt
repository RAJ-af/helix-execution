package com.helix.app.core.data.remote.sse

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.helix.app.core.data.local.prefs.TokenManager
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.sse.EventSource
import okhttp3.sse.EventSourceListener
import okhttp3.sse.EventSources
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SseClient @Inject constructor(
    private val okHttpClient: OkHttpClient,
    private val tokenManager: TokenManager,
    private val gson: Gson
) {
    fun stream(url: String): Flow<SseStreamEvent> = callbackFlow {
        val token = runBlocking { tokenManager.accessToken.first() }
        val request = Request.Builder().url(url).header("Authorization", "Bearer $token").header("Accept", "text/event-stream").build()

        val listener = object : EventSourceListener() {
            override fun onEvent(eventSource: EventSource, id: String?, type: String?, data: String) {
                val mapType = object : TypeToken<Map<String, Any>>() {}.type
                val dataMap: Map<String, Any> = try { gson.fromJson(data, mapType) } catch (e: Exception) { emptyMap() }

                when (type) {
                    "message_start" -> trySend(SseStreamEvent.MessageStart(0))
                    "message_delta" -> trySend(SseStreamEvent.MessageDelta(dataMap["delta"] as String, dataMap["text"] as String))
                    "search_start" -> trySend(SseStreamEvent.SearchStart(data))
                    "search_sources" -> {
                        val listType = object : TypeToken<List<SourceDto>>() {}.type
                        trySend(SseStreamEvent.SearchSources(gson.fromJson(data, listType)))
                    }
                    "tool_start" -> trySend(SseStreamEvent.ToolStart(dataMap["tool"] as String, dataMap["input"] as String))
                    "tool_output" -> trySend(SseStreamEvent.ToolOutput(dataMap["output"] as String))
                    "tool_done" -> trySend(SseStreamEvent.ToolDone(dataMap["output"] as String))
                    "task_created" -> trySend(SseStreamEvent.TaskCreated(dataMap["title"] as String, (dataMap["conversation_id"] as Double).toInt()))
                    "task_updated" -> {
                        val steps = (dataMap["steps"] as? List<String>) ?: emptyList()
                        trySend(SseStreamEvent.TaskUpdated(dataMap["status"] as String, steps))
                    }
                    "task_step_started" -> trySend(SseStreamEvent.TaskStepStarted((dataMap["index"] as Double).toInt(), dataMap["title"] as String))
                    "task_step_completed" -> trySend(SseStreamEvent.TaskStepCompleted((dataMap["index"] as Double).toInt(), dataMap["result"] as String))
                    "clarification_required" -> {
                        val opts = (dataMap["options"] as? List<String>) ?: emptyList()
                        trySend(SseStreamEvent.ClarificationRequired(dataMap["question"] as String, opts))
                    }
                    "clarification_received" -> trySend(SseStreamEvent.ClarificationReceived(dataMap["selection"] as String))
                    "task_completed" -> trySend(SseStreamEvent.TaskCompleted(dataMap["result"] as String))
                    "citation" -> {
                        val listType = object : TypeToken<List<Int>>() {}.type
                        trySend(SseStreamEvent.Citation(gson.fromJson(data, listType)))
                    }
                    "followup_questions" -> {
                        val listType = object : TypeToken<List<String>>() {}.type
                        trySend(SseStreamEvent.FollowUpQuestions(gson.fromJson(data, listType)))
                    }
                    "search_done" -> trySend(SseStreamEvent.SearchDone(dataMap["text"] as String))
                    "error" -> trySend(SseStreamEvent.Error(data))
                }
            }
            override fun onFailure(eventSource: EventSource, t: Throwable?, response: Response?) {
                trySend(SseStreamEvent.Error(t?.message ?: "SSE Failure"))
                close(t)
            }
            override fun onClosed(eventSource: EventSource) { close() }
        }
        val eventSource = EventSources.createFactory(okHttpClient).newEventSource(request, listener)
        awaitClose { eventSource.cancel() }
    }
}
