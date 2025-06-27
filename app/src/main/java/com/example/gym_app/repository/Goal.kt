package com.example.gym_app.repository


import SessionManager
import android.content.Context
import android.util.Log
import com.example.gym_app.model.Goal
import com.example.gym_app.network.ApiService
import com.example.gym_app.network.RetrofitClient
import kotlinx.coroutines.flow.first

class GoalRepository(private val context: Context) {
    private val api: ApiService = RetrofitClient.instance
    private val sessionManager = SessionManager(context)
    private val userRepository = UserRepository()

    suspend fun getAllGoals(): List<Goal>? {
        return try {
            val email = sessionManager.userEmail.first() ?: return null
            val userId = userRepository.getUserIdByEmail(email) ?: return null
            val response = api.getGoalsByUserId(userId)
            if (response.isSuccessful && response.body()?.success == true) {
                response.body()?.data
            } else {
                Log.e("GoalRepo", "Gagal ambil goals: ${response.code()} ${response.message()}")
                null
            }
        } catch (e: Exception) {
            Log.e("GoalRepo", "Exception ambil goals", e)
            null
        }
    }

    suspend fun getGoalById(id: String): Goal? {
        return try {
            val response = api.getGoalById(id)
            if (response.isSuccessful && response.body()?.success == true) {
                response.body()?.data
            } else {
                Log.e("GoalRepo", "Gagal ambil goal $id: ${response.code()} ${response.message()}")
                null
            }
        } catch (e: Exception) {
            Log.e("GoalRepo", "Exception ambil goal $id", e)
            return null
        }
    }

    suspend fun createGoal(goal: Goal): Goal? {
        return try {
            val email = sessionManager.userEmail.first() ?: return null
            val userId = userRepository.getUserIdByEmail(email) ?: return null

            val response = api.createGoal(
                title = goal.title,
                type = goal.type,
                userId= userId,
                targetValue = goal.targetValue,
                unit = goal.unit,
                category = goal.category,
                startDate = goal.startDate,
                endDate = goal.endDate
            )

            if (response.isSuccessful && response.body()?.success == true) {
                response.body()?.data
            } else {
                Log.e("GoalRepo", "Gagal buat goal: ${response.code()} ${response.message()}")
                null
            }
        } catch (e: Exception) {
            Log.e("GoalRepo", "Exception buat goal", e)
            null
        }
    }

    suspend fun updateGoal(id: String, goal: Goal): Goal? {
        val email = sessionManager.userEmail.first() ?: return null
        val userId = userRepository.getUserIdByEmail(email) ?: return null

        return try {
            val response = api.updateGoal(
                id = id,
                title = goal.title,
                type = goal.type,
                targetValue = goal.targetValue,
                unit = goal.unit,
                category = goal.category,
                startDate = goal.startDate,
                endDate = goal.endDate,
                userId= userId,
            )

            if (response.isSuccessful && response.body()?.success == true) {
                response.body()?.data
            } else {
                Log.e("GoalRepo", "Gagal update goal $id: ${response.code()} ${response.message()}")
                null
            }
        } catch (e: Exception) {
            Log.e("GoalRepo", "Exception update goal $id", e)
            null
        }
    }

    suspend fun deleteGoal(id: String): Boolean {
        return try {
            val response = api.deleteGoal(id)
            response.isSuccessful && response.body()?.success == true
        } catch (e: Exception) {
            Log.e("GoalRepo", "Exception hapus goal $id", e)
            false
        }
    }
}
