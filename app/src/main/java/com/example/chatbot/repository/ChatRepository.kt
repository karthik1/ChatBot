package com.example.chatbot.repository

import android.util.Log
import androidx.lifecycle.LiveData
import com.example.chatbot.api.ChatApiService
import com.example.chatbot.api.response.MessageResponse
import com.example.chatbot.model.Chat
import com.example.chatbot.persistence.ChatDao
import com.example.chatbot.session.SessionManager
import com.example.chatbot.ui.state.ChatViewState
import com.example.chatbot.util.AbsentLiveData
import com.example.chatbot.util.DataState
import com.example.chatbot.util.GenericApiResponse
import com.example.chatbot.util.GenericApiResponse.*
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.Main
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject


class ChatRepository @Inject constructor(
    val chatDao: ChatDao,
    val chatApiService: ChatApiService,
    val sessionManager: SessionManager
) {

    private var repositoryJob: Job? = null

    fun getBotResponse(senderText: String, chatWindowNum: Int): LiveData<DataState<ChatViewState>> {

        return object : NetworkBoundResource<MessageResponse, ChatViewState>(
            sessionManager.isConnectedToTheInternet(), true

        ) {

            override suspend fun createCacheRequestAndReturn() {
                TODO("Not yet implemented")
            }

            override suspend fun handleApiSuccessResponse(response: ApiSuccessResponse<MessageResponse>) {

                response?.let { response ->

                    val messageResponse = response.body
                    val msgJsonObj = messageResponse?.message
                    val botResponseObj = msgJsonObj?.getAsJsonPrimitive("message")
                    val botResponse = botResponseObj?.asString
//                    val botResponse = botResponseObj?.asString
                    Log.d("SUCCESS RESPONSE", "handleApiSuccessResponse: ")

                    //TODO -- > Create An Extension Function to create Date

                    val simpleDateFormat = SimpleDateFormat("dd-MM-yyyy-hh-mm-ss")
                    val format: String = simpleDateFormat.format(Date())
                    Log.d("MainActivity", "Current Timestamp: $format")



                    val chatRow = Chat(
                        senderOrBotText = botResponse,
                        chatWindowNum = chatWindowNum,
                        timeStamp = format,
                        status = "recv"

                    )

                    val rowId = chatDao.insert(chatRow)


                    //Check if the insertion successfull then set The result


                    CoroutineScope(Main).launch {
                        setValue(
                            DataState.success(
                                data = ChatViewState(chatList = null,chat = chatRow)
                            )
                        )

                    }

                }


            }

            override fun createCall(): LiveData<GenericApiResponse<MessageResponse>> {
                return chatApiService.getBotResponse(
                    "6nt5d1nJHkqbkphe",
                    senderText,
                    "63906",
                    "chirag1"

                )
            }

            override fun setJob(job: Job) {
                repositoryJob?.cancel()
                repositoryJob = job
            }

        }.asLiveData()
    }

    fun loadFromDB(chatWindowNum: Int): LiveData<DataState<ChatViewState>> {

        return object : NetworkBoundResource<MessageResponse, ChatViewState>(
            sessionManager.isConnectedToTheInternet(), true

        ) {
            override suspend fun createCacheRequestAndReturn() {

                val chatlist = chatDao.searchByWindowNum(chatWindowNum)
                CoroutineScope(Main).launch {
                    setValue(
                        DataState.success(
                            data = ChatViewState(chatList = chatlist, null),
                            display = null
                        )
                    )
                }

            }

            override suspend fun handleApiSuccessResponse(response: ApiSuccessResponse<MessageResponse>) {

            }

            override fun createCall(): LiveData<GenericApiResponse<MessageResponse>> {
                return AbsentLiveData.create()
            }

            override fun setJob(job: Job) {
                repositoryJob?.cancel()
                repositoryJob = job
            }

        }.asLiveData()


    }


}


//callAsync.enqueue(object : Callback<MessageResponse?> {
//    override fun onResponse(
//        call: Call<MessageResponse?>?,
//        response: Response<MessageResponse?>
//    ) {
//        if (response.isSuccessful()) {
//            val apiResponse: MessageResponse? = response.body()
//
//            //API response
//            Log.d("RESponse Call OBJ", "onResponse: " + apiResponse)
//        } else {
//            println("Request Error :: " + response.errorBody())
//
//        }
//    }
//
//    override fun onFailure(call: Call<MessageResponse?>?, t: Throwable) {
//        println("Network Error :: " + t.localizedMessage)
//    }
//})


//val retrofitBuilder: Retrofit.Builder =
//    Retrofit.Builder().baseUrl(Constants.BASE_URL)
//        .addConverterFactory(GsonConverterFactory.create())
//
//val retrofit = retrofitBuilder.build()
//
//val recipeApi = retrofit.create(ChatApiService::class.java)
//
//
//val callAsync: Call<MessageResponse> = recipeApi
//    .getBotResponsee("6nt5d1nJHkqbkphe", "Hi", "63906", "chirag1")
//
//


//fun getResponse() {
//    val result = MediatorLiveData<MessageResponse>()
//    val response = chatApiService.getBotResponse("6nt5d1nJHkqbkphe", "Hi", "63906", "chirag1")
//    Log.d("Responseee ", "getResponse: " + response.value)
//    result.addSource(response) {
//        result.removeSource(response)
//        Log.d("Resssponseeee", "getResponse: " + it)
//        when (it) {
//            is ApiSuccessResponse<*> -> {
//                Log.d("RESPONSE SUCCESS", "getResponse: ")
//            }
//            is ApiErrorResponse<*> -> {
//                Log.e("Response FAI", "NetworkBoundResource: ${it.errorMessage}")
//            }
//            is ApiEmptyResponse<*> -> {
//                Log.e(
//                    "Respo EMPTY",
//                    "NetworkBoundResource: Request returned NOTHING (HTTP 204)."
//                )
//            }
//        }
//
//    }
//}
