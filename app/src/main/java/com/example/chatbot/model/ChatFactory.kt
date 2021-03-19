package com.example.chatbot.model

import com.example.chatbot.util.DateUtil
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatFactory
@Inject
constructor(
    private val dateUtil: DateUtil
) {

   public fun createChatItem(
        text: String,
        chatWindowNum: Int?,
        status:String
    ): Chat {
        return Chat(
            chatId = UUID.randomUUID().toString(),
            senderOrBotText = text,
            chatWindowNum = chatWindowNum,
            status = status,
            timeStamp = dateUtil . getCurrentTimestamp (),
        )
    }

}