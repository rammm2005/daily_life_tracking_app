package com.example.gym_app.model

import org.bson.types.ObjectId

data class DailyTracker(
    val id: String? = null,
    val userId: String? = null,
    val date: String,
    val weight_kg: Double,
    val water_intake_liters: Double,
    val sleep_hours: Int,
    val mood: String,
    val workoutCompletedIds: List<String> = emptyList(),
    val mealsEatenIds: List<String> = emptyList()
)
