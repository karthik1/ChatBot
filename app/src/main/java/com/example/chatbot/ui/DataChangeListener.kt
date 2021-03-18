package com.example.chatbot.ui

import com.example.chatbot.util.DataState

interface DataStateListener {
    fun onDataStateChange(dataState: DataState<*>?)
}