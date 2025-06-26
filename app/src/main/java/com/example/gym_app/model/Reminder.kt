package com.example.gym_app.model

data class Reminder(
    val _id: String? = null,
    val type: String,
    val title: String,
    val schedule: String,
    val description: String,
    val method: List<String>,
    val days: List<String>,
    val status: String, // 'active', 'paused', 'done'
    val repeat: String, // 'none', 'daily', 'weekly', 'monthly'
    val createdAt: String? = null,
    val updatedAt: String? = null
)
