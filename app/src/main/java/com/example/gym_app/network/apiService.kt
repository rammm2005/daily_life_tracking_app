package com.example.gym_app.network

import com.example.gym_app.model.ChatSession
import com.example.gym_app.model.DailyTracker
import com.example.gym_app.model.FavoriteListResponse
import com.example.gym_app.model.FavoriteRequest
import com.example.gym_app.model.FavoriteResponse
import com.example.gym_app.model.Goal
import com.example.gym_app.model.Meal
import com.example.gym_app.model.Message
import com.example.gym_app.model.Reminder
import com.example.gym_app.model.Tip
import com.example.gym_app.model.User
import com.example.gym_app.model.Workout
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

    @POST("/api/auth/forgot-password")
    suspend fun forgotPassword(
        @Body request: ForgotPasswordRequest
    ): Response<ForgotPasswordResponse>

    @POST("/api/auth/reset-password")
    suspend fun resetPassword(
        @Body request: ResetPasswordRequest
    ): Response<ResetPasswordResponse>

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
    @GET("api/meals/all/data")
    suspend fun getAllMeals(): MealListResponse

    @GET("api/meals/detail/{id}")
    suspend fun getMealById(@Path("id") id: String): MealResponse

    @GET("api/meals/{userId}")
    suspend fun getMealsByUserId(@Path("userId") userId: String): Response<MealListResponse>

    @Multipart
    @POST("api/meals")
    suspend fun createMealWithImage(
        @Part("title") title: RequestBody,
        @Part("description") description: RequestBody,
        @Part("ingredients") ingredients: RequestBody,
        @Part("calories") calories: RequestBody,
        @Part("category") category: RequestBody,
        @Part("userId") userId: RequestBody,
        @Part image: MultipartBody.Part
    ): Response<MealResponse>

    @Multipart
    @PUT("api/meals/{id}")
    suspend fun updateMealWithImage(
        @Path("id") id: String,
        @Part("title") title: RequestBody,
        @Part("description") description: RequestBody,
        @Part("ingredients") ingredients: RequestBody,
        @Part("calories") calories: RequestBody,
        @Part("category") category: RequestBody,
        @Part("userId") userId: RequestBody,
        @Part image: MultipartBody.Part
    ): Response<MealResponse>

    @Multipart
    @PUT("api/meals/{id}")
    suspend fun updateMealWithoutImage(
        @Path("id") id: String,
        @Part("title") title: RequestBody,
        @Part("description") description: RequestBody,
        @Part("ingredients") ingredients: RequestBody,
        @Part("calories") calories: RequestBody,
        @Part("category") category: RequestBody,
        @Part("userId") userId: RequestBody
    ): Response<MealResponse>



    @DELETE("api/meals/{id}")
    suspend fun deleteMeal(@Path("id") id: String): Response<ApiResponse>

    // ---------- WORKOUT ----------
    @GET("api/workouts/all")
    suspend fun getAllWorkouts(): WorkoutListResponse

    @GET("api/workouts/{id}")
    suspend fun getWorkoutById(@Path("id") id: String): WorkoutResponse

    @Multipart
    @POST("api/workouts")
    suspend fun createWorkoutWithImage(
        @Part("title") title: RequestBody,
        @Part("description") description: RequestBody,
        @Part("category") category: RequestBody,
        @Part("kcal") kcal: RequestBody,
        @Part("difficulty") difficulty: RequestBody,
        @Part("video_url") videoUrl: RequestBody,
        @Part("durationAll") durationAll: RequestBody,
        @Part("lessons") lessons: RequestBody,
        @Part image: MultipartBody.Part
    ): Response<WorkoutResponse>

    @Multipart
    @PUT("api/workouts/{id}")
    suspend fun updateWorkoutWithImage(
        @Path("id") id: String,
        @Part("title") title: RequestBody,
        @Part("description") description: RequestBody,
        @Part("category") category: RequestBody,
        @Part("kcal") kcal: RequestBody,
        @Part("difficulty") difficulty: RequestBody,
        @Part("video_url") videoUrl: RequestBody,
        @Part("durationAll") durationAll: RequestBody,
        @Part("lessons") lessons: RequestBody,
        @Part image: MultipartBody.Part
    ): Response<WorkoutResponse>

    @Multipart
    @PUT("api/workouts/{id}")
    suspend fun updateWorkoutWithoutImage(
        @Path("id") id: String,
        @Part("title") title: RequestBody,
        @Part("description") description: RequestBody,
        @Part("category") category: RequestBody,
        @Part("kcal") kcal: RequestBody,
        @Part("difficulty") difficulty: RequestBody,
        @Part("video_url") videoUrl: RequestBody,
        @Part("durationAll") durationAll: RequestBody,
        @Part("lessons") lessons: RequestBody
    ): Response<WorkoutResponse>

    @DELETE("api/workouts/{id}")
    suspend fun deleteWorkout(@Path("id") id: String): Response<ApiResponse>

    // ---------- REMINDER ----------
    @GET("api/reminders/user/{userId}")
    suspend fun getRemindersByUserId(
        @Path("userId") userId: String
    ): Response<ReminderListResponse>

    @GET("api/reminders/{id}")
    suspend fun getReminderById(
        @Path("id") id: String
    ): Response<ReminderResponse>

    @FormUrlEncoded
    @POST("api/reminders")
    suspend fun createReminder(
        @Field("userId") userId: String,
        @Field("type") type: String,
        @Field("title") title: String,
        @Field("schedule") schedule: String,
        @Field("description") description: String,
        @Field("method") method: List<String>,
        @Field("days") days: List<String>,
        @Field("status") status: String,
        @Field("repeat") repeat: String
    ): Response<ReminderResponse>

    @FormUrlEncoded
    @PUT("api/reminders/{id}")
    suspend fun updateReminder(
        @Path("id") id: String,
        @Field("type") type: String,
        @Field("title") title: String,
        @Field("schedule") schedule: String,
        @Field("description") description: String,
        @Field("method") method: List<String>,
        @Field("days") days: List<String>,
        @Field("status") status: String,
        @Field("repeat") repeat: String
    ): Response<ReminderResponse>


    @DELETE("api/reminders/{id}")
    suspend fun deleteReminder(
        @Path("id") id: String
    ): Response<ApiResponse>



    // ---------- GOAL API ----------
    @GET("api/goals/user/{userId}")
    suspend fun getGoalsByUserId(
        @Path("userId") userId: String
    ): Response<GoalListResponse>

    @GET("api/goals/{id}")
    suspend fun getGoalById(
        @Path("id") id: String
    ): Response<GoalResponse>

    @FormUrlEncoded
    @POST("api/goals")
    suspend fun createGoal(
        @Field("title") title: String,
        @Field("userId") userId: String,
        @Field("type") type: String,
        @Field("targetValue") targetValue: Double?,
        @Field("unit") unit: String?,
        @Field("category") category: String,
        @Field("startDate") startDate: String,
        @Field("isActive") isActive: Boolean,
        @Field("endDate") endDate: String?
    ): Response<GoalResponse>

    @FormUrlEncoded
    @PUT("api/goals/{id}")
    suspend fun updateGoal(
        @Path("id") id: String,
        @Field("userId") userId: String,
        @Field("title") title: String,
        @Field("type") type: String,
        @Field("targetValue") targetValue: Double?,
        @Field("unit") unit: String?,
        @Field("category") category: String,
        @Field("startDate") startDate: String,
        @Field("isActive") isActive: Boolean,
        @Field("endDate") endDate: String?
    ): Response<GoalResponse>

    @DELETE("api/goals/{id}")
    suspend fun deleteGoal(
        @Path("id") id: String
    ): Response<ApiResponse>


    // ---------- DAILY TRACKER API ----------

    @GET("api/tracker/user/{userId}")
    suspend fun getDailyTrackersByUserId(@Path("userId") userId: String): Response<DailyTrackerListResponse>

    @GET("api/tracker/{id}")
    suspend fun getDailyTrackerById(@Path("id") id: String): Response<DailyTrackerResponse>

    @POST("api/tracker")
    suspend fun createDailyTracker(@Body tracker: DailyTracker): Response<DailyTrackerResponse>


    @GET("api/tracker/summary/{userId}")
    suspend fun getSummary(@Path("userId") userId: String): Response<SummaryResponse>

    @PUT("api/tracker/{id}")
    suspend fun updateDailyTracker(@Path("id") id: String, @Body tracker: DailyTracker): Response<DailyTrackerResponse>

    @DELETE("api/tracker/{id}")
    suspend fun deleteDailyTracker(@Path("id") id: String): Response<DailyTrackerResponse>


    // ---------- FAVORITE API ----------

    @GET("api/favorite/user/{userId}")
    suspend fun getFavoriteByUserId(@Path("userId") userId: String): Response<FavoriteListResponse>

    @GET("api/favorite/{id}")
    suspend fun getFavoriteById(@Path("id") id: String): Response<FavoriteResponse>

    @POST("api/favorite")
    suspend fun addToFavorite(@Body request: FavoriteRequest): Response<FavoriteResponse>

    @PUT("api/favorite/remove")
    suspend fun removeFromFavorite(@Body request: FavoriteRequest): Response<FavoriteResponse>

    @DELETE("api/favorite/{id}")
    suspend fun deleteFavorite(@Path("id") id: String): Response<FavoriteResponse>

    // ---------- CHAT BOT API ----------

    @POST("/api/chatbot/chat")
    suspend fun sendMessage(@Body request: ChatRequest): Response<ChatResponse>

    @DELETE("/api/chatbot/chat/messages")
    suspend fun deleteMessages(@Body request: DeleteMessagesRequest): Response<DeleteMessagesResponse>

    @GET("api/chatbot/chat/sessions/{userId}")
    suspend fun getChatSessions(@Path("userId") userId: String): Response<ChatSessionsResponse>

    @GET("api/chatbot/chat/session/{sessionId}/messages")
    suspend fun getMessagesBySessionId(@Path("sessionId") sessionId: String): Response<SessionMessagesResponse>

    // ---------- PROFILE API ----------

    @GET("/api/users/{email}")
    suspend fun spesifictUser(
        @Path("email") email: String,
    ): Response<UserGetResponse>
    @PUT("/api/users/email/{email}")
    suspend fun updateProfile(
        @Path("email") email: String,
        @Body request: UserData
    ): Response<UserUpdateResponse>


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


