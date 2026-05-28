package com.helix.app.core.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.helix.app.core.data.local.db.dao.ConversationDao
import com.helix.app.core.data.local.db.dao.MessageDao
import com.helix.app.core.data.local.db.entities.ConversationEntity
import com.helix.app.core.data.local.db.entities.MessageEntity

@Database(
    entities = [ConversationEntity::class, MessageEntity::class],
    version = 2,
    exportSchema = false
)
abstract class HelixDatabase : RoomDatabase() {
    abstract fun conversationDao(): ConversationDao
    abstract fun messageDao(): MessageDao
}
