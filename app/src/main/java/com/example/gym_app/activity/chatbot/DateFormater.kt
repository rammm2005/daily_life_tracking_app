package com.example.gym_app.activity.chatbot

import java.text.SimpleDateFormat
import java.util.*

fun formatIsoDateToIndonesian(dateString: String): String {
    return try {
        val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        parser.timeZone = TimeZone.getTimeZone("UTC")
        val date = parser.parse(dateString)

        val formatter = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale("id", "ID"))
        formatter.format(date ?: Date())
    } catch (e: Exception) {
        "-"
    }
}
