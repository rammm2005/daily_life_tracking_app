package com.example.gym_app.model

data class Favorite(
    val _id: String? = null,
    val userId: String,
    val tips: List<Tip> = emptyList(),
    val workouts: List<Workout> = emptyList(),
    val meals: List<Meal> = emptyList(),
    val createdAt: String? = null,
    val updatedAt: String? = null
)

data class FavoriteItem(
    val id: String,
    val title: String,
    val type: String,
    val imageUrl: String
)

data class FavoriteRequest(
    val userId: String,
    val tipId: String? = null,
    val workoutId: String? = null,
    val mealId: String? = null
)


data class FavoriteResponse(
    val success: Boolean,
    val data: Favorite?
)


data class FavoriteListResponse(
    val success: Boolean,
    val data: List<Favorite>? = null,
    val message: String? = null
)