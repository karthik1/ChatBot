package com.example.chatbot.ui.state

sealed class ChatStateEvent {

    class LoadChatWindow:ChatStateEvent()

    class GetResponseEvent(val userId:String) :
        ChatStateEvent()

    class None : ChatStateEvent()
}

