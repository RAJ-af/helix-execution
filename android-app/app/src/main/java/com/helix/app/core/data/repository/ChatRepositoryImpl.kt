package com.helix.app.core.data.repository

import com.helix.app.core.common.Resource
import com.helix.app.core.data.local.db.dao.ConversationDao
import com.helix.app.core.data.local.db.dao.MessageDao
import com.helix.app.core.data.local.db.entities.ConversationEntity
import com.helix.app.core.data.local.db.entities.MessageEntity
import com.helix.app.core.data.remote.ChatService
import com.helix.app.core.data.remote.dto.ConversationRequest
import com.helix.app.core.data.remote.dto.MessageRequest
import com.helix.app.core.domain.repository.ChatRepository
import kotlinx.coroutines.flow.*
import javax.inject.Inject

class ChatRepositoryImpl @Inject constructor(
    private val chatService: ChatService,
    private val conversationDao: ConversationDao,
    private val messageDao: MessageDao
) : ChatRepository {

    override fun getConversations(): Flow<Resource<List<ConversationEntity>>> = flow {
        emit(Resource.Loading())

        // Emitting local data first
        val localConversations = conversationDao.getConversations().first()
        emit(Resource.Success(localConversations))

        try {
            val response = chatService.getConversations()
            if (response.isSuccessful) {
                val remoteConversations = response.body()?.map {
                    ConversationEntity(it.id, it.title, it.user_id, it.is_deleted, it.created_at, it.updated_at)
                } ?: emptyList()

                conversationDao.insertConversations(remoteConversations)
                emit(Resource.Success(remoteConversations))
            } else {
                emit(Resource.Error(response.message(), localConversations))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Network error", localConversations))
        }
    }

    override fun getMessages(conversationId: Int): Flow<Resource<List<MessageEntity>>> = flow {
        emit(Resource.Loading())

        val localMessages = messageDao.getMessages(conversationId).first()
        emit(Resource.Success(localMessages))

        try {
            val response = chatService.getMessages(conversationId)
            if (response.isSuccessful) {
                val remoteMessages = response.body()?.map {
                    MessageEntity(it.id, it.conversation_id, it.content, it.role, it.created_at)
                } ?: emptyList()

                messageDao.insertMessages(remoteMessages)
                emit(Resource.Success(remoteMessages))
            } else {
                emit(Resource.Error(response.message(), localMessages))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Network error", localMessages))
        }
    }

    override suspend fun createConversation(title: String): Resource<ConversationEntity> {
        return try {
            val response = chatService.createConversation(ConversationRequest(title))
            if (response.isSuccessful) {
                val it = response.body()!!
                val entity = ConversationEntity(it.id, it.title, it.user_id, it.is_deleted, it.created_at, it.updated_at)
                conversationDao.insertConversations(listOf(entity))
                Resource.Success(entity)
            } else {
                Resource.Error(response.message())
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Network error")
        }
    }

    override suspend fun sendMessage(conversationId: Int, content: String): Resource<List<MessageEntity>> {
        return try {
            val response = chatService.sendMessage(conversationId, MessageRequest(content))
            if (response.isSuccessful) {
                val remoteMessages = response.body()!!.map {
                    MessageEntity(it.id, it.conversation_id, it.content, it.role, it.created_at)
                }
                messageDao.insertMessages(remoteMessages)
                Resource.Success(remoteMessages)
            } else {
                Resource.Error(response.message())
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Network error")
        }
    }

    override suspend fun deleteConversation(conversationId: Int): Resource<Unit> {
        return try {
            val response = chatService.deleteConversation(conversationId)
            if (response.isSuccessful) {
                conversationDao.deleteConversation(conversationId)
                messageDao.deleteMessagesForConversation(conversationId)
                Resource.Success(Unit)
            } else {
                Resource.Error(response.message())
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Network error")
        }
    }
}
