package com.example.chatbot.session

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

class SessionManager @Inject constructor(
     val application:Application
) {

    fun isConnectedToTheInternet(): Boolean{
        val cm = application.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        try{
            return cm.activeNetworkInfo!!.isConnected
        }catch (e: Exception){
            Log.e("SESSION MANAGER", "isConnectedToTheInternet: ${e.message}")
        }
        return false
    }
}