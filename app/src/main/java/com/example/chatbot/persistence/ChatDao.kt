package com.example.chatbot.persistence

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.chatbot.model.Chat
import com.example.chatbot.util.GenericApiResponse

@Dao
interface ChatDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(chat: Chat): Long

//    @Query("UPDATE chat_table SET status = "sent" WHERE status = "not sent")
//    fun updateChatStatus(status: String): Int

    @Query("SELECT * FROM chat_table WHERE chat_window_number = :pk")
    suspend fun searchByWindowNum(pk: Int): LiveData<List<Chat>>?

}