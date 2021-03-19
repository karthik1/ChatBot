package com.example.chatbot.ui

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkRequest
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chatbot.adapter.ChatListAdapter
import com.example.chatbot.databinding.FragmentChatBinding
import com.example.chatbot.model.Chat
import com.example.chatbot.model.ChatFactory
import com.example.chatbot.session.SessionManager
import com.example.chatbot.ui.state.ChatStateEvent.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class ChatFragment : Fragment() {


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
    var chatList: ArrayList<Chat> = ArrayList()


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

        mBinding.buttonSend.setOnClickListener(View.OnClickListener {

            val message = mBinding.messageInput.text.toString()
            val chat: Chat

            if (sessionManager.isConnectedToTheInternet())
                chat = chatFactory.createChatItem(message, 1, "online")
            else
                chat = chatFactory.createChatItem(message, 1, "offline")

            chatList.add(chat)
            mChatListAdapter.submitList(chatList)
//        else

            mChatlistView.scrollToPosition(mChatListAdapter.getItemCount() - 1);
            triggerGetResponseEvent(message)
            mBinding.messageInput.setText("");
        })

        connectivityCallBack()
        subscribeObservers()
        triggerLoadChatWindowEvent()


    }


    private fun subscribeObservers() {

        mChatViewModel.dataState.observe(viewLifecycleOwner, Observer { dataState ->

            dataStateHandler.onDataStateChange(dataState)

            dataState.data?.let { data ->

                data.data?.let { event ->

                    event.getContentIfNotHandled()?.let { chatViewState ->
                        chatViewState.chatList?.let {
                            mChatViewModel.displayChat(it)

                        }

                        chatViewState.chat?.let {
                            mChatViewModel.loadTypedText(it)
                        }
                    }
                }
            }
        })

        mChatViewModel.viewState.observe(viewLifecycleOwner, Observer { chatViewState ->

            chatViewState.chatList?.let {
                chatList = it as ArrayList<Chat>
                updateRecyclerViewAdapter(it)
            }

            chatViewState.chat?.let {

                chatList.add(it)
                updateRecyclerViewAdapter(chatList)
                Log.d("SIZE", "subscribeObservers: " + chatList?.size)
            }

        })


    }

    private fun initRecyclerViewAdapter() {
        mChatlistView = mBinding.recyclerView
        mChatListAdapter = ChatListAdapter()
        layoutManager = LinearLayoutManager(context)
        mChatlistView.layoutManager = layoutManager
        layoutManager.stackFromEnd = true
        mChatlistView.apply {
//            layoutManager = LinearLayoutManager(activity)
            adapter = mChatListAdapter
        }

    }

// STATE EVENTS

    private fun triggerLoadChatWindowEvent() {

        //Store the text if ter is any in the editted -- Later change it to hashmap
        mChatViewModel.setStateEvent(SwitchChatWindowEvent());
    }

    private fun triggerNewChatWindowEvent() {

        //Store the text if ter is any in the editted -- Later change it to hashmap
        mChatViewModel.setStateEvent(AddNewWindowEvent());
    }

    private fun triggerGetResponseEvent(message: String) {


        mChatViewModel.setStateEvent(GetResponseEvent(message));
    }


    private fun updateRecyclerViewAdapter(list: List<Chat>) {
        mChatListAdapter.submitList(list)
        mChatlistView.scrollToPosition(mChatListAdapter.getItemCount() - 1);
    }

//MENU RELATED

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)


        // TODO --> Menu for MultipleWindows
//        inflater.inflate(R.menu.main_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {


            // TODO --> Menu item to create new window  or move to other window
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            dataStateHandler = context as DataStateListener
        } catch (e: ClassCastException) {
            println("$context must implement DataStateListener")
        }
    }

    private fun connectivityCallBack() {

        val connectivityManager =
            activity?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val networkRequest = NetworkRequest.Builder().build()
        connectivityManager.registerNetworkCallback(
            networkRequest,
            object : ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network) {
                    super.onAvailable(network)
                    if (mChatListAdapter?.itemCount != 0) {
                        var chat = chatList.get(mChatListAdapter.itemCount - 1)
                        if (chat.status.equals("offline")) {
                            //Trigger GetResponseEvent
                            chat.senderOrBotText?.let {
                                CoroutineScope(Main).launch {
                                    triggerGetResponseEvent(it)
                                }

                            }
                        }
                    }
                }

                override fun onLost(network: Network) {
                    super.onLost(network)
                    Log.i("Tag", "losing active connection")
                }
            })
    }
//        val connectivityManager = context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
//
//        connectivityManager?.let {
//            it.registerDefaultNetworkCallback(object : ConnectivityManager.NetworkCallback() {
//                override fun onAvailable(network: Network) {
//                    Log.d("Network ", "onAvailable: ")
//                }
//
//                override fun onLost(network: Network) {
//                    Log.d("Network", "onLost: ")
//                    //take action when network connection is lost
//                }
//            })
//        }


}


//        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,
//            object : OnBackPressedCallback(true) {
//                override fun handleOnBackPressed() {
//                    if (mBinding.messageInput.hasFocus())
//                        mBinding.messageInput.clearFocus()
////                    else
////                        activity?.onBackPressed()
//                }
//            }
//        )


//        mBinding.messageInput.setOnFocusChangeListener(object : View.OnFocusChangeListener {
//            override fun onFocusChange(v: View?, hasFocus: Boolean) {
//                if (!hasFocus) {
//                    Log.d("FOCUS CHANGE 1", "onFocusChange: ")
//                    mChatlistView.scrollToPosition(mChatListAdapter.getItemCount() - 1);
//
//                } else {
//                    mChatlistView.scrollToPosition(mChatListAdapter.getItemCount() - 1);
//
//                    Log.d("FOCUS CHANGE 2", "onFocusChange: ")
//
//                }
//            }
//        })