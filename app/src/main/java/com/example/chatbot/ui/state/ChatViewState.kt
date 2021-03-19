package com.example.chatbot.ui.state

import com.example.chatbot.model.Chat

data class ChatViewState (
    var chatList:List<Chat> ? = null,
    var chat : Chat ? = null
)