package com.example.gym_app.component

import android.text.Layout
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun IconWithTooltip(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    description: String,
    tint: Color = Color.White,
    onClick: () -> Unit
) {
    var showTooltip by remember { mutableStateOf(false) }

    Box(
        contentAlignment = Alignment.Center
    ) {
        IconButton(
            onClick = {
                onClick()
                showTooltip = true
            },
            modifier = Modifier
        ) {
            Icon(
                imageVector = icon,
                contentDescription = description,
                tint = tint
            )
        }

        if (showTooltip) {
            Column (
                modifier = Modifier
                    .background(Color.Black.copy(alpha = 0.7f), shape = RoundedCornerShape(4.dp))
                    .padding(4.dp)
                    .align(Alignment.TopCenter)
            ) {
                Text(
                    text = description,
                    color = Color.White,
                    fontSize = 12.sp
                )
            }

            LaunchedEffect(Unit) {
                kotlinx.coroutines.delay(1500)
                showTooltip = false
            }
        }
    }
}
