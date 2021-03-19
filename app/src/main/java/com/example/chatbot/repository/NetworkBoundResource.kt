package com.example.chatbot.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.example.chatbot.util.Constants.ERROR_CHECK_NETWORK_CONNECTION
import com.example.chatbot.util.Constants.ERROR_UNKNOWN
import com.example.chatbot.util.Constants.NETWORK_TIMEOUT
import com.example.chatbot.util.Constants.TESTING_CACHE_DELAY
import com.example.chatbot.util.Constants.TESTING_NETWORK_DELAY
import com.example.chatbot.util.Constants.UNABLE_TODO_OPERATION_WO_INTERNET
import com.example.chatbot.util.Constants.UNABLE_TO_RESOLVE_HOST
import com.example.chatbot.util.Constants.isNetworkError
import com.example.chatbot.util.DataState
import com.example.chatbot.util.Display
import com.example.chatbot.util.DisplayType
import com.example.chatbot.util.GenericApiResponse
import com.example.chatbot.util.GenericApiResponse.*
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO


abstract class NetworkBoundResource<ResponseObject, ViewStateType>
    (
    isNetworkAvailable: Boolean,// is their a network connection?
    isNetworkRequest: Boolean
) {

    private val TAG: String = "AppDebug"

    protected val result = MediatorLiveData<DataState<ViewStateType>>()
    protected lateinit var job: CompletableJob
    protected lateinit var coroutineScope: CoroutineScope

    init {

        setJob(initNewJob())

        if (isNetworkRequest) {

            if (isNetworkAvailable) {

                coroutineScope.launch {

                    createCacheRequestAndReturn("online")
                    // simulate a network delay for testing
//                    delay(TESTING_NETWORK_DELAY)

                    withContext(Dispatchers.Main) {

                        // make network call
                        val apiResponse = createCall()
                        Log.d(TAG, ":RESSSS PONSEEE " + apiResponse.value)
                        result.addSource(apiResponse) { response ->
                            result.removeSource(apiResponse)

                            coroutineScope.launch {
                                handleNetworkCall(response)
                            }
                        }
                    }
                }

            } else {

                    coroutineScope.launch {
                        createCacheRequestAndReturn("offline")
                    }

                onErrorReturn(
                    UNABLE_TODO_OPERATION_WO_INTERNET,
                    shouldUseDialog = false,
                    shouldUseToast = true
                )
            }
        } else {

            setValue(DataState.loading(isLoading = true, cachedData = null))
            coroutineScope.launch {
                createCacheRequestAndReturn("online")
            }
        }


//    else {
//            coroutineScope.launch {
//                delay(TESTING_CACHE_DELAY)
//                // View data from cache only and return
//                createCacheRequestAndReturn()
//            }
//        }
    }

    suspend fun handleNetworkCall(response: GenericApiResponse<ResponseObject>) {

        when (response) {
            is ApiSuccessResponse -> {
                handleApiSuccessResponse(response)
            }
            is ApiErrorResponse -> {
                Log.e(TAG, "NetworkBoundResource: ${response.errorMessage}")
                onErrorReturn(response.errorMessage, true, false)
            }
            is ApiEmptyResponse -> {
                Log.e(TAG, "NetworkBoundResource: Request returned NOTHING (HTTP 204).")
                onErrorReturn("HTTP 204. Returned NOTHING.", true, false)
            }
        }
    }

    fun onCompleteJob(dataState: DataState<ViewStateType>) {
        GlobalScope.launch(Dispatchers.Main) {
            job.complete()
            setValue(dataState)
        }
    }

    fun onErrorReturn(errorMessage: String?, shouldUseDialog: Boolean, shouldUseToast: Boolean) {
        var msg = errorMessage
        var useDialog = shouldUseDialog
        var responseType: DisplayType = DisplayType.None()
        if (msg == null) {
            msg = ERROR_UNKNOWN
        } else if (isNetworkError(msg)) {
            msg = ERROR_CHECK_NETWORK_CONNECTION
            useDialog = false
        }
        if (shouldUseToast) {
            responseType = DisplayType.Toast()
        }
        if (useDialog) {
            responseType = DisplayType.Dialog()
        }
        Log.d("TAG", "onErrorReturn: " + msg)
        onCompleteJob(DataState.error(Display(msg, responseType)))
    }

    fun setValue(dataState: DataState<ViewStateType>) {
        result.value = dataState
    }

    @OptIn(InternalCoroutinesApi::class)
    private fun initNewJob(): Job {
        Log.d(TAG, "initNewJob: called.")
        job = Job() // create new job
        job.invokeOnCompletion(
            onCancelling = true,
            invokeImmediately = true,
            handler = object : CompletionHandler {
                override fun invoke(cause: Throwable?) {
                    if (job.isCancelled) {
                        Log.e(TAG, "NetworkBoundResource: Job has been cancelled.")
                        cause?.let {
                            onErrorReturn(it.message, false, true)
                        } ?: onErrorReturn("Unknown error.", false, true)
                    } else if (job.isCompleted) {
                        Log.e(TAG, "NetworkBoundResource: Job has been completed.")
                        // Do nothing -- Should be handled already at respective places
                    }
                }
            })
        coroutineScope = CoroutineScope(IO + job)
        return job
    }

    abstract suspend fun createCacheRequestAndReturn(sendStatus: String)

    fun asLiveData() = result as LiveData<DataState<ViewStateType>>

    abstract suspend fun handleApiSuccessResponse(response: ApiSuccessResponse<ResponseObject>)

    abstract fun createCall(): LiveData<GenericApiResponse<ResponseObject>>

    abstract fun setJob(job: Job)
}