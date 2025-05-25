package com.example.gym_app.model

import org.bson.types.ObjectId
import java.io.Serializable

data class Lession(
    val _id: ObjectId = ObjectId(),
    val workoutId: ObjectId,
    val title: String,
    val description: String,
    val duration: String,
    val link: String,
    val picPath: String,
    val repetitions: Int,
    val rest_seconds: Int,
    val video_url: String
): Serializable


