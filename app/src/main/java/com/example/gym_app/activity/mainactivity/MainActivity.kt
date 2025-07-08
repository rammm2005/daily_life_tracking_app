package com.example.gym_app.activity.mainactivity

import SessionManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.example.gym_app.repository.ReminderRepository
import com.example.gym_app.screen.AppNavHost
import com.example.gym_app.ui.theme.Gym_appTheme
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

class MainActivity : ComponentActivity() {
    private val sessionManager by lazy { SessionManager(this) }
    private val reminderRepository by lazy { ReminderRepository(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val snackbarHostState = remember { SnackbarHostState() }

            Gym_appTheme {
                AppNavHost(sessionManager = sessionManager)

                SnackbarHost(
                    hostState = snackbarHostState,
                    modifier = Modifier.padding(16.dp)
                )
            }

            LaunchedEffect(Unit) {
                val reminders = reminderRepository.getAllReminders() ?: emptyList()

                val today = LocalDate.now()
                val todayDay = today.dayOfWeek.getDisplayName(TextStyle.FULL, Locale("id"))
                val todayDate = today.dayOfMonth

                val messages = listOf(
                    "📌 Jangan lupa: %s hari ini ya!",
                    "🔔 Waktunya: %s",
                    "✅ Reminder: %s untuk hari ini",
                    "🚀 Ayo kerjakan: %s",
                    "📝 Catatan penting: %s"
                )

                reminders.filter {
                    it.status == "active" && (
                            it.repeat == "daily" ||
                                    (it.repeat == "weekly" && it.days.contains(todayDay)) ||
                                    (it.repeat == "monthly" && it.schedule.split("-")[2].split(" ")[0].toInt() == todayDate)
                            )
                }.forEach { reminder ->
                    val template = messages.random()
                    val message = String.format(template, reminder.title)
                    snackbarHostState.showSnackbar(message)
                }
            }
        }
    }
}
