package com.example.chatbot.api

import androidx.lifecycle.LiveData
import com.example.chatbot.api.response.MessageResponse
import com.example.chatbot.model.Chat
import com.example.chatbot.util.GenericApiResponse
import retrofit2.Call
import retrofit2.http.*

interface ChatApiService {


//    apiKey=6nt5d1nJHkqbkphe&message=Hi&chatBotID =63906&externalID=chirag1


    @GET("api/chat")
    fun getBotResponse(
        @Query("apiKey") key:String,
        @Query("message") message:String,
        @Query("chatBotID") botId:String,
        @Query("externalID") extId:String,
    ):LiveData<GenericApiResponse<MessageResponse>>

    @GET("api/chat")
    fun getBotResponsee(
        @Query("apiKey") key:String,
        @Query("message") message:String,
        @Query("chatBotID") botId:String,
        @Query("externalID") extId:String,
    ):Call<MessageResponse>

}