package com.example.gym_app.activity.dailytracking


import SessionManager
import android.content.Context
import androidx.compose.foundation.layout.*

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.example.gym_app.model.DailyTracker
import kotlinx.coroutines.flow.first


@Composable
fun DailyTrackingCard(tracker: DailyTracker, onEditClick: () -> Unit, context: Context) {
    val sessionManager = remember { SessionManager(context) }
    val scope = rememberCoroutineScope()

    var currentUserEmail by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        currentUserEmail = sessionManager.userEmail.first()
    }

    if (currentUserEmail == null) return

    val moodIcon = when (tracker.mood.lowercase()) {
        "happy" -> "😊"
        "sad" -> "😞"
        "Good" -> "😏"
        "neutral" -> "😐"
        else -> "😐"
    }

    val moodText = when (tracker.mood.lowercase()) {
        "happy" -> "Senang"
        "Good" -> "Bagus"
        "sad" -> "Sedih"
        "neutral" -> "Biasa"
        else -> "Biasa"
    }

    val progressColor = when (tracker.mood.lowercase()) {
        "happy" -> Color(0xFF3DDC84)
        "sad" -> Color(0xFFEF5350)
        "neutral" -> Color(0xFFFFCA28)
        else -> Color.LightGray
    }

    val initials = currentUserEmail
        ?.substringBefore("@")
        ?.take(2)
        ?.uppercase() ?: "US"

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = progressColor.copy(alpha = 0.08f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = moodIcon,
                fontSize = 28.sp
            )

            Spacer(modifier = Modifier.width(12.dp))

            Surface(
                shape = RoundedCornerShape(50),
                color = progressColor.copy(alpha = 0.3f),
                modifier = Modifier.size(50.dp)
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    Text(
                        text = initials,
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = Color.Black,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(tracker.date, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Text("Mood: $moodText", fontSize = 14.sp)
                Text("Berat: ${tracker.weightKg} kg", fontSize = 14.sp)
                Text("Tidur: ${tracker.sleepHours} jam", fontSize = 14.sp)
            }

            Spacer(modifier = Modifier.width(12.dp))

            Button(
                onClick = onEditClick,
                colors = ButtonDefaults.buttonColors(containerColor = progressColor),
                modifier = Modifier.height(36.dp)
            ) {
                Text("Edit", color = Color.White, fontSize = 12.sp)
            }
        }
    }
}


