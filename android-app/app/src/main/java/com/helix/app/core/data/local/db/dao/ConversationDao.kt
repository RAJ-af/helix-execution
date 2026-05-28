package com.helix.app.core.data.local.db.dao

import androidx.room.*
import com.helix.app.core.data.local.db.entities.ConversationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ConversationDao {
    @Query("SELECT * FROM conversations WHERE isDeleted = 0 ORDER BY updatedAt DESC")
    fun getConversations(): Flow<List<ConversationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConversations(conversations: List<ConversationEntity>)

    @Query("DELETE FROM conversations WHERE id = :id")
    suspend fun deleteConversation(id: Int)
}
