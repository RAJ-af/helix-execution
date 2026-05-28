package com.helix.app.core.data.local.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "messages")
data class MessageEntity(
    @PrimaryKey val id: Int,
    val conversationId: Int,
    val content: String,
    val role: String,
    val createdAt: String
)
