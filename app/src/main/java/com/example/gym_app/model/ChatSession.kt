package com.example.gym_app.model

import org.bson.types.ObjectId

data class ChatSession(
    val _id: ObjectId = ObjectId(),
    val userId: ObjectId,
    val startedAt: String,
    val messages: List<Message> = emptyList()
)
