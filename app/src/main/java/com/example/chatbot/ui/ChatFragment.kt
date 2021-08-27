package com.example.chatbot.ui

import android.content.Context
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkRequest
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chatbot.R
import com.example.chatbot.adapter.ChatListAdapter
import com.example.chatbot.databinding.FragmentChatBinding
import com.example.chatbot.model.Chat
import com.example.chatbot.model.ChatFactory
import com.example.chatbot.session.SessionManager
import com.example.chatbot.ui.state.ChatStateEvent.*
import com.example.chatbot.util.Constants.CURRENT_WINDOW
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class ChatFragment : Fragment(), FragmentListener {


    @Inject
    lateinit var mChatViewModel: ChatViewModel

    @Inject
    lateinit var chatFactory: ChatFactory

    @Inject
    lateinit var sessionManager: SessionManager

    val editTextString: String? = null

    lateinit var dataStateHandler: DataStateListener
    lateinit var mBinding: FragmentChatBinding
    lateinit var mChatlistView: RecyclerView;
    lateinit var mChatListAdapter: ChatListAdapter
    lateinit var layoutManager: LinearLayoutManager
    lateinit var sharedPreference: SharedPreferences
    lateinit var preferenceEditor: SharedPreferences.Editor


    var currentWindow: Int = 1

    var chatList: ArrayList<Chat> = ArrayList()
    var offlineList: ArrayList<Chat> = ArrayList()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        mBinding = FragmentChatBinding.inflate(inflater, container, false)
        return mBinding.root

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true) //Create a menu with 2 chat windows item and one new window item


        initRecyclerViewAdapter()

        setSendButtonClick()

        networkConnectivityCallBack()

