package com.example.chatbot.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.example.chatbot.model.Chat
import com.example.chatbot.repository.ChatRepository
import com.example.chatbot.ui.state.ChatStateEvent
import com.example.chatbot.ui.state.ChatStateEvent.*
import com.example.chatbot.ui.state.ChatViewState
import com.example.chatbot.util.AbsentLiveData
import com.example.chatbot.util.DataState
import dagger.hilt.android.scopes.ActivityRetainedScoped
import javax.inject.Inject

@ActivityRetainedScoped
class ChatViewModel @Inject constructor(
    val chatRepository: ChatRepository
):ViewModel() {


    private val _viewState: MutableLiveData<ChatViewState> = MutableLiveData()
    private val _stateEvent: MutableLiveData<ChatStateEvent> = MutableLiveData()

    val viewState: LiveData<ChatViewState>
        get() = _viewState

    val dataState: LiveData<DataState<ChatViewState>> = Transformations
        .switchMap(_stateEvent) { stateEvent ->
            stateEvent?.let {
                handleStateEvent(stateEvent)
            }
        }

    private fun handleStateEvent(stateEvent: ChatStateEvent): LiveData<DataState<ChatViewState>> {

        when (stateEvent) {

            is SwitchChatWindowEvent -> {
                //Load from db update RecyclerView and show the "Text in edittext if any"
                return chatRepository.loadFromDB(stateEvent.chatWindowNum)
            }

            is GetResponseEvent -> {

                return chatRepository.getBotResponse(stateEvent.message,stateEvent.status,stateEvent.chatWindowNum)
            }

            is AddNewWindowEvent -> {

                //Create a new item in option menu and Load a empty recyclerview
                //add num to windownumlist
                return AbsentLiveData.create()
            }
            is None ->{
                return AbsentLiveData.create()
            }
        }
    }

    fun displayChat(chatList: List<Chat>) {

        val update = getCurrentViewStateOrNew()
        update.chatList = chatList
        update.chat = null;
        _viewState.value = update
    }

    fun loadTypedText(chat: Chat) {
        val update = getCurrentViewStateOrNew()
        update.chat = chat
        update.chatList = null
        _viewState.value = update
    }

    private fun getCurrentViewStateOrNew(): ChatViewState {
        val value = viewState.value?.let {
            it
        } ?: ChatViewState()
        return value
    }

    fun setStateEvent(event: ChatStateEvent) {
        _stateEvent.value = event
    }

}