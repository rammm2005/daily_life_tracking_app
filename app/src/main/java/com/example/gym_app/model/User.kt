package com.example.gym_app.model
import org.bson.types.ObjectId

data class User(
    val _id: String? = null,
    val name: String,
    val email: String,
    val password: String,
    val age: Int,
    val gender: String,
    val height_cm: Int,
    val weight_kg: Int,
    val favoriteWorkouts: List<ObjectId> = emptyList(),
    val favoriteMeals: List<ObjectId> = emptyList(),
    val goal: Goal? = null,
    val role: String = "user",
    val isVerified: Boolean = false,
    val otpAttempt: Int = 0,
    val isBlocked: Boolean = false
)

data class Goal(
    val type: String,
    val targetWeight: Int,
    val startDate: String?,
    val endDate: String?
)
