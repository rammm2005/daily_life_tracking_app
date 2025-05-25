package com.example.gym_app.model

import org.bson.types.ObjectId

data class Reminder(
    val _id: ObjectId = ObjectId(),
    val userId: ObjectId,
    val type: String,
    val title: String,
    val schedule: String,
    val method: List<String>,
    val days: List<String>
)
