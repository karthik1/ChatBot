package com.example.chatbot.ui

import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.example.chatbot.R
import com.example.chatbot.ui.state.ChatStateEvent.*
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ChatFragment : Fragment(R.layout.fragment_chat) {


    @Inject
    lateinit var chatViewModel: ChatViewModel

    val editTextString: String? = null

    lateinit var dataStateHandler: DataStateListener

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true) //Create a menu with 2 chat windows item and one new window item

        subscribeObservers()
      //  initRecyclerViewAdapter()

        //TEMPORARY
        triggerGetResponseEvent("hi")


    }


    private fun subscribeObservers() {

        chatViewModel.dataState.observe(viewLifecycleOwner, Observer {dataState ->

            dataStateHandler.onDataStateChange(dataState)

            dataState.data?.let { data ->

                data.data?.let { event ->

                    event.getContentIfNotHandled()?.let { chatViewState ->
                        chatViewState.chatList?.let {

                            // Update viewstate() in the viewmodel
                        }

                        chatViewState.text?.let {

                            // Update viewstate() in the viewmodel
                        }
                    }
                }
            }
        })

        chatViewModel.viewState.observe(viewLifecycleOwner, Observer { chatViewState ->

            chatViewState.chatList?.let {

                // Update the RecyclerView
            }

            chatViewState.text?.let {

                // update the Edittext

                //Later change it to HashMap To accomodate Multiple Windows

            }

        })

    }

    private fun initRecyclerViewAdapter() {
        TODO("Not yet implemented")
    }


    private fun triggerLoadChatWindowEvent() {

        //Store the text if ter is any in the editted -- Later change it to hashmap
        chatViewModel.setStateEvent(SwitchChatWindowEvent());
    }

    private fun triggerNewChatWindowEvent() {

        //Store the text if ter is any in the editted -- Later change it to hashmap
        chatViewModel.setStateEvent(AddNewWindowEvent());
    }

    private fun triggerGetResponseEvent(message: String) {

        chatViewModel.setStateEvent(GetResponseEvent(message));
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