package com.example.chatbot.ui.state

sealed class ChatStateEvent {

    //To change chat window
    class SwitchChatWindowEvent(val chatWindowNum:Int?):ChatStateEvent()

    //To load already typed message before Changing the window
    class GetResponseEvent(val message:String?,val status:String?,val chatWindowNum:Int?) :
        ChatStateEvent()

    // TODO --> Add third stateEvent for creating new window
    //  in the menu and showing it in fragment
    class AddNewWindowEvent() :
        ChatStateEvent()

    class None : ChatStateEvent()
}

