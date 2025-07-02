package com.example.gym_app.repository

import SessionManager
import android.content.Context
import android.util.Log
import com.example.gym_app.model.Favorite
import com.example.gym_app.model.FavoriteRequest
import com.example.gym_app.network.ApiService
import com.example.gym_app.network.RetrofitClient
import kotlinx.coroutines.flow.first

class FavoriteRepository(private val context: Context) {
    private val api: ApiService = RetrofitClient.instance
    private val sessionManager = SessionManager(context)
    private val userRepository = UserRepository()

    suspend fun getFavorites(): List<Favorite>? {
        return try {
            val email = sessionManager.userEmail.first() ?: return null
            val userId = userRepository.getUserIdByEmail(email) ?: return null
            val response = api.getFavoriteByUserId(userId)

            if (response.isSuccessful && response.body()?.success == true) {
                response.body()?.data
            } else {
                Log.e("FavoriteRepo", "Gagal ambil favorites: ${response.code()} ${response.message()}")
                null
            }
        } catch (e: Exception) {
            Log.e("FavoriteRepo", "Exception ambil favorites", e)
            null
        }
    }

    suspend fun getFavoriteById(id: String): Favorite? {
        return try {
            val response = api.getFavoriteById(id)
            if (response.isSuccessful && response.body()?.success == true) {
                response.body()?.data
            } else {
                Log.e("FavoriteRepo", "Gagal ambil favorite $id: ${response.code()} ${response.message()}")
                null
            }
        } catch (e: Exception) {
            Log.e("FavoriteRepo", "Exception ambil favorite $id", e)
            null
        }
    }

    suspend fun addToFavorite(tipId: String? = null, workoutId: String? = null, mealId: String? = null): Favorite? {
        return try {
            val email = sessionManager.userEmail.first() ?: return null
            val userId = userRepository.getUserIdByEmail(email) ?: return null
            val request = FavoriteRequest(
                userId = userId,
                tipId = tipId,
                workoutId = workoutId,
                mealId = mealId
            )

            val response = api.addToFavorite(request)
            if (response.isSuccessful && response.body()?.success == true) {
                response.body()?.data
            } else {
                Log.e("FavoriteRepo", "Gagal tambah favorite: ${response.code()} ${response.message()}")
                null
            }
        } catch (e: Exception) {
            Log.e("FavoriteRepo", "Exception tambah favorite", e)
            null
        }
    }


    suspend fun removeFromFavorite(
        tipId: String? = null,
        mealId: String? = null,
        workoutId: String? = null
    ): Boolean {
        return try {
            val email = sessionManager.userEmail.first() ?: return false
            val userId = userRepository.getUserIdByEmail(email) ?: return false

            val request = FavoriteRequest(
                userId = userId,
                tipId = tipId,
                mealId = mealId,
                workoutId = workoutId
            )

            val response = api.removeFromFavorite(request)
            response.isSuccessful && response.body()?.success == true
        } catch (e: Exception) {
            Log.e("FavoriteRepo", "Exception hapus favorite", e)
            false
        }
    }

    suspend fun deleteFavorite(id: String): Boolean {
        return try {
            val response = api.deleteFavorite(id)
            response.isSuccessful && response.body()?.success == true
        } catch (e: Exception) {
            Log.e("FavoriteRepo", "Exception delete favorite by ID", e)
            false
        }
    }
}
