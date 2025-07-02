package com.example.gym_app.activity.mainactivity

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gym_app.R
import com.example.gym_app.repository.DailyTrackerRepository
import kotlinx.coroutines.delay

@Composable
fun MonitoringSection() {
    val context = LocalContext.current
    val repo = remember { DailyTrackerRepository(context) }

    var totalCalories by remember { mutableStateOf<String?>(null) }
    var totalSleep by remember { mutableStateOf<Int?>(null) }
    var totalWorkout by remember { mutableStateOf<Int?>(null) }

    LaunchedEffect(Unit) {
        delay(1000)
        val summary = repo.getDailySummary()
        summary?.let {
            totalCalories = it.totalCalories
            totalSleep = it.totalSleep
            totalWorkout = it.totalWorkout
        }
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = "Daily Monitoring",
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 16.dp)
        )
        Spacer(Modifier.height(16.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            if (totalCalories == null || totalSleep == null || totalWorkout == null) {
                repeat(3) {
                    MonitoringSkeleton()
                }
            } else {
                MonitoringItem(R.drawable.monitor1, "$totalCalories", "Daily Calories")
                MonitoringItem(R.drawable.monitor2, "${totalSleep}h", "Sleep")
                MonitoringItem(R.drawable.monitor3, "${totalWorkout}x", "Total Workout")
            }
        }
    }
}

@Composable
fun MonitoringItem(icon: Int, value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Image(
            painter = painterResource(id = icon),
            contentDescription = label
        )
        Text(
            text = value,
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 4.dp)
        )
        Text(
            text = label,
            color = Color.White,
            fontSize = 12.sp,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun MonitoringSkeleton() {
    Card(
        modifier = Modifier
            .width(100.dp)
            .height(100.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF2C2C2E))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(Color.Gray.copy(alpha = 0.3f), RoundedCornerShape(50))
            )
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .height(14.dp)
                    .width(60.dp)
                    .background(Color.Gray.copy(alpha = 0.3f), RoundedCornerShape(4.dp))
            )
            Spacer(modifier = Modifier.height(4.dp))
            Box(
                modifier = Modifier
                    .height(10.dp)
                    .width(80.dp)
                    .background(Color.Gray.copy(alpha = 0.3f), RoundedCornerShape(4.dp))
            )
        }
    }
}
