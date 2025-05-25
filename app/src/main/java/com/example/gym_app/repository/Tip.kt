package com.example.gym_app.repository

import SessionManager
import android.content.Context
import android.util.Log
import com.example.gym_app.model.Tip
import com.example.gym_app.network.ApiService
import com.example.gym_app.network.RetrofitClient
import kotlinx.coroutines.flow.first

class TipRepository(private val context: Context) {

    private val api: ApiService = RetrofitClient.instance
    private val sessionManager = SessionManager(context)
    private val userRepository = UserRepository()

    suspend fun getTips(): List<Tip>? {
        return try {
            val email = sessionManager.userEmail.first() ?: return null
            val userId = userRepository.getUserIdByEmail(email) ?: return null
            val response = api.getTipsByUserId(userId)
            if (response.isSuccessful && response.body()?.success == true) {
                response.body()?.data
            } else {
                Log.e("TipRepo", "Failed to fetch tips: ${response.message()}")
                null
            }
        } catch (e: Exception) {
            Log.e("TipRepo", "Exception fetching tips", e)
            null
        }
    }

    suspend fun createTip(tip: Tip): Tip? {
        return try {
            val response = api.createTip(tip)
            if (response.isSuccessful && response.body()?.success == true) {
                response.body()?.data
            } else {
                Log.e("TipRepo", "Failed to create tip: ${response.message()}")
                null
            }
        } catch (e: Exception) {
            Log.e("TipRepo", "Exception creating tip", e)
            null
        }
    }

    suspend fun updateTip(id: String, tip: Tip): Tip? {
        return try {
            val response = api.updateTip(id, tip)
            if (response.isSuccessful && response.body()?.success == true) {
                response.body()?.data
            } else {
                Log.e("TipRepo", "Failed to update tip: ${response.message()}")
                null
            }
        } catch (e: Exception) {
            Log.e("TipRepo", "Exception updating tip", e)
            null
        }
    }

    suspend fun deleteTip(id: String): Boolean {
        return try {
            val response = api.deleteTip(id)
            response.isSuccessful && response.body()?.success == true
        } catch (e: Exception) {
            Log.e("TipRepo", "Exception deleting tip", e)
            false
        }
    }
}
