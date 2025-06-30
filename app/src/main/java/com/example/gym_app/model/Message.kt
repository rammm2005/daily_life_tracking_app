package com.example.gym_app.model

import org.bson.types.ObjectId
import java.util.Date

data class Message(
    val _id: String? = null,
    val userId: String? = null,
    val timestamp: String?= null,
    val role: String,
    val content: String
)
