package com.example.chatbot.util


data class Loading(val isLoading: Boolean)
data class Data<T>(val data: Event<T>?, val display: Event<Display>?)
data class StateError(val response: Display)


data class Display(val message: String?, val responseType: DisplayType)

sealed class DisplayType{

    class Toast: DisplayType()

    class Dialog: DisplayType()

    class None: DisplayType()
}


/*
  Used as a wrapper for data that is exposed via a LiveData that represents an event.
 */
open class Event<out T>(private val content: T) {

    var hasBeenHandled = false
        private set // Allow external read but not write

    /*
     Returns the content and prevents its use again.
     */
    fun getContentIfNotHandled(): T? {
        return if (hasBeenHandled) {
            null
        } else {
            hasBeenHandled = true
            content
        }
    }

    /*
      Returns the content, even if it's already been handled.
     */
    fun peekContent(): T = content

    override fun toString(): String {
        return "Event(content=$content, hasBeenHandled=$hasBeenHandled)"
    }

    companion object {

        private val TAG: String = "AppDebug"

        // No need to create an event if the data is null
        fun <T> dataEvent(data: T?): Event<T>? {
            data?.let {
                return Event(it)
            }
            return null
        }

        // same as above
        fun displayEvent(display: Display?): Event<Display>? {
            display?.let {
                return Event(display)
            }
            return null
        }
    }
}

