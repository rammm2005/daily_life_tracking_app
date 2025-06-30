package com.example.gym_app.activity.dailytracking

import SessionManager
import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gym_app.R
import com.example.gym_app.model.DailyTracker
import kotlinx.coroutines.flow.first

@Composable
fun DailyTrackingCard(
    tracker: DailyTracker,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onShowClick: () -> Unit,
    context: Context
) {
    val sessionManager = remember { SessionManager(context) }
    var currentUserEmail by remember { mutableStateOf<String?>(null) }
    var menuExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        currentUserEmail = sessionManager.userEmail.first()
    }

    if (currentUserEmail == null) return

    val moodIcon = when (tracker.mood.lowercase()) {
        "happy" -> "😊"
        "sad" -> "😞"
        "good" -> "😏"
        "neutral" -> "😐"
        else -> "😐"
    }

    val moodText = when (tracker.mood.lowercase()) {
        "happy" -> "Senang"
        "good" -> "Bagus"
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
        colors = CardDefaults.cardColors(containerColor = colorResource(R.color.smoothMainColor))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(50),
                color = progressColor.copy(alpha = 0.3f),
                modifier = Modifier.size(50.dp)
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    Text(
                        text = initials,
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    tracker.date,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Mood: $moodText", fontSize = 14.sp, color = Color.White)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(moodIcon, fontSize = 18.sp)
                }
                Text("Berat: ${tracker.weightKg} kg", fontSize = 14.sp, color = Color.White)
                Text("Tidur: ${tracker.sleepHours} jam", fontSize = 14.sp, color = Color.White)
            }

            Box {
                IconButton(onClick = { menuExpanded = true }) {
                    Icon(Icons.Default.MoreVert, contentDescription = "Menu", tint = Color.White)
                }

                DropdownMenu(
                    expanded = menuExpanded,
                    onDismissRequest = { menuExpanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Lihat Detail") },
                        onClick = {
                            menuExpanded = false
                            onShowClick()
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Edit") },
                        onClick = {
                            menuExpanded = false
                            onEditClick()
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Hapus") },
                        onClick = {
                            menuExpanded = false
                            onDeleteClick()
                        }
                    )
                }
            }
        }
    }
}
