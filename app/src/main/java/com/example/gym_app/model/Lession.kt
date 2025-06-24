package com.example.gym_app.model

import org.bson.types.ObjectId
import java.io.Serializable

data class Lession(
    val _id: String? = null,
    val title: String,
    val duration: String,
    val video_url: String,
    val description: String,
): Serializable


