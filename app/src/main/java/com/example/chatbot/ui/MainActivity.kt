package com.example.chatbot.ui

import android.content.Context
import android.net.ConnectivityManager
import android.net.ConnectivityManager.NetworkCallback
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.chatbot.R
import com.example.chatbot.databinding.ActivityChatBinding
import com.example.chatbot.repository.ChatRepository
import com.example.chatbot.util.*
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : AppCompatActivity(), DataStateListener {

    @Inject
    lateinit var repository: ChatRepository

    @Inject
    lateinit var viewModel: ChatViewModel

    lateinit var mBinding:ActivityChatBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityChatBinding.inflate(layoutInflater)

        setContentView(mBinding.root)
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
            showProgressBar(dataState.loading?.isLoading)
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
            mBinding.progressBar.visibility = View.VISIBLE
        }
        else{
            mBinding.progressBar.visibility = View.GONE
        }
    }





}












//val networkCallback: NetworkCallback = object : NetworkCallback() {
//    override fun onAvailable(network: Network) {
//        // network available
//    }
//
//    override fun onLost(network: Network) {
//        // network unavailable
//    }
//}
//
//val connectivityManager = this.getSystemService(Context.CONNECTIVITY_SERVICE)
//
//if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//    connectivityManager.registerDefaultNetworkCallback(networkCallback)
//} else {
//    val request = NetworkRequest.Builder()
//        .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET).build()
//    connectivityManager.registerNetworkCallback(request, networkCallback)
//}