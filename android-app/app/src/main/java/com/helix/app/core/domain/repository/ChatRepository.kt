package com.helix.app.core.domain.repository

import com.helix.app.core.common.Resource
import com.helix.app.core.data.local.db.entities.ConversationEntity
import com.helix.app.core.data.local.db.entities.MessageEntity
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    fun getConversations(): Flow<Resource<List<ConversationEntity>>>
    fun getMessages(conversationId: Int): Flow<Resource<List<MessageEntity>>>
    suspend fun createConversation(title: String): Resource<ConversationEntity>
    suspend fun sendMessage(conversationId: Int, content: String): Resource<List<MessageEntity>>
    suspend fun deleteConversation(conversationId: Int): Resource<Unit>
}
