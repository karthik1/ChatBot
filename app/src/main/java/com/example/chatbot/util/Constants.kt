package com.example.chatbot.util

object Constants {

    const val BASE_URL = "https://www.personalityforge.com/"

    const val NETWORK_TIMEOUT = 12000L
    const val TESTING_NETWORK_DELAY = 0L // fake network delay for testing
    const val TESTING_CACHE_DELAY = 0L // fake cache delay for testing

    const val UNABLE_TO_RESOLVE_HOST = "Unable to resolve host"
    const val UNABLE_TODO_OPERATION_WO_INTERNET =
        "Can't do that operation without an internet connection"

    const val ERROR_CHECK_NETWORK_CONNECTION = "Check network connection."
    const val ERROR_UNKNOWN = "Unknown error"

    const val API_KEY = "6nt5d1nJHkqbkphe"
    const val BOT_ID = "63906"
    const val EXT_ID = "chirag1"

    const val CURRENT_WINDOW: String = "current_window";
    const val WINDOW_MAX: String = "window_max";

    const val CHAT_WINDOW: String = "Chat Window ";
    const val ADD_CHAT_WINDOW: String = "Add New Window";
    const val DEFAULT_CHAT_WINDOW_NUM: String = "1";

    fun isNetworkError(msg: String): Boolean {
        when {
            msg.contains(UNABLE_TO_RESOLVE_HOST) -> return true
            else -> return false
        }
    }


}