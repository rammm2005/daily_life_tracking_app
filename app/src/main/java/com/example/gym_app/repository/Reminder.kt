package com.example.gym_app.repository

import SessionManager
import android.content.Context
import android.util.Log
import com.example.gym_app.model.Reminder
import com.example.gym_app.network.ApiService
import com.example.gym_app.network.RetrofitClient
import com.google.gson.Gson
import kotlinx.coroutines.flow.first

class ReminderRepository(private val context: Context) {

    private val api: ApiService = RetrofitClient.instance
    private val sessionManager = SessionManager(context)
    private val userRepository = UserRepository()

    suspend fun getAllReminders(): List<Reminder>? {
        return try {
            val email = sessionManager.userEmail.first() ?: return null
            val userId = userRepository.getUserIdByEmail(email) ?: return null
            val response = api.getRemindersByUserId(userId)
            if (response.isSuccessful && response.body()?.success == true) {
                response.body()?.data
            } else {
                Log.e("ReminderRepo", "Gagal ambil reminder: ${response.code()} ${response.message()} ${response.errorBody()?.string()}")
                null
            }
        } catch (e: Exception) {
            Log.e("ReminderRepo", "Exception ambil reminder", e)
            null
        }
    }

    suspend fun getReminderById(id: String): Reminder? {
        return try {
            val response = api.getReminderById(id)
            if (response.isSuccessful && response.body()?.success == true) {
                response.body()?.data
            } else {
                Log.e("ReminderRepo", "Gagal ambil reminder ID $id: ${response.code()} ${response.message()} ${response.errorBody()?.string()}")
                null
            }
        } catch (e: Exception) {
            Log.e("ReminderRepo", "Exception ambil reminder ID $id", e)
            null
        }
    }

    suspend fun createReminder(reminder: Reminder): Reminder? {
        return try {
            val email = sessionManager.userEmail.first() ?: return null
            val userId = userRepository.getUserIdByEmail(email) ?: return null

            val methodStr = Gson().toJson(reminder.method)
            val daysStr = Gson().toJson(reminder.days)

            val response = api.createReminder(
                userId = userId,
                type = reminder.type,
                title = reminder.title,
                schedule = reminder.schedule,
                description = reminder.description,
                method = methodStr,
                days = daysStr,
                status = reminder.status,
                repeat = reminder.repeat
            )

            if (response.isSuccessful && response.body()?.success == true) {
                response.body()?.data
            } else {
                Log.e("ReminderRepo", "Gagal membuat reminder: ${response.code()} ${response.message()} ${response.errorBody()?.string()}")
                null
            }
        } catch (e: Exception) {
            Log.e("ReminderRepo", "Exception membuat reminder", e)
            null
        }
    }

    suspend fun updateReminder(id: String, reminder: Reminder): Reminder? {
        return try {
            val methodStr = Gson().toJson(reminder.method)
            val daysStr = Gson().toJson(reminder.days)

            val response = api.updateReminder(
                id = id,
                type = reminder.type,
                title = reminder.title,
                schedule = reminder.schedule,
                description = reminder.description,
                method = methodStr,
                days = daysStr,
                status = reminder.status,
                repeat = reminder.repeat
            )

            if (response.isSuccessful && response.body()?.success == true) {
                response.body()?.data
            } else {
                Log.e("ReminderRepo", "Gagal update reminder $id: ${response.code()} ${response.message()} ${response.errorBody()?.string()}")
                null
            }
        } catch (e: Exception) {
            Log.e("ReminderRepo", "Exception update reminder $id", e)
            null
        }
    }

    suspend fun deleteReminder(id: String): Boolean {
        return try {
            val response = api.deleteReminder(id)
            if (response.isSuccessful && response.body()?.success == true) {
                true
            } else {
                Log.e("ReminderRepo", "Gagal hapus reminder $id: ${response.code()} ${response.message()} ${response.errorBody()?.string()}")
                false
            }
        } catch (e: Exception) {
            Log.e("ReminderRepo", "Exception hapus reminder $id", e)
            false
        }
    }
}
