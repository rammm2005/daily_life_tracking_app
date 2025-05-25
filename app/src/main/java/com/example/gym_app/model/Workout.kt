package com.example.gym_app.model

import org.bson.types.ObjectId
import java.io.Serializable

data class Workout(
    val _id: ObjectId = ObjectId(),
    val title: String,
    val description: String,
    val category: String? = null,
    val picPath: String? = null,
    val kcal: Int? = null,
    val difficulty: String? = null,
    val video_url: String? = null,
    val durationAll: String? = null,
    val lessons: List<Lession> = emptyList()
) : Serializable

