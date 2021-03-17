package com.example.chatbot.repository

import com.example.chatbot.api.ChatApiService
import com.example.chatbot.persistence.ChatDao
import javax.inject.Inject


class ChatRepository @Inject constructor(
    chatDao: ChatDao,
    chatApiService: ChatApiService,

    ) {

}