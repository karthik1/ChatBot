package com.example.chatbot.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Entity(tableName = "chat_table")
data class Chat(

    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "chat_id")
    val chatId: Int? = null,

    @ColumnInfo(name = "text_message")
    val senderOrBotText: String? = null,

    @ColumnInfo(name = "chat_window_number")
    var chatWindowNum: Int,

    @ColumnInfo(name = "created_time")
    var timeStamp: String,

    @ColumnInfo(name = "text_status")
    var status: String,
)
