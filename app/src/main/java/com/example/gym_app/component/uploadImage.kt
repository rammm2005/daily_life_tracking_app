package com.example.gym_app.component

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

suspend fun uploadImage(imageUri: String): String {
    return withContext(Dispatchers.IO) {

        try {
            kotlinx.coroutines.delay(2000)
            "https://yourserver.com/uploads/${imageUri.substringAfterLast("/")}"
        } catch (e: Exception) {
            ""
        }
    }
}
