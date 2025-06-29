package com.example.gym_app.activity.dailytracking


import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Fireplace
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.gym_app.model.DailyTracker
import com.example.gym_app.model.Meal
import com.example.gym_app.model.Workout
import com.example.gym_app.repository.DailyTrackerRepository
import com.example.gym_app.repository.MealRepository
import com.example.gym_app.repository.WorkoutRepository
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun DatePickerRow(
    label: String,
    date: Calendar,
    onDateChange: (Calendar) -> Unit,
    mainColor: Color,
    enabled: Boolean = true
) {
    val context = LocalContext.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = enabled) {
                if (enabled) {
                    val datePickerDialog = DatePickerDialog(
                        context,
                        { _, year, month, dayOfMonth ->
                            val hour = date.get(Calendar.HOUR_OF_DAY)
                            val minute = date.get(Calendar.MINUTE)

                            val newDate = Calendar.getInstance().apply {
                                clear()
                                set(year, month, dayOfMonth, hour, minute)
                            }
                            onDateChange(newDate)
                        },
                        date.get(Calendar.YEAR),
                        date.get(Calendar.MONTH),
                        date.get(Calendar.DAY_OF_MONTH)
                    )
                    datePickerDialog.show()
                }
            }
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            color = if (enabled) Color(0xFF374151) else Color(0xFF9CA3AF)
        )

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = SimpleDateFormat("EEE, MMM dd, yyyy", Locale.getDefault()).format(date.time),
                fontSize = 14.sp,
                color = if (enabled) mainColor else Color(0xFF9CA3AF),
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = null,
                tint = if (enabled) Color(0xFF9CA3AF) else Color(0xFFD1D5DB),
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
fun TimePickerRow(
    label: String,
    time: Calendar,
    onTimeChange: (Calendar) -> Unit,
    mainColor: Color,
    enabled: Boolean = true
) {
    val context = LocalContext.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = enabled) {
                if (enabled) {
                    val timePickerDialog = TimePickerDialog(
                        context,
                        { _, hourOfDay, minute ->
                            val newTime = Calendar.getInstance().apply {
                                set(Calendar.HOUR_OF_DAY, hourOfDay)
                                set(Calendar.MINUTE, minute)
                            }
                            onTimeChange(newTime)
                        },
                        time.get(Calendar.HOUR_OF_DAY),
                        time.get(Calendar.MINUTE),
                        false
                    )
                    timePickerDialog.show()
                }
            }
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            color = if (enabled) Color(0xFF374151) else Color(0xFF9CA3AF)
        )

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(time.time),
                fontSize = 14.sp,
                color = if (enabled) mainColor else Color(0xFF9CA3AF),
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = null,
                tint = if (enabled) Color(0xFF9CA3AF) else Color(0xFFD1D5DB),
                modifier = Modifier.size(16.dp)
            )
        }
    }
}
