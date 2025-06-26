package com.example.gym_app.activity.schedule

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Row
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Share
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.example.gym_app.model.Reminder


@Composable
fun ReminderActions(
    reminder: Reminder,
) {
    val context = LocalContext.current

    val shareText = buildString {
        append("📌 *${reminder.title}*\n")
        append("📅 Jadwal: ${reminder.schedule}\n")
        append("🔁 Ulangi: ${reminder.repeat}\n")
        append("📖 Deskripsi: ${reminder.description}\n")
        append("📍 Hari: ${reminder.days.joinToString()}\n")
        append("💡 Status: ${reminder.status}")
    }

    Row {
        IconButton(
            onClick = {
                val intent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, shareText)
                }
                val chooser = Intent.createChooser(intent, "Bagikan Reminder via...")
                context.startActivity(chooser)
            }
        ) {
            Icon(
                imageVector = Icons.Default.Share,
                contentDescription = "Share",
                tint = Color(0xFF2196F3)
            )
        }
    }
}
