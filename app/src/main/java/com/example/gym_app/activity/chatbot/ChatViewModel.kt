package com.example.gym_app.activity.chatbot

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gym_app.model.Message
import com.example.gym_app.repository.ChatRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ChatViewModel(context: Context) : ViewModel() {

    private val repository = ChatRepository(context)

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

    init {
        viewModelScope.launch {
            _suggestions.value = repository.getSuggestionsOnly()
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
