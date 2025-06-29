package com.example.gym_app.activity.dailytracking


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Fireplace
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.gym_app.model.DailyTracker
import com.example.gym_app.model.Goal
import com.example.gym_app.model.Meal
import com.example.gym_app.model.Workout
import com.example.gym_app.repository.DailyTrackerRepository
import com.example.gym_app.repository.GoalRepository
import com.example.gym_app.repository.MealRepository
import com.example.gym_app.repository.WorkoutRepository
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun CreateUpdateDailyTrackingScreen(
    navController: NavController,
    trackerId: String? = null
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val repo = remember { DailyTrackerRepository(context) }
    val workoutRepo = remember { WorkoutRepository(context) }
    val mealRepo = remember { MealRepository(context) }
    val goalsRepo = remember { GoalRepository(context) }

    val isNew = trackerId == null
    var dailyTracker by remember { mutableStateOf<DailyTracker?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var isProcessing by remember { mutableStateOf(false) }

    var dateTime by remember { mutableStateOf(Calendar.getInstance()) }
    var weight by remember { mutableStateOf("") }
    var water by remember { mutableStateOf("") }
    var sleep by remember { mutableStateOf("") }
    var mood by remember { mutableStateOf("") }
    var stepCount by remember { mutableStateOf("") }
    var stressLevel by remember { mutableStateOf("") }

    var weightError by remember { mutableStateOf(false) }
    var waterError by remember { mutableStateOf(false) }
    var sleepError by remember { mutableStateOf(false) }
    var caloriesBurned by remember { mutableStateOf("") }
    var caloriesConsumed by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    var workoutCompletedIds by remember { mutableStateOf<List<String>>(emptyList()) }
    var mealsEatenIds by remember { mutableStateOf<List<String>>(emptyList()) }

    var userGoals by remember { mutableStateOf<List<Goal>>(emptyList()) }
    var goalCheckins by remember { mutableStateOf<List<String>>(emptyList()) }

    var workoutOptions by remember { mutableStateOf<List<Workout>>(emptyList()) }
    var mealOptions by remember { mutableStateOf<List<Meal>>(emptyList()) }

    LaunchedEffect(trackerId) {
        isLoading = true
        try {
            workoutOptions = workoutRepo.getAllWorkouts() ?: emptyList()
            mealOptions = mealRepo.getAllMeals() ?: emptyList()

            if (!isNew) {
                val result = repo.getTrackerById(trackerId)
                if (result != null) {
                    dailyTracker = result
                    val parts = result.date.split(" ")
                    if (parts.size == 2) {
                        val (d, t) = parts
                        val cal = Calendar.getInstance()
                        val dateParts = d.split("-").map { it.toInt() }
                        val timeParts = t.split(":").map { it.toInt() }
                        cal.set(dateParts[0], dateParts[1] - 1, dateParts[2], timeParts[0], timeParts[1])
                        dateTime = cal
                    }
                    weight = result.weightKg.toString()
                    water = result.waterIntakeLiters.toString()
                    sleep = result.sleepHours.toString()
                    mood = result.mood
                    notes = result.notes ?: ""
                    workoutCompletedIds = result.workoutCompletedIds
                    mealsEatenIds = result.mealsEatenIds
                    stepCount = result.stepCount?.toString() ?: ""
                    stressLevel = result.stressLevel?.toString() ?: ""
                    caloriesConsumed = result.caloriesConsumed?.toString() ?: ""
                    caloriesBurned = result.caloriesBurned?.toString() ?: ""
                    goalCheckins = result.goalCheckins
                }
            }

            userGoals = goalsRepo.getAllGoals() ?: emptyList()

        } finally {
            isLoading = false
        }
    }

    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp, bottom = 10.dp)
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
            }
            Text(
                text = if (isNew) "Buat Tracker Harian" else "Edit Tracker Harian",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
            )
        }

        DatePickerRow(
            label = "Tanggal",
            date = dateTime,
            onDateChange = { newDate -> dateTime = newDate },
            mainColor = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(12.dp))

        TimePickerRow(
            label = "Waktu",
            time = dateTime,
            onTimeChange = {
                dateTime.set(Calendar.HOUR_OF_DAY, it.get(Calendar.HOUR_OF_DAY))
                dateTime.set(Calendar.MINUTE, it.get(Calendar.MINUTE))
            },
            mainColor = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(12.dp))

        FilledTextField(weight, "Berat (kg)", weightError, "Berat tidak boleh kosong") {
            weight = it
            weightError = it.isBlank()
        }

        Spacer(modifier = Modifier.height(12.dp))

        FilledTextField(water, "Air Minum (liter)", waterError, "Air minum tidak boleh kosong") {
            water = it
            waterError = it.isBlank()
        }

        Spacer(modifier = Modifier.height(12.dp))

        FilledTextField(sleep, "Tidur (jam)", sleepError, "Tidur tidak boleh kosong") {
            sleep = it
            sleepError = it.isBlank()
        }

        Spacer(modifier = Modifier.height(12.dp))
        FilledTextField(mood, "Mood", false, null) { mood = it }
        Spacer(modifier = Modifier.height(12.dp))
        FilledTextField(notes, "Catatan", false, null) { notes = it }
        Spacer(modifier = Modifier.height(12.dp))
        FilledTextField(caloriesConsumed, "Kalori Masuk", false, null) { caloriesConsumed = it }
        Spacer(modifier = Modifier.height(12.dp))

        FilledTextField(caloriesBurned, "Kalori Terbakar", false, null) { caloriesBurned = it }
        Spacer(modifier = Modifier.height(12.dp))

        FilledTextField(stepCount, "Jumlah Langkah", false, null) { stepCount = it }
        Spacer(modifier = Modifier.height(12.dp))

        FilledTextField(stressLevel, "Tingkat Stres", false, null) { stressLevel = it }
        Spacer(modifier = Modifier.height(12.dp))


        Spacer(modifier = Modifier.height(24.dp))

        Text("Workout yang Selesai", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))
        workoutOptions.forEach { option ->
            val isChecked = workoutCompletedIds.contains(option._id)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .clickable {
                        option._id?.let { id ->
                            workoutCompletedIds = if (isChecked) {
                                workoutCompletedIds - id
                            } else {
                                workoutCompletedIds + id
                            }
                        }
                    },
                colors = CardDefaults.cardColors(
                    containerColor = if (isChecked) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Row(
                    Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Outlined.Fireplace, contentDescription = null)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text(option.title, fontWeight = FontWeight.Medium)
                        Text("${option.kcal} kcal", style = MaterialTheme.typography.bodySmall)
                    }
                    if (isChecked) Icon(Icons.Default.CheckCircle, contentDescription = null)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("Makanan yang Dimakan", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))
        mealOptions.forEach { option ->
            val isChecked = mealsEatenIds.contains(option._id)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .clickable {
                        option._id?.let { id ->
                            mealsEatenIds = if (isChecked) mealsEatenIds - id else mealsEatenIds + id
                        }
                    },
                colors = CardDefaults.cardColors(
                    containerColor = if (isChecked) MaterialTheme.colorScheme.tertiaryContainer else MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Row(
                    Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Outlined.Restaurant, contentDescription = null)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text(option.title, fontWeight = FontWeight.Medium)
                        Text("${option.calories} kcal", style = MaterialTheme.typography.bodySmall)
                    }
                    if (isChecked) Icon(Icons.Default.CheckCircle, contentDescription = null)
                }
            }
        }

        if (userGoals.isNotEmpty()) {
            Spacer(modifier = Modifier.height(24.dp))
            Text("Goals Harian", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            userGoals.forEach { goal ->
                val checked = goalCheckins.contains(goal._id)
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .clickable {
                            goal._id?.let { id ->
                                goalCheckins = if (goalCheckins.contains(id)) {
                                    goalCheckins - id
                                } else {
                                    goalCheckins + id
                                }
                            }
                        },
                    colors = CardDefaults.cardColors(
                        containerColor = if (checked) MaterialTheme.colorScheme.tertiaryContainer else MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Row(
                        Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (checked) Icons.Default.CheckCircle else Icons.Outlined.CheckCircle,
                            contentDescription = null,
                            tint = if (checked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) {
                            Text(goal.title, fontWeight = FontWeight.Medium)
                        }
                    }
                }
            }
        } else {
            Spacer(modifier = Modifier.height(24.dp))
            Text("Goals Kamu Kosong \uD83D\uDE13", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                "Anda belum memiliki Goal yang tersedia",
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                weightError = weight.isBlank()
                waterError = water.isBlank()
                sleepError = sleep.isBlank()

                if (weightError || waterError || sleepError) return@Button

                scope.launch {
                    isProcessing = true
                    val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(dateTime.time)
                    val time = SimpleDateFormat("HH:mm", Locale.getDefault()).format(dateTime.time)
                    val tracker = DailyTracker(
                        _id = dailyTracker?._id,
                        userId = dailyTracker?.userId ?: "",
                        date = "$date $time",
                        weightKg = weight.toDoubleOrNull() ?: 0.0,
                        waterIntakeLiters = water.toDoubleOrNull() ?: 0.0,
                        sleepHours = sleep.toDoubleOrNull() ?: 0.0,
                        mood = mood,
                        workoutCompletedIds = workoutCompletedIds,
                        mealsEatenIds = mealsEatenIds,
                        caloriesConsumed = dailyTracker?.caloriesConsumed,
                        caloriesBurned = dailyTracker?.caloriesBurned,
                        stepCount = dailyTracker?.stepCount,
                        stressLevel = dailyTracker?.stressLevel,
                        notes = notes,
                        goalCheckins = goalCheckins,
                        createdAt = dailyTracker?.createdAt,
                        updatedAt = dailyTracker?.updatedAt
                    )

                    val result = if (isNew) repo.createTracker(tracker) else repo.updateTracker(tracker._id!!, tracker)
                    if (result != null) navController.popBackStack()
                    isProcessing = false
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 25.dp),
            enabled = !isProcessing
        ) {
            if (isProcessing) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = Color.White,
                    strokeWidth = 2.dp
                )
            } else {
                Text(text = if (isNew) "Simpan" else "Perbarui")
            }
        }
    }
}

@Composable
fun FilledTextField(
    value: String,
    label: String,
    isError: Boolean,
    errorText: String?,
    onValueChange: (String) -> Unit
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        isError = isError,
        supportingText = { if (isError && errorText != null) Text(errorText) },
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            errorContainerColor = MaterialTheme.colorScheme.errorContainer
        )
    )
}