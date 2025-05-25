package com.example.gym_app.repository

import android.util.Log
import com.example.gym_app.network.ApiService
import com.example.gym_app.network.RetrofitClient

class UserRepository {

    private val api: ApiService = RetrofitClient.instance

    suspend fun getUserIdByEmail(email: String): String? {
        return try {
            val response = api.getUserByEmail(email)
            if (response.isSuccessful && response.body()?.success == true) {
                response.body()?.Id
            } else {
                Log.e("UserRepository", "Failed to get userId: ${response.message()}")
                Log.e("UserRepositoryEmail", "get userId: ${response.body()?.Id}")
                null
            }
        } catch (e: Exception) {
            Log.e("UserRepository", "Exception getting userId by email", e)
            null
        }
    }
}
