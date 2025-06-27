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
            val response = api.getTrackerByUserId(userId)
            if (response.isSuccessful && response.body()?.success == true) {
                response.body()?.data
            } else {
                Log.e("TrackerRepo", "Gagal ambil tracker: ${response.code()} ${response.message()}")
                null
            }
        } catch (e: Exception) {
            Log.e("TrackerRepo", "Exception ambil tracker", e)
            null
        }
    }

    suspend fun getTrackerById(id: String): DailyTracker? {
        return try {
            val response = api.getTrackerById(id)
            if (response.isSuccessful && response.body()?.success == true) {
                response.body()?.data
            } else {
                Log.e("TrackerRepo", "Gagal ambil tracker $id: ${response.code()} ${response.message()}")
                null
            }
        } catch (e: Exception) {
            Log.e("TrackerRepo", "Exception ambil tracker $id", e)
            return null
        }
    }

    suspend fun createTracker(tracker: DailyTracker): DailyTracker? {
        return try {
            val email = sessionManager.userEmail.first() ?: return null
            val userId = userRepository.getUserIdByEmail(email) ?: return null

            val trackerWithUserId = tracker.copy(userId = userId)

            val response = api.createDailyTracker(trackerWithUserId)

            if (response.isSuccessful && response.body()?.success == true) {
                response.body()?.data
            } else {
                Log.e("TrackerRepo", "Gagal buat tracker: ${response.code()} ${response.message()}")
                null
            }
        } catch (e: Exception) {
            Log.e("TrackerRepo", "Exception buat tracker", e)
            null
        }
    }
}
