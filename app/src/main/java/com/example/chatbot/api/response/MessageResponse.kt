package com.example.chatbot.api.response

import com.google.gson.JsonObject
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class MessageResponse(

    @SerializedName("success")
    @Expose
    var success: String? = null,

    @SerializedName("errorMessage")
    @Expose
    var errorMessage: String? = null,

    @SerializedName("message")
    @Expose
    var message: JsonObject? = null,


    @SerializedName("errorType")
    @Expose
    var errorType: String? = null,
) {
    override fun toString(): String {
            return "Responseee (response='$success', errorMessage='$errorMessage', token='$message')"
    }
}