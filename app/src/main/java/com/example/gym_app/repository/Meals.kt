package com.example.gym_app.repository

import SessionManager
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.OpenableColumns
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.gym_app.model.Meal
import com.example.gym_app.model.Tip
import com.example.gym_app.network.ApiService
import com.example.gym_app.network.RetrofitClient
import com.google.gson.Gson
import kotlinx.coroutines.flow.first
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class MealRepository(private val context: Context) {

    private val api: ApiService = RetrofitClient.instance
    private val sessionManager = SessionManager(context)
    private val userRepository = UserRepository()


    suspend fun getMealById(mealId: String): Meal? {
        return try {
            val response = api.getMealById(mealId)
            if (response.success) {
                Log.d("Meal", "Successfully fetched meal: $response")
                response.data
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun getAllMeals(): List<Meal>? {
        return try {
            val response = api.getAllMeals()
            if (response.success) {
                Log.d("Meal", "Successfully fetched meals: $response")
                response.data
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun getMeals(): List<Meal>? {
        return try {
            val email = sessionManager.userEmail.first() ?: return null
            val userId = userRepository.getUserIdByEmail(email) ?: return null
            val response = api.getMealsByUserId(userId)
            if (response.isSuccessful && response.body()?.success == true) {
                response.body()?.data
            } else {
                Log.e("MealRepo", "Failed to fetch meals: ${response.message()}")
                null
            }
        } catch (e: Exception) {
            Log.e("MealRepo", "Exception fetching meals", e)
            null
        }
    }

    suspend fun createMeal(meal: Meal, imageUri: Uri): Meal? {
        return try {
            val email = sessionManager.userEmail.first() ?: return null
            val userIdValue = userRepository.getUserIdByEmail(email) ?: return null

            val titlePart = meal.title.toRequestBody("text/plain".toMediaTypeOrNull())
            val descriptionPart = meal.description.toRequestBody("text/plain".toMediaTypeOrNull())
            val ingredientsPart = Gson().toJson(meal.ingredients).toRequestBody("text/plain".toMediaTypeOrNull())
            val caloriesPart = meal.calories.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            val categoryPart = meal.category.toRequestBody("text/plain".toMediaTypeOrNull())
            val userIdPart = userIdValue.toRequestBody("text/plain".toMediaTypeOrNull())
            val imagePart = getImageMultipartPart(imageUri)
            if (imagePart == null) {
                Log.e("MealRepo", "Gagal membuat MultipartBody.Part dari imageUri")
                return null
            }

            val response = api.createMealWithImage(
                title = titlePart,
                description = descriptionPart,
                ingredients = ingredientsPart,
                calories = caloriesPart,
                category = categoryPart,
                userId = userIdPart,
                image = imagePart
            )

            if (response.isSuccessful && response.body()?.success == true) {
                Log.d("MealRepo", "Meal created successfully: ${response.body()?.data}")
                response.body()?.data
            } else {
                Log.e("MealRepo", "Failed to create meal: ${response.message()}, ${response.errorBody()?.string()}")
                null
            }
        } catch (e: Exception) {
            Log.e("MealRepo", "Exception creating meal", e)
            null
        }
    }


    suspend fun updateMeal(id: String, meal: Meal, imageUri: Uri?): Meal? {
        return try {
            val email = sessionManager.userEmail.first() ?: return null
            val userIdValue = userRepository.getUserIdByEmail(email) ?: return null

            val titlePart = meal.title.toRequestBody("text/plain".toMediaTypeOrNull())
            val descriptionPart = meal.description.toRequestBody("text/plain".toMediaTypeOrNull())
            val ingredientsPart = meal.ingredients.joinToString("\n").toRequestBody("text/plain".toMediaTypeOrNull())
            val caloriesPart = meal.calories.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            val categoryPart = meal.category.toRequestBody("text/plain".toMediaTypeOrNull())
            val userIdPart = userIdValue.toRequestBody("text/plain".toMediaTypeOrNull())

            // Hanya buat imagePart jika user memilih gambar baru (bukan URL Cloudinary)
            val imagePart = if (imageUri != null && imageUri.toString().startsWith("content://")) {
                getImageMultipartPart(imageUri)
            } else {
                null
            }

            val response = if (imagePart != null) {
                api.updateMealWithImage(
                    id, titlePart, descriptionPart, ingredientsPart,
                    caloriesPart, categoryPart, userIdPart, imagePart
                )
            } else {
                api.updateMealWithoutImage(
                    id, titlePart, descriptionPart, ingredientsPart,
                    caloriesPart, categoryPart, userIdPart
                )
            }

            if (response.isSuccessful && response.body()?.success == true) {
                Log.d("MealRepo", "Meal updated successfully: ${response.body()?.data}")
                response.body()?.data
            } else {
                Log.e("MealRepo", "Failed to update meal: ${response.message()}, ${response.errorBody()?.string()}")
                null
            }
        } catch (e: Exception) {
            Log.e("MealRepo", "Exception updating meal", e)
            null
        }
    }

    private fun getFileName(context: Context, uri: Uri): String {
        var name = "uploaded_image_${System.currentTimeMillis()}"
        val cursor = context.contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (nameIndex != -1) {
                    val displayName = it.getString(nameIndex)
                    if (displayName != null) name = displayName
                }
            }
        }
        return name
    }

    private fun getImageMultipartPart(uri: Uri): MultipartBody.Part? {
        return try {
            val fileName = getFileName(context, uri)
            val inputStream = context.contentResolver.openInputStream(uri) ?: run {
                Log.e("MealRepo", "Failed to open input stream for URI: $uri")
                return null
            }
            val requestBody = inputStream.readBytes().toRequestBody("image/*".toMediaTypeOrNull())
            inputStream.close()
            MultipartBody.Part.createFormData("meal", fileName, requestBody)
        } catch (e: Exception) {
            Log.e("MealRepo", "Error creating multipart for URI: $uri", e)
            null
        }
    }



    suspend fun deleteMeal(id: String): Boolean {
        return try {
            val response = api.deleteMeal(id)
            response.isSuccessful && response.body()?.success == true
        } catch (e: Exception) {
            Log.e("MealRepo", "Exception deleting meal", e)
            false
        }
    }

}