//        initSharedPref()

        subscribeObservers()

        triggerLoadChatWindowEvent(currentWindow)

        (activity as MainActivity)?.setActivityListener(this)
    }



    private fun setSendButtonClick() {

        mBinding.buttonSend.setOnClickListener(View.OnClickListener {

            val message = mBinding.messageInput.text.toString()
            if (message.isNotEmpty() && message != "") {
                val chat: Chat

                if (sessionManager.isConnectedToTheInternet())
                    chat = chatFactory.createChatItem(message, currentWindow, "online")
                else
                    chat = chatFactory.createChatItem(message, currentWindow, "offline")


                chatList.add(chat)
                mChatListAdapter.submitList(chatList)
                mChatlistView.scrollToPosition(mChatListAdapter.getItemCount() - 1);

                triggerGetResponseEvent(message, "new", currentWindow)
                mBinding.messageInput.setText("");
            }

        })
    }


    private fun subscribeObservers() {

        mChatViewModel.dataState.observe(viewLifecycleOwner, Observer { dataState ->

            dataStateHandler.onDataStateChange(dataState)

            dataState.data?.let { data ->

                data.data?.let { event ->

                    event.getContentIfNotHandled()?.let { chatViewState ->
                        chatViewState.chatList?.let {
                            mChatViewModel.displayChat(it)
                            Log.d("SIZE1.", "subscribeObservers: " + chatList?.size)
                        }

                        chatViewState.chat?.let {
                            mChatViewModel.loadTypedText(it)
                            Log.d("SIZE2..", "subscribeObservers: " + chatList?.size)
                        }
                    }
                }
            }
        })

        mChatViewModel.viewState.observe(viewLifecycleOwner, Observer { chatViewState ->

            chatViewState.chatList?.let {
                chatList = it as ArrayList<Chat>
                updateRecyclerViewAdapter(it)
                checkForOfflineMessages()
            }

            chatViewState.chat?.let {

                if (it.chatWindowNum == currentWindow) {
                    chatList.add(it)
                    updateRecyclerViewAdapter(chatList)
                }
            }
        })
    }

    fun checkForOfflineMessages() {

        if (sessionManager.isConnectedToTheInternet()) {
            if (mChatListAdapter?.itemCount != 0) {
                var chat = chatList.get(mChatListAdapter.itemCount - 1)
                var status = chat.status
                if (chat.status.equals("offline")) {
                    //Trigger GetResponseEvent
                    chat.senderOrBotText?.let {
                        CoroutineScope(Main).launch {
                            triggerGetResponseEvent(it, status, currentWindow)
                        }
                    }
                }
            }
        }
    }

    private fun initRecyclerViewAdapter() {
        mChatlistView = mBinding.recyclerView
        mChatListAdapter = ChatListAdapter()
        layoutManager = LinearLayoutManager(context)
        mChatlistView.layoutManager = layoutManager
        layoutManager.stackFromEnd = true
        mChatlistView.apply {
            adapter = mChatListAdapter
           // layoutManager = LinearLayoutManager(context)
        }

    }

    // STATE EVENTS
    private fun triggerLoadChatWindowEvent(chatWindowNum: Int?) {

        mChatViewModel.setStateEvent(SwitchChatWindowEvent(chatWindowNum));
    }

    //Dint use this case
    private fun triggerNewChatWindowEvent() {

        mChatViewModel.setStateEvent(AddNewWindowEvent());
    }

    private fun triggerGetResponseEvent(message: String, status: String?, chatWindowNum: Int?) {
        mChatViewModel.setStateEvent(GetResponseEvent(message, status, currentWindow))
    }


    private fun updateRecyclerViewAdapter(list: List<Chat>) {
        mChatListAdapter.submitList(list)
        mChatlistView.scrollToPosition(mChatListAdapter.getItemCount() - 1);
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            dataStateHandler = context as DataStateListener
        } catch (e: ClassCastException) {
            println("$context must implement DataStateListener")
        }
    }

    private fun networkConnectivityCallBack() {

        val connectivityManager =
            activity?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val networkRequest = NetworkRequest.Builder().build()
        connectivityManager.registerNetworkCallback(
            networkRequest,
            object : ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network) {
                    super.onAvailable(network)
                    Log.i("Tag", "got active connection")
                    checkForOfflineMessages()
                }

                override fun onLost(network: Network) {
                    super.onLost(network)
                    Log.i("Tag", "losing active connection")
                }
            })

    }

    override fun onDestroy() {

        preferenceEditor.putInt(CURRENT_WINDOW, 1)
        preferenceEditor.commit()

        super.onDestroy()
    }

    override fun switchWindows(currentWindow:Int) {
        this.currentWindow = currentWindow
        triggerLoadChatWindowEvent(currentWindow)
        mBinding.messageInput.setText("");
    }
}





//override fun onOptionsItemSelected(item: MenuItem): Boolean {
//
//    return when (item.itemId) {
//        R.id.chat_1 -> {
//            if (currentWindow != 1) {
//                currentWindow = 1
//                preferenceEditor.putInt(CURRENT_WINDOW, currentWindow!!)
//                triggerLoadChatWindowEvent(currentWindow)
//                item.isChecked = true
//            }
//            true
//        }
//        R.id.chat_2 -> {
//            if (currentWindow != 2) {
//                currentWindow = 2
//                preferenceEditor.putInt(CURRENT_WINDOW, currentWindow!!)
//                triggerLoadChatWindowEvent(currentWindow)
//                item.isChecked = true
//            }
//            true
//
//        }
//        R.id.chat_3 -> {
//            if (currentWindow != 3) {
//
//                currentWindow = 3
//                preferenceEditor.putInt(CURRENT_WINDOW, currentWindow!!)
//                triggerLoadChatWindowEvent(currentWindow)
//                item.isChecked = true
//            }
//            true
//        }
//        else -> return super.onOptionsItemSelected(item)
//    }
//
//}

//
//private fun initSharedPref() {
//    sharedPreference = this.requireActivity().getSharedPreferences("pref", Context.MODE_PRIVATE)
//    preferenceEditor = sharedPreference.edit()
//
//    currentWindow = sharedPreference.getInt(CURRENT_WINDOW, 0)
//
//    if (currentWindow == 0) {
//        preferenceEditor.putInt(CURRENT_WINDOW, 1)
//        currentWindow = 1
//    }
//    preferenceEditor.commit()
//}