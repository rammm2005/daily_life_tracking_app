package com.example.gym_app.model

import org.bson.types.ObjectId

data class DailyTracker(
    val id: ObjectId = ObjectId(),
    val userId: ObjectId,
    val date: String,
    val weight_kg: Double,
    val water_intake_liters: Double,
    val sleep_hours: Int,
    val mood: String,
    val workoutCompletedIds: List<ObjectId> = emptyList(),
    val mealsEatenIds: List<ObjectId> = emptyList()
)
