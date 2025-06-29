package com.example.gym_app.model
data class DailyTracker(
    val _id: String? = null,
    val userId: String,
    val date: String,
    val weightKg: Double,
    val waterIntakeLiters: Double,
    val sleepHours: Double,
    val mood: String,
    val workoutCompletedIds: List<String> = emptyList(),
    val mealsEatenIds: List<String> = emptyList(),
    val caloriesConsumed: Int? = null,
    val caloriesBurned: Int? = null,
    val stepCount: Int? = null,
    val stressLevel: Int? = null,
    val notes: String? = null,
    val goalCheckins: List<String> = emptyList(),
    val createdAt: String? = null,
    val updatedAt: String? = null
)

//data class DailyTrackerRequest(
//    val userId: String,
//    val date: String,
//    val weightKg: Double,
//    val waterIntakeLiters: Double,
//    val sleepHours: Double,
//    val mood: String,
//    val workoutCompletedIds: List<String> = emptyList(),
//    val mealsEatenIds: List<String> = emptyList(),
//    val caloriesConsumed: Int? = null,
//    val caloriesBurned: Int? = null,
//    val stepCount: Int? = null,
//    val stressLevel: Int? = null,
//    val notes: String? = null,
//    val goalCheckins: Map<String, Boolean> = emptyMap()
//)
