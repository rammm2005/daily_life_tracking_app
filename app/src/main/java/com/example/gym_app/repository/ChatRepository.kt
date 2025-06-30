package com.example.gym_app.repository

import SessionManager
import android.content.Context
import android.util.Log
import com.example.gym_app.model.ChatSession
import com.example.gym_app.network.ApiService
import com.example.gym_app.network.ChatRequest
import com.example.gym_app.network.DeleteMessagesRequest
import com.example.gym_app.network.RetrofitClient
import kotlinx.coroutines.flow.first

class ChatRepository(private val context: Context) {

    private val api: ApiService = RetrofitClient.instance
    private val sessionManager = SessionManager(context)
    private val userRepository = UserRepository()


    suspend fun getSuggestionsOnly(): List<String> {
        return try {
            val email = sessionManager.userEmail.first() ?: return emptyList()
            val userId = userRepository.getUserIdByEmail(email) ?: return emptyList()

            val response = api.sendMessage(ChatRequest(userId = userId, message = ""))
            if (response.isSuccessful) {
                response.body()?.suggestions ?: emptyList()
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            Log.e("ChatRepository", "Exception ambil suggestions", e)
            emptyList()
        }
    }


    suspend fun getAllChatSessions(): List<ChatSession> {
        return try {
            val email = sessionManager.userEmail.first() ?: return emptyList()
            val userId = userRepository.getUserIdByEmail(email) ?: return emptyList()

            val response = api.getChatSessions(userId)
            if (response.isSuccessful) {
                response.body() ?: emptyList()
            } else {
                Log.e("ChatRepository", "Gagal ambil sesi: ${response.code()}")
                emptyList()
            }
        } catch (e: Exception) {
            Log.e("ChatRepository", "Exception ambil sesi", e)
            emptyList()
        }
    }

    suspend fun sendMessage(message: String): Pair<String?, List<String>> {
        return try {
            val email = sessionManager.userEmail.first() ?: return Pair(null, emptyList())
            val userId = userRepository.getUserIdByEmail(email) ?: return Pair(null, emptyList())

            val request = ChatRequest(userId = userId, message = message)
            val response = api.sendMessage(request)

            if (response.isSuccessful) {
                val body = response.body()
                Pair(body?.reply, body?.suggestions ?: emptyList())
            } else {
                Log.e("ChatRepository", "Gagal kirim pesan: ${response.code()} ${response.message()}")
                Pair(null, emptyList())
            }
        } catch (e: Exception) {
            Log.e("ChatRepository", "Exception kirim pesan", e)
            Pair(null, emptyList())
        }
    }

    suspend fun deleteMessages(messageIds: List<String>): Boolean {
        return try {
            val email = sessionManager.userEmail.first() ?: return false
            val userId = userRepository.getUserIdByEmail(email) ?: return false

            val request = DeleteMessagesRequest(userId = userId, messageIds = messageIds)
            val response = api.deleteMessages(request)

            response.isSuccessful && (response.body()?.deletedCount ?: 0) > 0
        } catch (e: Exception) {
            Log.e("ChatRepository", "Exception hapus pesan", e)
            false
        }
    }
}
