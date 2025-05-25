package com.example.gym_app.model

import org.bson.types.ObjectId

data class Tip(
    val _id: String? = null,
    val Image: String,
    val title: String,
    val content: String,
    val description_short: String?,
    val type: String
)
