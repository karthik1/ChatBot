package com.example.chatbot.persistence

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.chatbot.model.Chat

@Database(entities = [Chat::class],version = 1)
abstract class ChatDatabase: RoomDatabase() {

    abstract fun getchatDao():ChatDao

    companion object{
        val DATABASE_NAME: String = "chat_db"
    }

}

