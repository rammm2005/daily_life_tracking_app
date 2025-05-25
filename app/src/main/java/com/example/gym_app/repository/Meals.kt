package com.example.gym_app.repository

import SessionManager
import android.content.Context
import android.util.Log
import com.example.gym_app.model.Meal
import com.example.gym_app.network.ApiService
import com.example.gym_app.network.RetrofitClient
import kotlinx.coroutines.flow.first

class MealRepository(private val context: Context) {

    private val api: ApiService = RetrofitClient.instance
    private val sessionManager = SessionManager(context)
    private val userRepository = UserRepository()

    suspend fun getMeals(): List<Meal>? {
        return try {
            val email = sessionManager.userEmail.first() ?: return null
            val userId = userRepository.getUserIdByEmail(email) ?: return null
            val response = api.getMealsByUserId(userId)
            if (response.isSuccessful && response.body()?.success == true) {
                response.body()?.data
            } else {
                Log.e("MealRepo", "Failed to fetch meals: ${response.message()}")
                null
            }
        } catch (e: Exception) {
            Log.e("MealRepo", "Exception fetching meals", e)
            null
        }
    }

    suspend fun createMeal(meal: Meal): Meal? {
        return try {
            val response = api.createMeal(meal)
            if (response.isSuccessful && response.body()?.success == true) {
                response.body()?.data
            } else {
                Log.e("MealRepo", "Failed to create meal: ${response.message()}")
                null
            }
        } catch (e: Exception) {
            Log.e("MealRepo", "Exception creating meal", e)
            null
        }
    }

    suspend fun updateMeal(id: String, meal: Meal): Meal? {
        return try {
            val response = api.updateMeal(id, meal)
            if (response.isSuccessful && response.body()?.success == true) {
                response.body()?.data
            } else {
                Log.e("MealRepo", "Failed to update meal: ${response.message()}")
                null
            }
        } catch (e: Exception) {
            Log.e("MealRepo", "Exception updating meal", e)
            null
        }
    }

    suspend fun deleteMeal(id: String): Boolean {
        return try {
            val response = api.deleteMeal(id)
            response.isSuccessful && response.body()?.success == true
        } catch (e: Exception) {
            Log.e("MealRepo", "Exception deleting meal", e)
            false
        }
    }
}
