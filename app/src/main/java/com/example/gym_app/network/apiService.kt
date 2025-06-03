package com.example.gym_app.network

import com.example.gym_app.model.Meal
import com.example.gym_app.model.Tip
import com.example.gym_app.model.User
import com.google.gson.annotations.SerializedName
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // ---------- AUTH ----------
    @POST("api/auth/register")
    suspend fun registerUser(@Body user: User): Response<ApiResponse>

    @POST("api/auth/login")
    suspend fun loginUser(@Body loginRequest: LoginRequest): Response<LoginResponse>

    @POST("api/auth/verify-otp")
    suspend fun verifyOtp(@Body otp: OtpRequest): Response<VerifyOtpResponse>

    @POST("api/auth/resend-otp")
    suspend fun resendOtp(@Body resendOtpRequest: ResendOtpRequest): Response<ApiResponse>

    // ---------- USER ----------
    @GET("api/users/email/{email}")
    suspend fun getUserByEmail(@Path("email") email: String): Response<UserResponse>

    // ---------- TIP ----------
    @GET("api/tips/{userId}")
    suspend fun getTipsByUserId(@Path("userId") userId: String): Response<TipListResponse>

    @GET("api/tips/all/data")
    suspend fun getAllTips(): TipListResponse

    @GET("api/tips/detail/{id}")
    suspend fun getTipById(@Path("id") id: String): TipResponse

    @Multipart
    @POST("api/tips")
    suspend fun createTipWithImage(
        @Part("title") title: RequestBody,
        @Part("content") content: RequestBody,
        @Part("description_short") descriptionShort: RequestBody,
        @Part("type") type: RequestBody,
        @Part("userId") userId: RequestBody,
        @Part images: List<MultipartBody.Part>
    ): Response<TipResponse>


    @Multipart
    @PUT("api/tips/{id}")
    suspend fun updateTipWithImage(
        @Path("id") id: String,
        @Part("title") title: RequestBody,
        @Part("content") content: RequestBody,
        @Part("description_short") descriptionShort: RequestBody,
        @Part("type") type: RequestBody,
        @Part("userId") userId: RequestBody,
        @Part images: List<MultipartBody.Part>
    ): Response<TipResponse>

    @DELETE("api/tips/{id}")
    suspend fun deleteTip(@Path("id") id: String): Response<ApiResponse>

    // ---------- MEAL ----------
    @GET("api/meals/{userId}")
    suspend fun getMealsByUserId(@Path("userId") userId: String): Response<MealListResponse>

    @POST("api/meals")
    suspend fun createMeal(@Body meal: Meal): Response<MealResponse>

    @PUT("api/meals/{id}")
    suspend fun updateMeal(@Path("id") id: String, @Body meal: Meal): Response<MealResponse>

    @DELETE("api/meals/{id}")
    suspend fun deleteMeal(@Path("id") id: String): Response<ApiResponse>
}

// ---------- AUTH ----------
data class LoginRequest(val email: String, val password: String)
data class ResendOtpRequest(val email: String)
data class OtpRequest(val email: String, val otp: String)
data class ApiResponse(val success: Boolean, val message: String?)
data class LoginResponse(val success: Boolean, val message: String?, val user: User?)
data class VerifyOtpResponse(val success: Boolean, val message: String?, val role: String?)

// ---------- User ----------
data class UserResponse(
    val success: Boolean,
    val message: String?,
    @SerializedName("userId")
    val Id: String?
)

// ---------- TIP ----------

data class TipResponse(
    val success: Boolean,
    val message: String?,
    val data: Tip?
)

data class TipListResponse(
    val success: Boolean,
    val message: String?,
    val data: List<Tip>
)


// ---------- MEAL ----------

data class MealResponse(
    val success: Boolean,
    val message: String?,
    val data: Meal?
)

data class MealListResponse(
    val success: Boolean,
    val message: String?,
    val data: List<Meal>
)
