package com.helix.app.core.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.helix.app.core.data.local.db.entities.MessageEntity

@Database(entities = [MessageEntity::class], version = 1, exportSchema = false)
abstract class HelixDatabase : RoomDatabase() {
    // abstract fun messageDao(): MessageDao
}