// ---------- WORKOUT ----------

data class WorkoutResponse(
    val success: Boolean,
    val message: String?,
    val data: Workout?
)

data class WorkoutListResponse(
    val success: Boolean,
    val message: String?,
    val data: List<Workout>
)

// ---------- REMINDER ----------

data class ReminderResponse(
    val success: Boolean,
    val message: String?,
    val data: Reminder?
)

data class ReminderListResponse(
    val success: Boolean,
    val message: String?,
    val data: List<Reminder>
)

// ---------- GOAL ----------

data class GoalResponse(
    val success: Boolean,
    val data: Goal
)

data class GoalListResponse(
    val success: Boolean,
    val message: String?,
    val data: List<Goal>
)

// ---------- DailyTracker ----------

data class DailyTrackerResponse(
    val success: Boolean,
    val data: DailyTracker
)

data class DailyTrackerListResponse(
    val success: Boolean,
    val message: String?,
    val data: List<DailyTracker>
)

// --- Request & Response DTOs ---
data class ChatRequest(
    val userId: String,
    val message: String
)
data class ChatResponse(
    val reply: String,
    val suggestions: List<String>
)

data class DeleteMessagesRequest(
    val userId: String,
    val messageIds: List<String>
)

data class DeleteMessagesResponse(
    val message: String,
    val deletedCount: Int
)

data class SessionMessagesResponse(
    val messages: List<Message>
)

data class ChatSessionsResponse(
    val success: Boolean,
    val data: List<ChatSession>
)


// Summary Data
data class SummaryResponse(
    val success: Boolean,
    val data: SummaryData
)

data class SummaryData(
    val totalCalories: String,
    val totalSleep: Int,
    val totalWorkout: Int
)

data class UserUpdateResponse(
    val success: Boolean,
    val message: String,
    val user: UserUpdate
)

data class UserGetResponse(
    val success: Boolean,
    val message: String,
    val user: UserData
)

data class UserUpdate(
    val _id: String,
    val name: String,
    val email: String,
    val age: Int,
    val gender: String,
    val height_cm: Int,
    val weight_kg: Int
)
data class UserData(
    val name: String?,
    val age: Int?,
    val gender: String?,
    val height_cm: Int?,
    val weight_kg: Int?
)


// Forgot Passoword
data class ForgotPasswordRequest(
    val email: String
)
data class ForgotPasswordResponse(
    val success: Boolean,
    val message: String
)

data class ResetPasswordRequest(
    val email: String,
    val otp: String,
    val newPassword: String
)

data class ResetPasswordResponse(
    val success: Boolean,
    val message: String
)

