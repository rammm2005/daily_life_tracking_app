package com.example.gym_app.model

import org.bson.types.ObjectId
import java.util.Date

data class Message(
    val _id: ObjectId = ObjectId(),
    val userId: ObjectId,
    val timestamp: Date,
    val role: String,
    val content: String
)
