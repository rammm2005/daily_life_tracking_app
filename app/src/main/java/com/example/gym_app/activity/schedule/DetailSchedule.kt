package com.example.gym_app.activity.schedule

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.gym_app.R
import com.example.gym_app.model.Reminder
import com.example.gym_app.repository.ReminderRepository
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

// Helper functions to parse nested JSON strings
fun parseNestedJsonArray(jsonString: String): List<String> {
    return try {
        val gson = Gson()

        // Remove outer array brackets and quotes if present
        var cleanString = jsonString.trim()
        if (cleanString.startsWith("[") && cleanString.endsWith("]")) {
            cleanString = cleanString.substring(1, cleanString.length - 1)
        }
        if (cleanString.startsWith("\"") && cleanString.endsWith("\"")) {
            cleanString = cleanString.substring(1, cleanString.length - 1)
        }

        // Handle multiple levels of escaping
        var unescaped = cleanString
        var previousLength = 0

        // Keep unescaping until no more changes occur
        while (unescaped.length != previousLength) {
            previousLength = unescaped.length
            unescaped = unescaped
                .replace("\\\\\\\"", "\"")
                .replace("\\\\\"", "\"")
                .replace("\\\"", "\"")
                .replace("\\\\", "\\")
        }

        // Try to parse as JSON array
        val type = object : TypeToken<List<String>>() {}.type
        gson.fromJson<List<String>>(unescaped, type) ?: emptyList()
    } catch (e: Exception) {
        // If parsing fails, try to extract individual items
        try {
            extractItemsFromComplexString(jsonString)
        } catch (e2: Exception) {
            listOf(jsonString) // Return original string as single item
        }
    }
}

fun extractItemsFromComplexString(complexString: String): List<String> {
    val items = mutableListOf<String>()

    // Common patterns to look for
    val patterns = listOf(
        "Push Notification",
        "Email",
        "In-App Alert",
        "SMS",
        "Monday",
        "Tuesday",
        "Wednesday",
        "Thursday",
        "Friday",
        "Saturday",
        "Sunday"
    )

    patterns.forEach { pattern ->
        if (complexString.contains(pattern, ignoreCase = true)) {
            if (!items.contains(pattern)) {
                items.add(pattern)
            }
        }
    }

    return items.ifEmpty { listOf(complexString) }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailSchedule(
    navController: NavController,
    scheduleId: String,
    onDismiss: () -> Unit = { navController.navigateUp() }
) {
    val context = LocalContext.current
    val reminderRepository = remember { ReminderRepository(context) }
    val coroutineScope = rememberCoroutineScope()
    val mainColor = colorResource(id = R.color.mainColor)

    var schedule by remember { mutableStateOf<Reminder?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var showBottomSheet by remember { mutableStateOf(true) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    val bottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    // Load schedule data
    LaunchedEffect(scheduleId) {
        coroutineScope.launch {
            isLoading = true
            schedule = reminderRepository.getReminderById(scheduleId)
            isLoading = false
        }
    }

    // Background overlay
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f))
            .clickable {
                showBottomSheet = false
                onDismiss()
            }
    )

    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = {
                showBottomSheet = false
                onDismiss()
            },
            sheetState = bottomSheetState,
            containerColor = Color.White,
            contentColor = Color.Black,
            dragHandle = {
                Surface(
                    modifier = Modifier.padding(vertical = 8.dp),
                    color = Color.Gray.copy(alpha = 0.3f),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Box(modifier = Modifier.size(width = 32.dp, height = 4.dp))
                }
            }
        ) {
            if (isLoading) {
                LoadingContent(mainColor)
            } else if (schedule != null) {
                DetailScheduleContent(
                    schedule = schedule!!,
                    mainColor = mainColor,
                    onEdit = {
                        navController.navigate("edit_schedule/$scheduleId")
                        onDismiss()
                    },
                    onDelete = { showDeleteDialog = true },
                    onStatusChange = { newStatus ->
                        coroutineScope.launch {
                            val updatedSchedule = schedule!!.copy(status = newStatus)
                            reminderRepository.updateReminder(scheduleId, updatedSchedule)
                            schedule = updatedSchedule
                        }
                    },
                    onClose = onDismiss
                )
            } else {
                ErrorContent(onClose = onDismiss)
            }
        }
    }

    // Delete confirmation dialog
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Schedule") },
            text = { Text("Are you sure you want to delete this schedule? This action cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        coroutineScope.launch {
                            reminderRepository.deleteReminder(scheduleId)
                            showDeleteDialog = false
                            onDismiss()
                        }
                    }
                ) {
                    Text("Delete", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun DetailScheduleContent(
    schedule: Reminder,
    mainColor: Color,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onStatusChange: (String) -> Unit,
    onClose: () -> Unit
) {
    // Parse the complex nested JSON strings
    val parsedMethods = remember(schedule.method) {
        schedule.method.flatMap { methodString ->
            parseNestedJsonArray(methodString)
        }.distinct()
    }

    val parsedDays = remember(schedule.days) {
        schedule.days.flatMap { dayString ->
            parseNestedJsonArray(dayString)
        }.distinct()
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header with actions
        item {
            DetailHeader(
                schedule = schedule,
                mainColor = mainColor,
                onEdit = onEdit,
                onDelete = onDelete,
                onClose = onClose
            )
        }

        // Status section
        item {
            StatusSection(
                currentStatus = schedule.status,
                onStatusChange = onStatusChange,
                mainColor = mainColor
            )
        }

        // Basic info section
        item {
            BasicInfoSection(schedule = schedule, mainColor = mainColor)
        }

        // Schedule details section
        item {
            ScheduleDetailsSection(schedule = schedule, mainColor = mainColor)
        }

        // Days section
        if (parsedDays.isNotEmpty()) {
            item {
                DaysSection(
                    parsedDays = parsedDays,
                    mainColor = mainColor
                )
            }
        }

        // Reminder methods section
        if (parsedMethods.isNotEmpty()) {
            item {
                ReminderMethodsSection(
                    parsedMethods = parsedMethods,
                    mainColor = mainColor
                )
            }
        }

        // Description section
        if (schedule.description.isNotEmpty()) {
            item {
                DescriptionSection(schedule = schedule, mainColor = mainColor)
            }
        }

        // Metadata section
        item {
            MetadataSection(schedule = schedule, mainColor = mainColor)
        }

        // Bottom spacing
        item {
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun DetailHeader(
    schedule: Reminder,
    mainColor: Color,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onClose: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onClose) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Close",
                tint = Color.Gray
            )
        }

        Text(
            text = "Schedule Details",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1F2937)
        )

        ReminderActions(
            reminder = schedule,
        )
    }
}

