package com.example.chatbot.ui

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chatbot.adapter.ChatListAdapter
import com.example.chatbot.databinding.FragmentChatBinding
import com.example.chatbot.model.Chat
import com.example.chatbot.model.ChatFactory
import com.example.chatbot.ui.state.ChatStateEvent.*
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ChatFragment : Fragment() {


    @Inject
    lateinit var mChatViewModel: ChatViewModel

    val editTextString: String? = null

    lateinit var dataStateHandler: DataStateListener
    lateinit var mBinding: FragmentChatBinding
    lateinit var mChatlistView: RecyclerView;
    lateinit var mChatListAdapter: ChatListAdapter
    lateinit var layoutManager: LinearLayoutManager
    val chatList: ArrayList<Chat> = ArrayList()

    @Inject
    lateinit var chatFactory: ChatFactory


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

        //TEMPORARY

        mBinding.buttonSend.setOnClickListener(View.OnClickListener {

            triggerGetResponseEvent(mBinding.messageInput.text.toString())
            mBinding.messageInput.setText("");
        })


        subscribeObservers()

//        triggerLoadChatWindowEvent()
    }


    private fun subscribeObservers() {

        mChatViewModel.dataState.observe(viewLifecycleOwner, Observer { dataState ->

            dataStateHandler.onDataStateChange(dataState)

            dataState.data?.let { data ->

                data.data?.let { event ->

                    event.getContentIfNotHandled()?.let { chatViewState ->
                        chatViewState.chatList?.let {

                            // Update viewstate() in the viewmodel
                        }

                        chatViewState.chat?.let {

                            // Update viewstate() in the viewmodel
                            mChatViewModel.loadTypedText(it)
                        }
                    }
                }
            }
        })

        mChatViewModel.viewState.observe(viewLifecycleOwner, Observer { chatViewState ->

            chatViewState.chatList?.let {

                // Update the RecyclerView
            }

            chatViewState.chat?.let {

                chatList.add(it)
                mChatListAdapter.submitList(chatList)
                mChatlistView.scrollToPosition(mChatListAdapter.getItemCount() - 1);
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


    private fun triggerLoadChatWindowEvent() {

        //Store the text if ter is any in the editted -- Later change it to hashmap
        mChatViewModel.setStateEvent(SwitchChatWindowEvent());
    }

    private fun triggerNewChatWindowEvent() {

        //Store the text if ter is any in the editted -- Later change it to hashmap
        mChatViewModel.setStateEvent(AddNewWindowEvent());
    }

    private fun triggerGetResponseEvent(message: String) {

        val chat = chatFactory.createChatItem(message, 1)
        chatList.add(chat)
        mChatListAdapter.submitList(chatList)
        mChatlistView.scrollToPosition(mChatListAdapter.getItemCount() - 1);
        mChatViewModel.setStateEvent(GetResponseEvent(message));
    }


    private fun updateRecyclerViewAdapter(list: List<Chat>?) {

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