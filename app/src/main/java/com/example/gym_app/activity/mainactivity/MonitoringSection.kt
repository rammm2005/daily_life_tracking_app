package com.example.gym_app.activity.mainactivity

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gym_app.R
import com.example.gym_app.model.DailyData
import com.example.gym_app.model.DailyTracker
import com.example.gym_app.repository.DailyTrackerRepository


@Composable
fun MonitoringSection() {
    val context = LocalContext.current
    val repo = remember { DailyTrackerRepository(context) }

    var totalCalories by remember { mutableStateOf(String()) }
    var totalSleep by remember { mutableStateOf(0) }
    var totalWorkout by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
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
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            MonitoringItem(R.drawable.monitor1, "$totalCalories", "Daily Calories")
            MonitoringItem(R.drawable.monitor2, "${totalSleep}h", "Sleep")
            MonitoringItem(R.drawable.monitor3, "${totalWorkout}x", "Total Workout")
        }
    }
}



@Composable
fun MonitoringItem(icon: Int,value: String, label: String ) {
    Column (horizontalAlignment = Alignment.CenterHorizontally) {
        Image(
            painter = painterResource(id=icon),
            contentDescription = label
        )
        Text(text = value, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(vertical = 4.dp))
        Text(text = label, color = Color.White, fontSize = 12.sp,
            textAlign = TextAlign.Center
        )

    }
}