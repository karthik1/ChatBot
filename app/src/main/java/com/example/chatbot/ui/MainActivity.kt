package com.example.chatbot.ui

import android.content.Context
import android.app.Activity
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chatbot.R
import com.example.chatbot.adapter.NavigationDrawerListAdapter
import com.example.chatbot.databinding.ActivityDrawerLayoutBinding
import com.example.chatbot.model.NavigationAdapterItemModel
import com.example.chatbot.repository.ChatRepository
import com.example.chatbot.util.*
import com.example.chatbot.util.Constants.ADD_CHAT_WINDOW
import com.example.chatbot.util.Constants.CHAT_WINDOW
import com.example.chatbot.util.Constants.CURRENT_WINDOW
import com.example.chatbot.util.Constants.DEFAULT_CHAT_WINDOW_NUM
import com.example.chatbot.util.Constants.WINDOW_MAX
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : AppCompatActivity(), DataStateListener,
    NavigationDrawerListAdapter.Interaction {

    //    private lateinit var appBarConfiguration:
    lateinit var toolbar: Toolbar
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var toggle: ActionBarDrawerToggle
    lateinit var mBinding: ActivityDrawerLayoutBinding
    lateinit var adapter: NavigationDrawerListAdapter
    lateinit var rcView: RecyclerView
    val chatWindowList: ArrayList<NavigationAdapterItemModel> = ArrayList()

    lateinit var sharedPreference: SharedPreferences
    lateinit var preferenceEditor: SharedPreferences.Editor
    var currentWindow: Int? = null


    lateinit var fragmentListener: FragmentListener

    lateinit var chatFragment: ChatFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = ActivityDrawerLayoutBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        drawerLayout = mBinding.drawerLayout
        toolbar = mBinding.appBarMain.toolbar

        toolbar.setTitle(CHAT_WINDOW + DEFAULT_CHAT_WINDOW_NUM)
        setSupportActionBar(toolbar)
        toggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )

        addDrawerListener()
        toggle.syncState()

//Toast.makeText(this,"Show",Toast.LENGTH_LONG).show()
        initSharedPref()
        initNavigationRecyclerView()
        showMainFragment()



    }

    private fun addDrawerListener() {
        drawerLayout.addDrawerListener(object : DrawerLayout.DrawerListener
        {
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
                val imm: InputMethodManager =
                    getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(drawerView.getWindowToken(), 0)
            }

            override fun onDrawerOpened(drawerView: View) {

            }

            override fun onDrawerClosed(drawerView: View) {
            }

            override fun onDrawerStateChanged(newState: Int) {
            }

        })
    }

    override fun onResume() {
        super.onResume()
//        setContentView(R.layout.dummy_layout)
    }

    fun setActivityListener(listener: FragmentListener) {
        fragmentListener = listener
    }

    fun initSharedPref() {
        sharedPreference = this.getSharedPreferences("pref", Context.MODE_PRIVATE)
        preferenceEditor = sharedPreference.edit()

        currentWindow = sharedPreference.getInt(CURRENT_WINDOW, 0)

        if (currentWindow == 0) {
            preferenceEditor.putInt(CURRENT_WINDOW, 1)
            currentWindow = 1
        }
        preferenceEditor.commit()


    }

    private fun showMainFragment() {

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container, ChatFragment(), "ChatFragment")
            .commit()

    }


    private fun initNavigationRecyclerView() {

        val numOfChatWindows = sharedPreference.getInt(WINDOW_MAX, 1)

        //To inflated Created Chat Window list
        for (i in 1..numOfChatWindows) {
            chatWindowList.add(NavigationAdapterItemModel(CHAT_WINDOW, i))
        }

        chatWindowList.add(NavigationAdapterItemModel(ADD_CHAT_WINDOW, 0))

        rcView = mBinding.rvChatWindow
        rcView.layoutManager = LinearLayoutManager(this)
        rcView.setHasFixedSize(true)
        adapter = NavigationDrawerListAdapter(this, this)
        rcView.adapter = adapter
        adapter.submitList(chatWindowList)

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

        if (isVisible) {
            mBinding.appBarMain.progressBar.visibility = View.VISIBLE
        } else {
            mBinding.appBarMain.progressBar.visibility = View.GONE
        }
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {

            return true
        }
        return super.onOptionsItemSelected(item)
    }


    //Nav Drawer RecyclerViewClick Events
    override fun onNavigationItemSelected(position: Int, item: NavigationAdapterItemModel) {

        //Inflate New Window
        if (position == adapter.itemCount - 1) {

            chatWindowList.add(position, NavigationAdapterItemModel("Chat Window", position + 1))
            adapter.submitList(chatWindowList)
            preferenceEditor.putInt(WINDOW_MAX, position + 1)
            preferenceEditor.commit()

        } else {//Switch to selected one
            if (currentWindow != position + 1) {

                currentWindow = position + 1
                toolbar.setTitle("Chat Window " + "$currentWindow")
                adapter.selected_item = position;
                adapter.notifyDataSetChanged()
                fragmentListener.switchWindows(currentWindow!!)

            }
            drawerLayout.closeDrawers()
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