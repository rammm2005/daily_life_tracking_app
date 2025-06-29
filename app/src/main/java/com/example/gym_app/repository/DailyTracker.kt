package com.example.gym_app.repository

import SessionManager
import android.content.Context
import android.util.Log
import com.example.gym_app.model.DailyTracker
import com.example.gym_app.network.ApiService
import com.example.gym_app.network.RetrofitClient
import kotlinx.coroutines.flow.first

class DailyTrackerRepository(private val context: Context) {

    private val api: ApiService = RetrofitClient.instance
    private val sessionManager = SessionManager(context)
    private val userRepository = UserRepository()

    suspend fun getAllTrackers(): List<DailyTracker>? {
        return try {
            val email = sessionManager.userEmail.first() ?: return null
            val userId = userRepository.getUserIdByEmail(email) ?: return null
            val response = api.getDailyTrackersByUserId(userId)
            if (response.isSuccessful && response.body()?.success == true) {
                response.body()?.data
            } else {
                Log.e("DailyTrackerRepo", "Gagal ambil tracker: ${response.code()} ${response.message()}")
                null
            }
        } catch (e: Exception) {
            Log.e("DailyTrackerRepo", "Exception ambil tracker", e)
            null
        }
    }

    suspend fun getTrackerById(id: String): DailyTracker? {
        return try {
            val response = api.getDailyTrackerById(id)
            if (response.isSuccessful && response.body()?.success == true) {
                response.body()?.data
            } else {
                Log.e("DailyTrackerRepo", "Gagal ambil tracker ID $id")
                null
            }
        } catch (e: Exception) {
            Log.e("DailyTrackerRepo", "Exception ambil tracker ID $id", e)
            null
        }
    }

    suspend fun createTracker(tracker: DailyTracker): DailyTracker? {
        return try {
            val email = sessionManager.userEmail.first() ?: return null
            val userId = userRepository.getUserIdByEmail(email) ?: return null

            val request = DailyTracker(
                userId = userId,
                date = tracker.date,
                weightKg = tracker.weightKg,
                waterIntakeLiters = tracker.waterIntakeLiters,
                sleepHours = tracker.sleepHours,
                mood = tracker.mood,
                workoutCompletedIds = tracker.workoutCompletedIds,
                mealsEatenIds = tracker.mealsEatenIds,
                caloriesConsumed = tracker.caloriesConsumed,
                caloriesBurned = tracker.caloriesBurned,
                stepCount = tracker.stepCount,
                stressLevel = tracker.stressLevel,
                notes = tracker.notes,
                goalCheckins = tracker.goalCheckins
            )

            val response = api.createDailyTracker(request)

            if (response.isSuccessful && response.body()?.success == true) {
                response.body()?.data
            } else {
                Log.e("DailyTrackerRepo", "Gagal create tracker")
                null
            }
        } catch (e: Exception) {
            Log.e("DailyTrackerRepo", "Exception create tracker", e)
            null
        }
    }

    suspend fun updateTracker(id: String, tracker: DailyTracker): DailyTracker? {
        return try {
            val request = DailyTracker(
                userId = tracker.userId,
                date = tracker.date,
                weightKg = tracker.weightKg,
                waterIntakeLiters = tracker.waterIntakeLiters,
                sleepHours = tracker.sleepHours,
                mood = tracker.mood,
                workoutCompletedIds = tracker.workoutCompletedIds,
                mealsEatenIds = tracker.mealsEatenIds,
                caloriesConsumed = tracker.caloriesConsumed,
                caloriesBurned = tracker.caloriesBurned,
                stepCount = tracker.stepCount,
                stressLevel = tracker.stressLevel,
                notes = tracker.notes,
                goalCheckins = tracker.goalCheckins
            )

            val response = api.updateDailyTracker(id, request)

            if (response.isSuccessful && response.body()?.success == true) {
                response.body()?.data
            } else {
                Log.e("DailyTrackerRepo", "Gagal update tracker $id")
                null
            }
        } catch (e: Exception) {
            Log.e("DailyTrackerRepo", "Exception update tracker", e)
            null
        }
    }

    suspend fun deleteTracker(id: String): Boolean {
        return try {
            val response = api.deleteDailyTracker(id)
            response.isSuccessful && response.body()?.success == true
        } catch (e: Exception) {
            Log.e("DailyTrackerRepo", "Exception delete tracker", e)
            false
        }
    }
}
