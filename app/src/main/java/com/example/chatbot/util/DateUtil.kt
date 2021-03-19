package com.example.chatbot.util

import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DateUtil
@Inject
constructor(
    private val dateFormat: SimpleDateFormat
)
{
    // Date format: "2019-07-23 HH:mm:ss"

    fun removeTimeFromDateString(sd: String): String{
        return sd.substring(0, sd.indexOf(" "))
    }

    fun removeDateFromString(sd: String): String{
        return sd.substring(sd.indexOf(" ")+1,sd.length)
    }

    fun getCurrentTimestamp(): String {
        return dateFormat.format(Date())
    }

}
