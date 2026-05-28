package com.helix.app.core.data.local.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "conversations")
data class ConversationEntity(
    @PrimaryKey val id: Int,
    val title: String,
    val userId: Int,
    val isDeleted: Boolean,
    val createdAt: String,
    val updatedAt: String
)
