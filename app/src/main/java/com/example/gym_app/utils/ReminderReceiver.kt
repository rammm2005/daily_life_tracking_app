package com.example.gym_app.utils


import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

object ReminderEventBus {
    private val _reminderMessage = MutableLiveData<String>()
    val reminderMessage: LiveData<String> = _reminderMessage

    fun postMessage(message: String) {
        _reminderMessage.postValue(message)
    }
}

class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val title = intent.getStringExtra("title") ?: "Pengingat"
        val message = intent.getStringExtra("message") ?: ""
        ReminderEventBus.postMessage("$title: $message")
    }
}
