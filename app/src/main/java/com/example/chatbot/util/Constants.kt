package com.example.chatbot.util

object Constants {

    const val BASE_URL = "https://www.personalityforge.com/"

    const val NETWORK_TIMEOUT = 3000L
    const val TESTING_NETWORK_DELAY = 0L // fake network delay for testing
    const val TESTING_CACHE_DELAY = 0L // fake cache delay for testing

    const val UNABLE_TO_RESOLVE_HOST = "Unable to resolve host"
    const val UNABLE_TODO_OPERATION_WO_INTERNET = "Can't do that operation without an internet connection"

    const val ERROR_CHECK_NETWORK_CONNECTION = "Check network connection."
    const val ERROR_UNKNOWN = "Unknown error"

    fun isNetworkError(msg: String): Boolean{
        when{
            msg.contains(UNABLE_TO_RESOLVE_HOST) -> return true
            else-> return false
        }
    }
}