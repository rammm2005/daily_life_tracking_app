package com.example.gym_app.model

import org.bson.types.ObjectId

data class Tip(
    val _id: String? = null,
    val images: List<String> = emptyList(),
    val title: String,
    val content: String,
    val description_short: String?,
//    val userId: String?,
    val type: String
)