@Composable
fun StatusSection(
    currentStatus: String,
    onStatusChange: (String) -> Unit,
    mainColor: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFAFAFA)),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Status",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF374151),
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatusChip(
                    status = "active",
                    isSelected = currentStatus == "active",
                    onClick = { onStatusChange("active") },
                    color = Color(0xFF10B981)
                )
                StatusChip(
                    status = "paused",
                    isSelected = currentStatus == "paused",
                    onClick = { onStatusChange("paused") },
                    color = Color(0xFFF59E0B)
                )
                StatusChip(
                    status = "done",
                    isSelected = currentStatus == "done",
                    onClick = { onStatusChange("done") },
                    color = Color(0xFF6B7280)
                )
            }
        }
    }
}

@Composable
fun StatusChip(
    status: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    color: Color
) {
    Surface(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .clickable { onClick() },
        color = if (isSelected) color else color.copy(alpha = 0.1f),
        contentColor = if (isSelected) Color.White else color
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(
                        if (isSelected) Color.White else color,
                        CircleShape
                    )
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = status.replaceFirstChar { it.uppercase() },
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun BasicInfoSection(schedule: Reminder, mainColor: Color) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Title
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Title,
                    contentDescription = "Title",
                    tint = mainColor,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = schedule.title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1F2937)
                )
            }

            // Type
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = getTypeIcon(schedule.type),
                    contentDescription = "Type",
                    tint = Color(0xFF6B7280),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Surface(
                    color = mainColor.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = schedule.type,
                        fontSize = 14.sp,
                        color = mainColor,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ScheduleDetailsSection(schedule: Reminder, mainColor: Color) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Schedule Details",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1F2937),
                modifier = Modifier.padding(bottom = 12.dp)
            )

            // Schedule time
            DetailRow(
                icon = Icons.Outlined.Schedule,
                label = "Time",
                value = schedule.schedule,
                mainColor = mainColor
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Repeat frequency
            DetailRow(
                icon = Icons.Outlined.Repeat,
                label = "Repeat",
                value = schedule.repeat.replaceFirstChar { it.uppercase() },
                mainColor = mainColor
            )
        }
    }
}

@Composable
fun DaysSection(
    parsedDays: List<String>,
    mainColor: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.CalendarMonth,
                    contentDescription = "Days",
                    tint = mainColor,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Active Days",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1F2937)
                )
            }

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(parsedDays) { day ->
                    EnhancedDayChip(
                        day = day,
                        mainColor = mainColor
                    )
                }
            }
        }
    }
}

@Composable
fun EnhancedDayChip(day: String, mainColor: Color) {
    val dayAbbr = day.take(3).uppercase()
    val isToday = isTodays(day)

    Surface(
        color = if (isToday) mainColor else mainColor.copy(alpha = 0.1f),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isToday) mainColor else mainColor.copy(alpha = 0.3f)
        )
    ) {
        Text(
            text = dayAbbr,
            fontSize = 12.sp,
            color = if (isToday) Color.White else mainColor,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}

@Composable
fun ReminderMethodsSection(
    parsedMethods: List<String>,
    mainColor: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Notifications,
                    contentDescription = "Reminder Methods",
                    tint = mainColor,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Reminder Methods",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1F2937)
                )
            }

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(parsedMethods) { method ->
                    EnhancedMethodChip(
                        method = method,
                        mainColor = mainColor
                    )
                }
            }
        }
    }
}

