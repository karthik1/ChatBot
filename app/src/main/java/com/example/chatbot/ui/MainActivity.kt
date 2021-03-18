package com.example.chatbot.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.example.chatbot.R
import com.example.chatbot.repository.ChatRepository
import com.example.chatbot.util.DataState
import com.example.chatbot.util.DisplayType
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), DataStateListener {

    @Inject
    lateinit var repository: ChatRepository

    lateinit var viewModel: ChatViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

//        repository.getResponse()
        viewModel = ViewModelProvider(this).get(ChatViewModel::class.java)
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
                event.getContentIfNotHandled()?.let { stateError ->
                    stateError.response?.let { display ->

                        when (display.responseType) {

                            is DisplayType.Dialog -> {

                            }
                            is DisplayType.Toast -> {

                            }
                        }


                    }
                }
            }

        }
    }
}

fun showProgressBar(isVisible: Boolean) {
    if (isVisible) {
//        progress_bar.visibility = View.VISIBLE
    } else {
//        progress_bar.visibility = View.INVISIBLE
    }

}