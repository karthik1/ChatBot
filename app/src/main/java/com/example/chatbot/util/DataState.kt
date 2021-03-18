package com.example.chatbot.util

data class DataState<T>(
    var error: Event<StateError>? = null,
    var loading: Loading = Loading(false),
    var data: Data<T>? = null
) {

    companion object {

        fun <T> error(
            display: Display
        ): DataState<T> {
            return DataState(
                error = Event(
                    StateError(
                        display
                    )
                ),
                loading = Loading(false),
                data = null
            )
        }

        fun <T> loading(
            isLoading: Boolean,
            cachedData: T? = null
        ): DataState<T> {
            return DataState(
                error = null,
                loading = Loading(isLoading),
                data = Data(
                    Event.dataEvent(
                        cachedData
                    ), null
                )
            )
        }

        fun <T> data(
            data: T? = null,
            display: Display? = null
        ): DataState<T> {
            return DataState(
                error = null,
                loading = Loading(false),
                data = Data(
                    Event.dataEvent(data),
                    Event.displayEvent(display)
                )
            )
        }
    }
}