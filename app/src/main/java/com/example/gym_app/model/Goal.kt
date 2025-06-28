package com.example.gym_app.model

data class Goal(
    val _id: String? = null,
//    val userId: String,
    val title: String,
    val type: String, // DAILY, WEEKLY, CUSTOM
    val targetValue: Double?,
    val unit: String?,
    val category: String, // FITNESS, NUTRITION, etc.
    val startDate: String, // yyyy-MM-dd
    val endDate: String?,
    val isActive: Boolean = true,
    val createdAt: String? = null,
    val updatedAt: String? = null
)


