package com.example.gym_app.repository

import SessionManager
import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import com.example.gym_app.model.Tip
import com.example.gym_app.network.ApiService
import com.example.gym_app.network.RetrofitClient
import kotlinx.coroutines.flow.first
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

class TipRepository(private val context: Context) {

    private val api: ApiService = RetrofitClient.instance
    private val sessionManager = SessionManager(context)
    private val userRepository = UserRepository()



    suspend fun getAllTips(): List<Tip>? {
        return try {
            val response = api.getAllTips()
            if (response.success) {
                response.data
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }


    suspend fun getTips(): List<Tip>? {
        return try {
            val email = sessionManager.userEmail.first() ?: return null
            val userId = userRepository.getUserIdByEmail(email) ?: return null
            val response = api.getTipsByUserId(userId)
            if (response.isSuccessful && response.body()?.success == true) {
                response.body()?.data
            } else {
                Log.e("TipRepo", "Failed to fetch tips: ${response.code()} ${response.message()} ${response.errorBody()?.string()}")
                null
            }
        } catch (e: Exception) {
            Log.e("TipRepo", "Exception fetching tips", e)
            null
        }
    }

    suspend fun createTip(tip: Tip, imageUris: List<Uri>): Tip? {
        return try {
            val email = sessionManager.userEmail.first() ?: return null
            val userIdValue = userRepository.getUserIdByEmail(email) ?: return null

            val titlePart = tip.title.toRequestBody("text/plain".toMediaTypeOrNull())
            val contentPart = tip.content.toRequestBody("text/plain".toMediaTypeOrNull())
            val descriptionShortPart = (tip.description_short ?: "").toRequestBody("text/plain".toMediaTypeOrNull())
            val typePart = tip.type.toRequestBody("text/plain".toMediaTypeOrNull())
            val userIdPart = userIdValue.toRequestBody("text/plain".toMediaTypeOrNull())

            val imageParts = getImageMultipartParts(imageUris)

            Log.d("TipRepo", "Creating tip. Title: ${tip.title}, Content: ${tip.content}, DescShort: ${tip.description_short}, Type: ${tip.type}, UserID: $userIdValue, Image count: ${imageParts.size}")
            imageParts.forEachIndexed { index, part ->
                Log.d("TipRepo", "Image part $index: headers ${part.headers}, body size ${part.body.contentLength()}")
            }


            val response = api.createTipWithImage(
                title = titlePart,
                content = contentPart,
                descriptionShort = descriptionShortPart,
                type = typePart,
                userId = userIdPart,
                images = imageParts
            )

            if (response.isSuccessful && response.body()?.success == true) {
                Log.d("TipRepo", "Successfully created tip: ${response.body()?.data}")
                response.body()?.data
            } else {
                Log.e("TipRepo", "Failed to create tip. Code: ${response.code()}, Message: ${response.message()}, ErrorBody: ${response.errorBody()?.string()}")
                null
            }
        } catch (e: Exception) {
            Log.e("TipRepo", "Exception creating tip", e)
            null
        }
    }

    suspend fun updateTip(id: String, tip: Tip, imageUris: List<Uri>): Tip? {
        return try {
            val email = sessionManager.userEmail.first() ?: return null
            val userIdValue = userRepository.getUserIdByEmail(email) ?: return null

            val titlePart = tip.title.toRequestBody("text/plain".toMediaTypeOrNull())
            val contentPart = tip.content.toRequestBody("text/plain".toMediaTypeOrNull())
            val descriptionShortPart = (tip.description_short ?: "").toRequestBody("text/plain".toMediaTypeOrNull())
            val typePart = tip.type.toRequestBody("text/plain".toMediaTypeOrNull())
            val userIdPart = userIdValue.toRequestBody("text/plain".toMediaTypeOrNull())

            val imageParts = getImageMultipartParts(imageUris)

            val response = api.updateTipWithImage(
                id = id,
                title = titlePart,
                content = contentPart,
                descriptionShort = descriptionShortPart,
                type = typePart,
                userId = userIdPart,
                images = imageParts
            )

            if (response.isSuccessful && response.body()?.success == true) {
                response.body()?.data
            } else {
                Log.e("TipRepo", "Failed to update tip. Code: ${response.code()}, Message: ${response.message()}, ErrorBody: ${response.errorBody()?.string()}")
                null
            }
        } catch (e: Exception) {
            Log.e("TipRepo", "Exception updating tip with images", e)
            null
        }
    }

    suspend fun deleteTip(id: String): Boolean {
        return try {
            val response = api.deleteTip(id)
            if (response.isSuccessful && response.body()?.success == true) {
                true
            } else {
                Log.e("TipRepo", "Failed to delete tip: ${response.code()} ${response.message()} ${response.errorBody()?.string()}")
                false
            }
        } catch (e: Exception) {
            Log.e("TipRepo", "Exception deleting tip", e)
            false
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


    private fun getImageMultipartParts(imageUris: List<Uri>): List<MultipartBody.Part> {
        return imageUris.mapNotNull { uri ->
            try {
                val fileName = getFileName(context, uri)
                // Log.d("TipRepo", "Processing URI: $uri, FileName: $fileName")
                val inputStream = context.contentResolver.openInputStream(uri) ?: run {
                    // Log.e("TipRepo", "Failed to open input stream for URI: $uri")
                    return@mapNotNull null
                }
                val requestBody = inputStream.readBytes().toRequestBody("image/*".toMediaTypeOrNull())
                inputStream.close()
                MultipartBody.Part.createFormData("images", fileName, requestBody)
            } catch (e: Exception) {
                Log.e("TipRepo", "Error creating multipart for URI: $uri", e)
                null
            }
        }
    }
}