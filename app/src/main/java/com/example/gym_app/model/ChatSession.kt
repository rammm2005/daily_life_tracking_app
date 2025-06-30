package com.example.gym_app.model

import org.bson.types.ObjectId

data class ChatSession(
    val _id: String? = null,
    val userId: String? = null,
    val startedAt: String,
    val messages: List<Message> = emptyList()
)
