package com.example.gym_app.activity.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.gym_app.R

data class NotificationSetting(
    val title: String,
    val description: String,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(navController: NavController) {

    val smoothMainColor = colorResource(R.color.smoothMainColor)

    val initialSettings = listOf(
        NotificationSetting("Workout Reminders", "Get reminders for your scheduled workouts"),
        NotificationSetting("Promotions", "Receive special offers and promotions"),
        NotificationSetting("News & Updates", "Stay up-to-date with the latest news"),
        NotificationSetting("Goal Progress", "Track and receive updates on your progress")
    )

    var globalNotificationsEnabled by remember { mutableStateOf(true) }

    val itemStates = remember {
        mutableStateListOf(true, false, true, true)
    }

    Scaffold(
        containerColor = smoothMainColor,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Notifications",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = smoothMainColor
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize()
                .background(smoothMainColor)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Enable Notifications",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    Text(
                        text = "Turn all notifications on or off",
                        color = Color.LightGray,
                        fontSize = 14.sp
                    )
                }
                Switch(
                    checked = globalNotificationsEnabled,
                    onCheckedChange = { globalNotificationsEnabled = it }
                )
            }

            Divider(color = Color.Gray, thickness = 1.dp, modifier = Modifier.padding(vertical = 8.dp))

            LazyColumn {
                itemsIndexed(initialSettings) { index, setting ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = setting.title,
                                color = Color.White,
                                fontWeight = FontWeight.Medium,
                                fontSize = 16.sp
                            )
                            Text(
                                text = setting.description,
                                color = Color.LightGray,
                                fontSize = 13.sp
                            )
                        }
                        Switch(
                            checked = itemStates[index] && globalNotificationsEnabled,
                            onCheckedChange = {
                                itemStates[index] = it
                            },
                            enabled = globalNotificationsEnabled
                        )
                    }
                    Divider(color = Color.Gray.copy(alpha = 0.5f), thickness = 0.5.dp)
                }
            }
        }
    }
}
