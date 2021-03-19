package com.example.chatbot.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.chatbot.R
import com.example.chatbot.repository.ChatRepository
import com.example.chatbot.util.DataState
import com.example.chatbot.util.DisplayType
import com.example.chatbot.util.Event
import com.example.chatbot.util.StateError
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject
import com.example.chatbot.util.displaySuccessDialog
import com.example.chatbot.util.displayToast

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), DataStateListener {

    @Inject
    lateinit var repository: ChatRepository

    @Inject
    lateinit var viewModel: ChatViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

//        repository.getResponse()
        showMainFragment()
    }

    private fun showMainFragment() {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container, ChatFragment(), "MainFragment")
            .commit()
    }

    override fun onDataStateChange(dataState: DataState<*>?) {

        dataState?.let {
            // Handle loading
            showProgressBar(dataState.loading.isLoading)

            // Handle Message
            dataState.error?.let { event ->

                handleStateError(event)


            }
        }
    }

    private fun handleStateError(event: Event<StateError>) {
        event.getContentIfNotHandled()?.let {
            when (it.response.responseType) {
                is DisplayType.Toast -> {
                    it.response.message?.let { message ->
                        displayToast(message)
                        Log.d("TAG", "handleStateError: " + message)
                    }
                }

                is DisplayType.Dialog -> {
                    it.response.message?.let { message ->
                        displaySuccessDialog(message)
                    }
                }

                is DisplayType.None -> {
                    Log.i("TAG", "handleStateError: ${it.response.message}")
                }
            }
        }

    }

    fun showProgressBar(isVisible: Boolean) {

        if(isVisible){
//            progress_bar.visibility = View.VISIBLE
        }
        else{
//            progress_bar.visibility = View.GONE
        }
    }


}

