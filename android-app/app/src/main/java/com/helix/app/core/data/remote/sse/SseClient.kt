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

        val request = Request.Builder()
            .url(url)
            .header("Authorization", "Bearer $token")
            .header("Accept", "text/event-stream")
            .build()

        val listener = object : EventSourceListener() {
            override fun onEvent(eventSource: EventSource, id: String?, type: String?, data: String) {
                when (type) {
                    "message_start" -> trySend(SseStreamEvent.MessageStart(0))
                    "message_delta" -> {
                        val deltaData = gson.fromJson(data, Map::class.java)
                        trySend(SseStreamEvent.MessageDelta(deltaData["delta"] as String, deltaData["text"] as String))
                    }
                    "search_start" -> trySend(SseStreamEvent.SearchStart(data))
                    "search_sources" -> {
                        val listType = object : TypeToken<List<SourceDto>>() {}.type
                        val sources = gson.fromJson<List<SourceDto>>(data, listType)
                        trySend(SseStreamEvent.SearchSources(sources))
                    }
                    "citation" -> {
                        val listType = object : TypeToken<List<Int>>() {}.type
                        val ids = gson.fromJson<List<Int>>(data, listType)
                        trySend(SseStreamEvent.Citation(ids))
                    }
                    "followup_questions" -> {
                        val listType = object : TypeToken<List<String>>() {}.type
                        val qs = gson.fromJson<List<String>>(data, listType)
                        trySend(SseStreamEvent.FollowUpQuestions(qs))
                    }
                    "search_done", "message_done" -> {
                        val doneData = gson.fromJson(data, Map::class.java)
                        trySend(SseStreamEvent.SearchDone(doneData["text"] as String))
                    }
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
