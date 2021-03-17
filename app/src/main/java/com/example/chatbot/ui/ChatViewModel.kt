package com.example.chatbot.ui

import androidx.lifecycle.ViewModel
import com.example.chatbot.repository.ChatRepository
import dagger.hilt.android.scopes.ActivityRetainedScoped
import javax.inject.Inject

@ActivityRetainedScoped
class ChatViewModel @Inject constructor(
    val chatRepository: ChatRepository
):ViewModel() {

}