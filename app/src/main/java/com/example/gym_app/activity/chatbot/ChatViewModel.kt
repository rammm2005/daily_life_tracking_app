package com.example.gym_app.activity.chatbot

import SessionManager
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gym_app.model.ChatSession
import com.example.gym_app.model.Message
import com.example.gym_app.repository.ChatRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class ChatViewModel(context: Context) : ViewModel() {

    private val repository = ChatRepository(context)
    private val sessionManager = SessionManager(context)

    private val _email = MutableStateFlow<String?>(null)
    val email: StateFlow<String?> = _email

    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> = _messages

    private val _input = MutableStateFlow("")
    val input: StateFlow<String> = _input

    private val _isThinking = MutableStateFlow(false)
    val isThinking: StateFlow<Boolean> = _isThinking

    private val _username = MutableStateFlow("Saya")
    val username: StateFlow<String> = _username

    private val _suggestions = MutableStateFlow<List<String>>(emptyList())
    val suggestions: StateFlow<List<String>> = _suggestions

    private val _chatSessions = MutableStateFlow<List<ChatSession>>(emptyList())
    val chatSessions: StateFlow<List<ChatSession>> = _chatSessions

    init {
        viewModelScope.launch {
            _email.value = sessionManager.userEmail.first()
            _suggestions.value = repository.getSuggestionsOnly()
        }
    }

    fun loadChatSessions() {
        viewModelScope.launch {
            _chatSessions.value = repository.getAllChatSessions()
        }
    }

    fun loadMessagesBySession(sessionId: String?) {
        if (sessionId == null) return

        viewModelScope.launch {
            val sessionMessages = repository.getMessagesBySessionId(sessionId)
            _messages.value = sessionMessages
        }
    }

    fun onInputChange(newText: String) {
        _input.value = newText
    }

    fun sendMessage() {
        val userInput = input.value.trim()
        if (userInput.isBlank()) return

        val newUserMessage = Message("", "", "", "user", userInput)
        _messages.value = _messages.value + newUserMessage
        _input.value = ""
        _isThinking.value = true

        viewModelScope.launch {
            val (reply, newSuggestions) = repository.sendMessage(userInput)
            if (reply != null) {
                val botMessage = Message("", "", "", "bot", reply)
                _messages.value = _messages.value + botMessage
                _suggestions.value = newSuggestions
            }
            _isThinking.value = false
        }
    }
}

