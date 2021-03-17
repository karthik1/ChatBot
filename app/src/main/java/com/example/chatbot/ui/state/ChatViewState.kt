package com.example.chatbot.ui.state

import com.example.chatbot.model.Chat

data class ChatViewState (
    var blogposts:List<Chat> ? = null,
    var text : String ? = null
)