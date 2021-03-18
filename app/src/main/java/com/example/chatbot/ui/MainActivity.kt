package com.example.chatbot.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.chatbot.R
import com.example.chatbot.repository.ChatRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var repository: ChatRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        repository.getResponse()

    }


}