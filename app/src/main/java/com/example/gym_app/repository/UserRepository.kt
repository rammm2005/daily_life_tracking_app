package com.example.gym_app.repository

import android.util.Log
import com.example.gym_app.network.ApiService
import com.example.gym_app.network.ForgotPasswordRequest
import com.example.gym_app.network.ForgotPasswordResponse
import com.example.gym_app.network.ResetPasswordRequest
import com.example.gym_app.network.ResetPasswordResponse
import com.example.gym_app.network.RetrofitClient
import com.example.gym_app.network.UserData
import com.example.gym_app.network.UserUpdateResponse
import retrofit2.Response

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


    suspend fun getUserSpesifictByEmail(email: String): UserData? {
        return try {
            val response = api.spesifictUser(email)
            if (response.isSuccessful && response.body()?.success == true) {
                response.body()?.user
            } else {
                Log.e("UserRepository", "Failed to get user: ${response.message()}")
                null
            }
        } catch (e: Exception) {
            Log.e("UserRepository", "Exception getting user by email", e)
            null
        }
    }


    suspend fun forgotPassword(email: String): ForgotPasswordResponse? {
        return try {
            val request = ForgotPasswordRequest(email)
            val response = api.forgotPassword(request)

            if (response.isSuccessful) {
                response.body()
            } else {
                Log.e("UserRepository", "ForgotPassword failed: ${response.message()}")
                null
            }
        } catch (e: Exception) {
            Log.e("UserRepository", "Exception in forgotPassword", e)
            null
        }
    }

    suspend fun resetPassword(email: String, otp: String, newPassword: String): ResetPasswordResponse? {
        return try {
            val request = ResetPasswordRequest(email, otp, newPassword)
            val response = api.resetPassword(request)

            if (response.isSuccessful) {
                response.body()
            } else {
                Log.e("UserRepository", "ResetPassword failed: ${response.message()}")
                null
            }
        } catch (e: Exception) {
            Log.e("UserRepository", "Exception in resetPassword", e)
            null
        }
    }


    suspend fun updateUserProfile(email: String, request: UserData): UserUpdateResponse? {
        return try {
            val response: Response<UserUpdateResponse> = api.updateProfile(email, request)
            if (response.isSuccessful && response.body()?.success == true) {
                response.body()
            } else {
                Log.e("UserRepository", "Failed to update profile: ${response.message()}")
                null
            }
        } catch (e: Exception) {
            Log.e("UserRepository", "Exception updating profile", e)
            null
        }
    }
}
