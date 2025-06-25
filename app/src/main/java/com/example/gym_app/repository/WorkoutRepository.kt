package com.example.gym_app.repository

import SessionManager
import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import com.example.gym_app.model.Workout
import com.example.gym_app.network.ApiService
import com.example.gym_app.network.RetrofitClient
import kotlinx.coroutines.flow.first
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import java.io.File

class WorkoutRepository(private val context: Context) {

    private val api: ApiService = RetrofitClient.instance
    private val sessionManager = SessionManager(context)
    private val userRepository = UserRepository()

    suspend fun getAllWorkouts(): List<Workout>? {
        return try {
            val response = api.getAllWorkouts()
            if (response.success) response.data else null
        } catch (e: Exception) {
            Log.e("WorkoutRepo", "Error fetching all workouts", e)
            null
        }
    }

    suspend fun getWorkoutById(id: String): Workout? {
        return try {
            val response = api.getWorkoutById(id)
            if (response.success) response.data else null
        } catch (e: Exception) {
            Log.e("WorkoutRepo", "Error fetching workout by ID", e)
            null
        }
    }

    suspend fun createWorkout(workout: Workout, imageUri: Uri?): Workout? {
        return try {
            val email = sessionManager.userEmail.first() ?: return null
            val userId = userRepository.getUserIdByEmail(email) ?: return null

            val parts = getWorkoutRequestParts(workout, userId)
            val imagePart = imageUri?.let { getImageMultipart(it) }

            if (imagePart == null) throw IllegalArgumentException("Workout creation requires an image")

            val response = api.createWorkoutWithImage(
                title = parts.title,
                description = parts.description,
                category = parts.category,
                kcal = parts.kcal,
                difficulty = parts.difficulty,
                videoUrl = parts.video_url,
                durationAll = parts.duration,
                lessons = parts.lessons,
                image = imagePart
            )

            if (response.isSuccessful && response.body()?.success == true) {
                response.body()?.data
            } else {
                Log.e("WorkoutRepo", "Failed to create workout: ${response.code()} ${response.message()}")
                null
            }
        } catch (e: Exception) {
            Log.e("WorkoutRepo", "Exception creating workout", e)
            null
        }
    }

    suspend fun updateWorkout(id: String, workout: Workout, imageUri: Uri?): Workout? {
        return try {
            val email = sessionManager.userEmail.first() ?: return null
            val userId = userRepository.getUserIdByEmail(email) ?: return null

            val parts = getWorkoutRequestParts(workout, userId)
            val imagePart = imageUri?.takeIf { it.scheme == "content" || it.scheme == "file" }?.let { getImageMultipart(it) }
            Log.d("WorkoutRepo", "Image URI scheme: ${imageUri?.scheme}, URI: $imageUri")
            val response = if (imagePart != null) {
                api.updateWorkoutWithImage(
                    id = id,
                    title = parts.title,
                    description = parts.description,
                    category = parts.category,
                    kcal = parts.kcal,
                    difficulty = parts.difficulty,
                    videoUrl = parts.video_url,
                    durationAll = parts.duration,
                    lessons = parts.lessons,
                    image = imagePart
                )
            } else {
                api.updateWorkoutWithoutImage(
                    id = id,
                    title = parts.title,
                    description = parts.description,
                    category = parts.category,
                    kcal = parts.kcal,
                    difficulty = parts.difficulty,
                    videoUrl = parts.video_url,
                    durationAll = parts.duration,
                    lessons = parts.lessons
                )
            }

            if (response.isSuccessful && response.body()?.success == true) {
                response.body()?.data
            } else {
                Log.e("WorkoutRepo", "Failed to update workout: ${response.code()} ${response.message()}")
                null
            }
        } catch (e: Exception) {
            Log.e("WorkoutRepo", "Exception updating workout", e)
            null
        }
    }

    suspend fun deleteWorkout(id: String): Boolean {
        return try {
            val response = api.deleteWorkout(id)
            response.isSuccessful && response.body()?.success == true
        } catch (e: Exception) {
            Log.e("WorkoutRepo", "Exception deleting workout", e)
            false
        }
    }

    private fun getWorkoutRequestParts(workout: Workout, userId: String): WorkoutRequestParts {
        return WorkoutRequestParts(
            title = workout.title.toRequestBody("text/plain".toMediaTypeOrNull()),
            description = workout.description.toRequestBody("text/plain".toMediaTypeOrNull()),
            category = (workout.category ?: "").toRequestBody("text/plain".toMediaTypeOrNull()),
            kcal = (workout.kcal?.toString() ?: "0").toRequestBody("text/plain".toMediaTypeOrNull()),
            difficulty = (workout.difficulty ?: "").toRequestBody("text/plain".toMediaTypeOrNull()),
            video_url = (workout.video_url ?: "").toRequestBody("text/plain".toMediaTypeOrNull()),
            duration = (workout.durationAll ?: "").toRequestBody("text/plain".toMediaTypeOrNull()),
            lessons = JSONArray(workout.lessons.map { l ->
                mapOf(
                    "title" to l.title,
                    "duration" to l.duration,
                    "videoUrl" to l.videoUrl,
                    "description" to l.description,
                )
            }).toString().toRequestBody("text/plain".toMediaTypeOrNull())
        )
    }

    private fun getImageMultipart(uri: Uri): MultipartBody.Part {
        val contentResolver = context.contentResolver
        val inputStream = contentResolver.openInputStream(uri)
            ?: throw IllegalArgumentException("Invalid URI: $uri")

        val fileName = getFileName(uri)
        val tempFile = File.createTempFile("upload", fileName, context.cacheDir)
        tempFile.outputStream().use { output ->
            inputStream.use { input ->
                input.copyTo(output)
            }
        }

        val requestFile = tempFile.readBytes().toRequestBody("image/*".toMediaTypeOrNull())
        return MultipartBody.Part.createFormData("pic", fileName, requestFile)
    }


    private fun getFileName(uri: Uri): String {
        var name = "image.jpg"
        val cursor = context.contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (cursor.moveToFirst()) {
                name = cursor.getString(nameIndex)
            }
        }
        return name
    }

    data class WorkoutRequestParts(
        val title: okhttp3.RequestBody,
        val description: okhttp3.RequestBody,
        val category: okhttp3.RequestBody,
        val kcal: okhttp3.RequestBody,
        val difficulty: okhttp3.RequestBody,
        val video_url: okhttp3.RequestBody,
        val duration: okhttp3.RequestBody,
        val lessons: okhttp3.RequestBody
    )
}
