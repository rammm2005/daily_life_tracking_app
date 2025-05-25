package com.example.gym_app.repository

import android.util.Log
import com.example.gym_app.model.User
import com.example.gym_app.network.ApiResponse
import com.example.gym_app.network.ApiService
import com.example.gym_app.network.RetrofitClient
import com.example.gym_app.network.LoginRequest
import com.example.gym_app.network.OtpRequest
import com.example.gym_app.network.ResendOtpRequest
import com.example.gym_app.network.VerifyOtpResponse
import retrofit2.Response

class AuthRepository {

    private val api: ApiService = RetrofitClient.instance

    suspend fun registerUser(user: User): Boolean {
        return try {
            val response = api.registerUser(user)
            if (response.isSuccessful) {
                Log.d("AuthRepo", "Register success: ${response.body()?.message}")
                response.body()?.success == true
            } else {
                val errorBody = response.errorBody()?.string()
                Log.e("AuthRepo", "Register failed: ${response.code()} - $errorBody")
                false
            }
        } catch (e: Exception) {
            Log.e("AuthRepo", "Register exception: ${e.localizedMessage}", e)
            false
        }
    }

    suspend fun login(email: String, password: String): User? {
        val response = api.loginUser(LoginRequest(email, password))
        if (response.isSuccessful) {
            val body = response.body()
            if (body?.success == true) {
                return body.user
            }
        }
        return null
    }

    suspend fun resendOtp(email: String): ApiResponse? {
        val response: Response<ApiResponse> = api.resendOtp(ResendOtpRequest(email))
        return if (response.isSuccessful) response.body() else null
    }

    suspend fun verifyOtp(email: String, otp: String): VerifyOtpResponse? {
        val response: Response<VerifyOtpResponse> = api.verifyOtp(OtpRequest(email, otp))
        return if (response.isSuccessful) response.body() else null
    }
}
