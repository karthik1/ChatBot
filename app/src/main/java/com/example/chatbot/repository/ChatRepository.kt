package com.example.chatbot.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModelProvider
import com.example.chatbot.api.ChatApiService
import com.example.chatbot.api.response.MessageResponse
import com.example.chatbot.persistence.ChatDao
import com.example.chatbot.session.SessionManager
import com.example.chatbot.ui.state.ChatViewState
import com.example.chatbot.util.Constants
import com.example.chatbot.util.DataState
import com.example.chatbot.util.GenericApiResponse
import com.example.chatbot.util.GenericApiResponse.*
import kotlinx.coroutines.Job
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Inject


class ChatRepository @Inject constructor(
    val chatDao: ChatDao,
    val chatApiService: ChatApiService,
    val sessionManager: SessionManager
) {

    private var repositoryJob: Job? = null

    fun getBotResponse(): LiveData<DataState<ChatViewState>> {

        return object :NetworkBoundResource<MessageResponse,ChatViewState>(
            sessionManager.isConnectedToTheInternet()
        )
        {


            override suspend fun createCacheRequestAndReturn() {
                TODO("Not yet implemented")
            }

            override suspend fun handleApiSuccessResponse(response: ApiSuccessResponse<MessageResponse>) {
                TODO("Not yet implemented")
            }

            override fun createCall(): LiveData<GenericApiResponse<MessageResponse>> {
                TODO("Not yet implemented")
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
