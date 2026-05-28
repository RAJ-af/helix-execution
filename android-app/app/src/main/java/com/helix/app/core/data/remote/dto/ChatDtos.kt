package com.helix.app.core.data.remote.dto

data class ConversationRequest(
    val title: String = "New Conversation"
)

data class ConversationResponse(
    val id: Int,
    val title: String,
    val user_id: Int,
    val is_deleted: Boolean,
    val created_at: String,
    val updated_at: String
)

data class MessageRequest(
    val content: String,
    val role: String = "user"
)

data class MessageResponse(
    val id: Int,
    val conversation_id: Int,
    val content: String,
    val role: String,
    val created_at: String
)

data class ConversationWithMessagesResponse(
    val id: Int,
    val title: String,
    val user_id: Int,
    val is_deleted: Boolean,
    val created_at: String,
    val updated_at: String,
    val messages: List<MessageResponse>
)
