package com.helix.app.core.data.remote

import com.helix.app.core.data.remote.dto.*
import retrofit2.Response
import retrofit2.http.*

interface ChatService {
    @POST("conversations")
    suspend fun createConversation(@Body request: ConversationRequest): Response<ConversationResponse>

    @GET("conversations")
    suspend fun getConversations(): Response<List<ConversationResponse>>

    @GET("conversations/{id}")
    suspend fun getConversation(@Path("id") id: Int): Response<ConversationWithMessagesResponse>

    @PATCH("conversations/{id}")
    suspend fun renameConversation(@Path("id") id: Int, @Body request: ConversationRequest): Response<ConversationResponse>

    @DELETE("conversations/{id}")
    suspend fun deleteConversation(@Path("id") id: Int): Response<Unit>

    @POST("conversations/{id}/messages")
    suspend fun sendMessage(@Path("id") id: Int, @Body request: MessageRequest): Response<List<MessageResponse>>

    @GET("conversations/{id}/messages")
    suspend fun getMessages(@Path("id") id: Int): Response<List<MessageResponse>>
}
