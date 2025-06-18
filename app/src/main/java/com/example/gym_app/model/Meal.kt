package com.example.gym_app.model

import org.bson.types.ObjectId

data class Meal(
    val _id: String? = null,
    val title: String,
    val description: String,
    val ingredients: List<String>,
    val calories: Int,
    val category: String,
    val image: String,
)
