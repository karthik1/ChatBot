package com.example.chatbot.api.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class MessageResponse(

    @SerializedName("response")
    @Expose
    var success: String,

    @SerializedName("error_message")
    @Expose
    var errorMessage: String,

    @SerializedName("message")
    @Expose
    var message: String,
) {
}