@Composable
fun EnhancedMethodChip(method: String, mainColor: Color) {
    val icon = when (method) {
        "Push Notification" -> Icons.Outlined.Notifications
        "Email" -> Icons.Outlined.Email
        "In-App Alert" -> Icons.Outlined.NotificationImportant
        "SMS" -> Icons.Outlined.Sms
        else -> Icons.Outlined.Notifications
    }

    Surface(
        color = mainColor.copy(alpha = 0.1f),
        shape = RoundedCornerShape(20.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            mainColor.copy(alpha = 0.3f)
        )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = method,
                tint = mainColor,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = method,
                fontSize = 12.sp,
                color = mainColor,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun DescriptionSection(schedule: Reminder, mainColor: Color) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Description,
                    contentDescription = "Description",
                    tint = mainColor,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Description",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1F2937)
                )
            }

            Text(
                text = schedule.description,
                fontSize = 14.sp,
                color = Color(0xFF6B7280),
                lineHeight = 20.sp
            )
        }
    }
}

@Composable
fun MetadataSection(schedule: Reminder, mainColor: Color) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFAFAFA)),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Information",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF374151),
                modifier = Modifier.padding(bottom = 12.dp)
            )

            schedule.createdAt?.let { createdAt ->
                DetailRow(
                    icon = Icons.Outlined.DateRange,
                    label = "Created",
                    value = formatDate(createdAt),
                    mainColor = Color(0xFF6B7280)
                )
            }

            schedule.updatedAt?.let { updatedAt ->
                Spacer(modifier = Modifier.height(8.dp))
                DetailRow(
                    icon = Icons.Outlined.Update,
                    label = "Updated",
                    value = formatDate(updatedAt),
                    mainColor = Color(0xFF6B7280)
                )
            }

            schedule._id?.let { id ->
                Spacer(modifier = Modifier.height(8.dp))
                DetailRow(
                    icon = Icons.Outlined.Tag,
                    label = "ID",
                    value = id.take(8) + "...",
                    mainColor = Color(0xFF6B7280)
                )
            }
        }
    }
}

@Composable
fun DetailRow(
    icon: ImageVector,
    label: String,
    value: String,
    mainColor: Color
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = mainColor,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = label,
            fontSize = 12.sp,
            color = Color(0xFF6B7280),
            modifier = Modifier.width(60.dp)
        )
        Text(
            text = value,
            fontSize = 14.sp,
            color = Color(0xFF1F2937),
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun LoadingContent(mainColor: Color) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(400.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator(
                color = mainColor,
                strokeWidth = 3.dp
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Loading schedule details...",
                color = Color(0xFF6B7280),
                fontSize = 14.sp
            )
        }
    }
}

@Composable
fun ErrorContent(onClose: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(400.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.Error,
                contentDescription = "Error",
                tint = Color.Red,
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Schedule not found",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF1F2937)
            )
            Text(
                text = "The schedule you're looking for doesn't exist or has been deleted.",
                fontSize = 14.sp,
                color = Color(0xFF6B7280),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 8.dp)
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(onClick = onClose) {
                Text("Close")
            }
        }
    }
}

private fun formatDate(dateString: String): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        val outputFormat = SimpleDateFormat("MMM dd, yyyy 'at' HH:mm", Locale.getDefault())
        val date = inputFormat.parse(dateString)
        outputFormat.format(date ?: Date())
    } catch (e: Exception) {
        try {
            val inputFormat2 = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val outputFormat2 = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
            val date = inputFormat2.parse(dateString)
            outputFormat2.format(date ?: Date())
        } catch (e2: Exception) {
            dateString
        }
    }
}

private fun getTypeIcon(type: String): ImageVector {
    return when (type.lowercase()) {
        "workout" -> Icons.Outlined.FitnessCenter
        "cardio" -> Icons.Outlined.DirectionsRun
        "strength" -> Icons.Outlined.FitnessCenter
        "yoga" -> Icons.Outlined.SelfImprovement
        "class" -> Icons.Outlined.Group
        "personal training" -> Icons.Outlined.Person
        "rest day" -> Icons.Outlined.Hotel
        "nutrition" -> Icons.Outlined.Restaurant
        "other" -> Icons.Outlined.Category
        else -> Icons.Outlined.Category
    }
}

private fun isTodays(dayName: String): Boolean {
    val today = Calendar.getInstance().getDisplayName(
        Calendar.DAY_OF_WEEK,
        Calendar.LONG,
        Locale.getDefault()
    )
    return dayName.equals(today, ignoreCase = true)
}