package com.example.gym_app.activity.schedule

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.util.Log
import androidx.compose.animation.*
import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.gym_app.R
import com.example.gym_app.model.Reminder
import com.example.gym_app.repository.ReminderRepository
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

data class ValidationState(
    val isValid: Boolean = true,
    val errorMessage: String = ""
)

data class FormValidation(
    val titleValidation: ValidationState = ValidationState(),
    val descriptionValidation: ValidationState = ValidationState(),
    val daysValidation: ValidationState = ValidationState(),
    val methodsValidation: ValidationState = ValidationState(),
    val timeValidation: ValidationState = ValidationState()
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateUpdateScheduleScreen(
    navController: NavController,
    scheduleId: String? = null,
    onCreate: (Reminder) -> Unit = {},
    onUpdate: (Reminder) -> Unit = {}
) {
    val context = LocalContext.current
    val reminderRepository = remember { ReminderRepository(context) }
    val coroutineScope = rememberCoroutineScope()
    val mainColor = colorResource(id = R.color.mainColor)

    val bottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    var showBottomSheet by remember { mutableStateOf(true) }

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("Workout") }
    var schedule by remember { mutableStateOf("") }
    var selectedMethods by remember { mutableStateOf<List<String>>(listOf("Push Notification")) }
    var selectedDays by remember { mutableStateOf<List<String>>(emptyList()) }
    var selectedStatus by remember { mutableStateOf("active") }
    var selectedRepeat by remember { mutableStateOf("none") }
    var isLoading by remember { mutableStateOf(false) }

    var formValidation by remember { mutableStateOf(FormValidation()) }
    var showValidationErrors by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }

    var selectedDate by remember { mutableStateOf(Calendar.getInstance()) }
    var startTime by remember { mutableStateOf(Calendar.getInstance()) }
    var endTime by remember { mutableStateOf(Calendar.getInstance().apply { add(Calendar.HOUR, 1) }) }
    var isAllDay by remember { mutableStateOf(false) }

    fun validateTitle(value: String): ValidationState {
        return when {
            value.isBlank() -> ValidationState(false, "Title is required")
            value.length < 3 -> ValidationState(false, "Title must be at least 3 characters")
            value.length > 50 -> ValidationState(false, "Title must be less than 50 characters")
            else -> ValidationState(true)
        }
    }

    fun validateDescription(value: String): ValidationState {
        return when {
            value.isNotBlank() && value.length < 10 -> ValidationState(false, "Description must be at least 10 characters")
            value.length > 200 -> ValidationState(false, "Description must be less than 200 characters")
            else -> ValidationState(true)
        }
    }

    fun validateDays(days: List<String>): ValidationState {
        return if (days.isEmpty()) {
            ValidationState(false, "Please select at least one day")
        } else {
            ValidationState(true)
        }
    }

    fun validateMethods(methods: List<String>): ValidationState {
        return if (methods.isEmpty()) {
            ValidationState(false, "Please select at least one reminder method")
        } else {
            ValidationState(true)
        }
    }

    fun validateDateTime(
        date: Calendar,
        startTime: Calendar,
        endTime: Calendar,
        isAllDay: Boolean
    ): ValidationState {
        val now = Calendar.getInstance()

        val start = Calendar.getInstance().apply {
            time = date.time
            set(Calendar.HOUR_OF_DAY, startTime.get(Calendar.HOUR_OF_DAY))
            set(Calendar.MINUTE, startTime.get(Calendar.MINUTE))
            set(Calendar.SECOND, 0)
        }

        val end = Calendar.getInstance().apply {
            time = date.time
            set(Calendar.HOUR_OF_DAY, endTime.get(Calendar.HOUR_OF_DAY))
            set(Calendar.MINUTE, endTime.get(Calendar.MINUTE))
            set(Calendar.SECOND, 0)
        }

        return when {
            !isAllDay && start.before(now) ->
                ValidationState(false, "Start time cannot be in the past")
            !isAllDay && end.before(start) ->
                ValidationState(false, "End time must be after start time")
            isAllDay && date.before(now.apply { set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0) }) ->
                ValidationState(false, "Date cannot be in the past")
            else ->
                ValidationState(true)
        }
    }

    fun validateForm(): Boolean {
        val titleVal = validateTitle(title)
        val descVal = validateDescription(description)
        val daysVal = validateDays(selectedDays)
        val methodsVal = validateMethods(selectedMethods)
        val dateTimeVal = validateDateTime(selectedDate, startTime, endTime, isAllDay)

        formValidation = FormValidation(
            titleValidation = titleVal,
            descriptionValidation = descVal,
            daysValidation = daysVal,
            methodsValidation = methodsVal,
            timeValidation  = dateTimeVal
        )

        return titleVal.isValid && descVal.isValid &&
                daysVal.isValid && methodsVal.isValid &&
                dateTimeVal.isValid
    }


    LaunchedEffect(title) {
        if (showValidationErrors) {
            formValidation = formValidation.copy(titleValidation = validateTitle(title))
        }
    }

    LaunchedEffect(description) {
        if (showValidationErrors) {
            formValidation = formValidation.copy(descriptionValidation = validateDescription(description))
        }
    }

    LaunchedEffect(selectedDays) {
        if (showValidationErrors) {
            formValidation = formValidation.copy(daysValidation = validateDays(selectedDays))
        }
    }

    LaunchedEffect(selectedMethods) {
        if (showValidationErrors) {
            formValidation = formValidation.copy(methodsValidation = validateMethods(selectedMethods))
        }
    }

    LaunchedEffect(startTime, endTime, isAllDay) {
        schedule = if (isAllDay) {
            SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(selectedDate.time)
        } else {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
            "${dateFormat.format(startTime.time)} - ${SimpleDateFormat("HH:mm", Locale.getDefault()).format(endTime.time)}"
        }

        if (showValidationErrors) {
            formValidation = formValidation.copy(
                timeValidation = validateDateTime(selectedDate, startTime, endTime, isAllDay)
            )
        }
    }

    LaunchedEffect(scheduleId) {
        scheduleId?.let { id ->
            val reminder = reminderRepository.getReminderById(id)
            reminder?.let {
                title = it.title
                description = it.description
                type = it.type
                schedule = it.schedule
                selectedMethods = it.method
                selectedDays = it.days
                selectedStatus = it.status
                selectedRepeat = it.repeat
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f))
            .clickable {
                showBottomSheet = false
                navController.navigateUp()
            }
    ) {
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }

    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = {
                showBottomSheet = false
                navController.navigateUp()
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
            CreateScheduleBottomSheetContent(
                title = title,
                onTitleChange = { title = it },
                description = description,
                onDescriptionChange = { description = it },
                type = type,
                onTypeChange = { type = it },
                schedule = schedule,
                selectedMethods = selectedMethods,
                onMethodsChange = { selectedMethods = it },
                selectedDays = selectedDays,
                onDaysChange = { selectedDays = it },
                selectedStatus = selectedStatus,
                onStatusChange = { selectedStatus = it },
                selectedRepeat = selectedRepeat,
                onRepeatChange = { selectedRepeat = it },
                selectedDate = selectedDate,
                onDateChange = { selectedDate = it },
                startTime = startTime,
                onStartTimeChange = { startTime = it },
                endTime = endTime,
                onEndTimeChange = { endTime = it },
                isAllDay = isAllDay,
                onAllDayChange = { isAllDay = it },
                isLoading = isLoading,
                mainColor = mainColor,
                isUpdate = scheduleId != null,
                formValidation = formValidation,
                showValidationErrors = showValidationErrors,
                onSave = {
                    showValidationErrors = true
                    if (validateForm()) {
                        coroutineScope.launch {
                            isLoading = true
                            try {
                                val reminder = Reminder(
                                    _id = scheduleId,
                                    type = type,
                                    title = title,
                                    schedule = schedule,
                                    description = description,
                                    method = selectedMethods,
                                    days = selectedDays,
                                    status = selectedStatus,
                                    repeat = selectedRepeat
                                )

                                val result = if (scheduleId != null) {
                                    reminderRepository.updateReminder(reminder._id!!, reminder)?.also { updated ->
                                        onUpdate(updated)
                                    }
                                } else {
                                    reminderRepository.createReminder(reminder)?.also { created ->
                                        onCreate(created)
                                    }
                                }

                                if (result != null) {
                                    snackbarHostState.showSnackbar(
                                        message = if (scheduleId != null) "Reminder updated successfully!" else "Reminder created successfully!",
                                        duration = SnackbarDuration.Short
                                    )
                                    navController.navigateUp()
                                } else {
                                    snackbarHostState.showSnackbar(
                                        message = "Failed to save reminder. Please try again.",
                                        duration = SnackbarDuration.Long
                                    )
                                }
                            } catch (e: Exception) {
                                Log.d("CreateUpdateScheduleScreen", "Error saving reminder: ${e.message}")
                                snackbarHostState.showSnackbar(
                                    message = "Error: ${e.message ?: "Unknown error occurred"}",
                                    duration = SnackbarDuration.Long
                                )
                            } finally {
                                isLoading = false
                            }
                        }
                    } else {
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar(
                                message = "Please fix the errors before saving",
                                duration = SnackbarDuration.Short
                            )
                        }
                    }
                },
                onCancel = {
                    showBottomSheet = false
                    navController.navigateUp()
                },
                onDelete = scheduleId?.let { id ->
                    {
                        coroutineScope.launch {
                            try {
                                reminderRepository.deleteReminder(id)
                                snackbarHostState.showSnackbar(
                                    message = "Reminder deleted successfully!",
                                    duration = SnackbarDuration.Short
                                )
                                navController.navigateUp()
                            } catch (e: Exception) {
                                snackbarHostState.showSnackbar(
                                    message = "Failed to delete reminder",
                                    duration = SnackbarDuration.Short
                                )
                            }
                        }
                    }
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateScheduleBottomSheetContent(
    title: String,
    onTitleChange: (String) -> Unit,
    description: String,
    onDescriptionChange: (String) -> Unit,
    type: String,
    onTypeChange: (String) -> Unit,
    schedule: String,
    selectedMethods: List<String>,
    onMethodsChange: (List<String>) -> Unit,
    selectedDays: List<String>,
    onDaysChange: (List<String>) -> Unit,
    selectedStatus: String,
    onStatusChange: (String) -> Unit,
    selectedRepeat: String,
    onRepeatChange: (String) -> Unit,
    selectedDate: Calendar,
    onDateChange: (Calendar) -> Unit,
    startTime: Calendar,
    onStartTimeChange: (Calendar) -> Unit,
    endTime: Calendar,
    onEndTimeChange: (Calendar) -> Unit,
    isAllDay: Boolean,
    onAllDayChange: (Boolean) -> Unit,
    isLoading: Boolean,
    mainColor: Color,
    isUpdate: Boolean,
    formValidation: FormValidation,
    showValidationErrors: Boolean,
    onSave: () -> Unit,
    onCancel: () -> Unit,
    onDelete: (() -> Unit)? = null
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = onCancel) {
                    Text("Cancel", color = Color.Gray)
                }

                Text(
                    text = if (isUpdate) "Edit Reminder" else "New Reminder",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1F2937)
                )

                TextButton(
                    onClick = onSave,
                    enabled = !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            strokeWidth = 2.dp,
                            color = mainColor
                        )
                    } else {
                        Text("Save", color = mainColor, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        item {
            Column {
                OutlinedTextField(
                    value = title,
                    onValueChange = onTitleChange,
                    label = { Text("Reminder Title *") },
                    placeholder = { Text("Add title") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = showValidationErrors && !formValidation.titleValidation.isValid,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = if (showValidationErrors && !formValidation.titleValidation.isValid)
                            Color.Red else mainColor,
                        focusedLabelColor = if (showValidationErrors && !formValidation.titleValidation.isValid)
                            Color.Red else mainColor,
                        errorBorderColor = Color.Red,
                        errorLabelColor = Color.Red
                    ),
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.Title,
                            contentDescription = null,
                            tint = if (showValidationErrors && !formValidation.titleValidation.isValid)
                                Color.Red else mainColor
                        )
                    },
                    trailingIcon = {
                        if (showValidationErrors && !formValidation.titleValidation.isValid) {
                            Icon(
                                imageVector = Icons.Default.Error,
                                contentDescription = "Error",
                                tint = Color.Red,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                )

                if (showValidationErrors && !formValidation.titleValidation.isValid) {
                    Text(
                        text = formValidation.titleValidation.errorMessage,
                        color = Color.Red,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                    )
                }
            }
        }

        item {
            TypeSelector(
                selectedType = type,
                onTypeChange = onTypeChange,
                mainColor = mainColor
            )
        }

        item {
            StatusSelector(
                selectedStatus = selectedStatus,
                onStatusChange = onStatusChange,
                mainColor = mainColor
            )
        }

        item {
            Column {
                DateTimeSection(
                    selectedDate = selectedDate,
                    onDateChange = onDateChange,
                    startTime = startTime,
                    onStartTimeChange = onStartTimeChange,
                    endTime = endTime,
                    onEndTimeChange = onEndTimeChange,
                    isAllDay = isAllDay,
                    onAllDayChange = onAllDayChange,
                    mainColor = mainColor,
                    hasError = showValidationErrors && !formValidation.timeValidation.isValid
                )

                if (showValidationErrors && !formValidation.timeValidation.isValid) {
                    Text(
                        text = formValidation.timeValidation.errorMessage,
                        color = Color.Red,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                    )
                }
            }
        }

        item {
            OutlinedTextField(
                value = schedule,
                onValueChange = { },
                label = { Text("Schedule") },
                modifier = Modifier.fillMaxWidth(),
                enabled = false,
                colors = OutlinedTextFieldDefaults.colors(
                    disabledBorderColor = mainColor.copy(alpha = 0.5f),
                    disabledLabelColor = mainColor.copy(alpha = 0.5f)
                ),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.Schedule,
                        contentDescription = null,
                        tint = mainColor.copy(alpha = 0.5f)
                    )
                }
            )
        }

        item {
            RepeatSelector(
                selectedRepeat = selectedRepeat,
                onRepeatChange = onRepeatChange,
                mainColor = mainColor
            )
        }

        item {
            Column {
                DaysSelector(
                    selectedDays = selectedDays,
                    onDaysChange = onDaysChange,
                    mainColor = mainColor,
                    hasError = showValidationErrors && !formValidation.daysValidation.isValid
                )

                if (showValidationErrors && !formValidation.daysValidation.isValid) {
                    Text(
                        text = formValidation.daysValidation.errorMessage,
                        color = Color.Red,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                    )
                }
            }
        }

        item {
            Column {
                ReminderMethodsSelector(
                    selectedMethods = selectedMethods,
                    onMethodsChange = onMethodsChange,
                    mainColor = mainColor,
                    hasError = showValidationErrors && !formValidation.methodsValidation.isValid
                )

                if (showValidationErrors && !formValidation.methodsValidation.isValid) {
                    Text(
                        text = formValidation.methodsValidation.errorMessage,
                        color = Color.Red,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                    )
                }
            }
        }

        item {
            Column {
                OutlinedTextField(
                    value = description,
                    onValueChange = onDescriptionChange,
                    label = { Text("Description") },
                    placeholder = { Text("Add description (optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    isError = showValidationErrors && !formValidation.descriptionValidation.isValid,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = if (showValidationErrors && !formValidation.descriptionValidation.isValid)
                            Color.Red else mainColor,
                        focusedLabelColor = if (showValidationErrors && !formValidation.descriptionValidation.isValid)
                            Color.Red else mainColor,
                        errorBorderColor = Color.Red,
                        errorLabelColor = Color.Red
                    ),
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.Description,
                            contentDescription = null,
                            tint = if (showValidationErrors && !formValidation.descriptionValidation.isValid)
                                Color.Red else mainColor
                        )
                    },
                    trailingIcon = {
                        if (showValidationErrors && !formValidation.descriptionValidation.isValid) {
                            Icon(
                                imageVector = Icons.Default.Error,
                                contentDescription = "Error",
                                tint = Color.Red,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                )

                if (showValidationErrors && !formValidation.descriptionValidation.isValid) {
                    Text(
                        text = formValidation.descriptionValidation.errorMessage,
                        color = Color.Red,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                    )
                }
            }
        }

        if (isUpdate && onDelete != null) {
            item {
                OutlinedButton(
                    onClick = onDelete,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color.Red
                    )
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Delete,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Delete Reminder")
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun StatusSelector(
    selectedStatus: String,
    onStatusChange: (String) -> Unit,
    mainColor: Color
) {
    val statusOptions = listOf(
        Triple("active", Icons.Outlined.PlayArrow, Color(0xFF10B981)), // Green
        Triple("paused", Icons.Outlined.Pause, Color(0xFFF59E0B)),     // Orange
        Triple("done", Icons.Outlined.CheckCircle, Color(0xFF6B7280))  // Gray
    )

    Column {
        Text(
            text = "Status",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF1F2937),
            modifier = Modifier.padding(bottom = 8.dp)
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(statusOptions) { (status, icon, color) ->
                StatusChip(
                    status = status,
                    icon = icon,
                    isSelected = selectedStatus == status,
                    onClick = { onStatusChange(status) },
                    chipColor = color
                )
            }
        }
    }
}

@Composable
fun StatusChip(
    status: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    chipColor: Color
) {
    Surface(
        modifier = Modifier.clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        color = if (isSelected) chipColor else Color(0xFFF3F4F6),
        contentColor = if (isSelected) Color.White else Color(0xFF6B7280)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = status.replaceFirstChar { it.uppercase() },
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

// New Repeat Selector Component
@Composable
fun RepeatSelector(
    selectedRepeat: String,
    onRepeatChange: (String) -> Unit,
    mainColor: Color
) {
    val repeatOptions = listOf(
        "none" to Icons.Outlined.Block,
        "daily" to Icons.Outlined.Today,
        "weekly" to Icons.Outlined.DateRange,
        "monthly" to Icons.Outlined.CalendarMonth
    )

    Column {
        Text(
            text = "Repeat",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF1F2937),
            modifier = Modifier.padding(bottom = 8.dp)
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(repeatOptions) { (repeat, icon) ->
                RepeatChip(
                    repeat = repeat,
                    icon = icon,
                    isSelected = selectedRepeat == repeat,
                    onClick = { onRepeatChange(repeat) },
                    mainColor = mainColor
                )
            }
        }
    }
}

@Composable
fun RepeatChip(
    repeat: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    mainColor: Color
) {
    Surface(
        modifier = Modifier.clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        color = if (isSelected) mainColor else Color(0xFFF3F4F6),
        contentColor = if (isSelected) Color.White else Color(0xFF6B7280)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = repeat.replaceFirstChar { it.uppercase() },
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

// Keep all existing components (TypeSelector, DaysSelector, etc.) with the same implementation...

@Composable
fun TypeSelector(
    selectedType: String,
    onTypeChange: (String) -> Unit,
    mainColor: Color
) {
    val types = listOf(
        "Workout" to Icons.Outlined.FitnessCenter,
        "Cardio" to Icons.Outlined.DirectionsRun,
        "Strength" to Icons.Outlined.FitnessCenter,
        "Yoga" to Icons.Outlined.SelfImprovement,
        "Class" to Icons.Outlined.Group,
        "Personal Training" to Icons.Outlined.Person,
        "Rest Day" to Icons.Outlined.Hotel,
        "Nutrition" to Icons.Outlined.Restaurant,
        "Other" to Icons.Outlined.Category
    )

    Column {
        Text(
            text = "Type",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF1F2937),
            modifier = Modifier.padding(bottom = 8.dp)
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(types) { (type, icon) ->
                TypeChip(
                    type = type,
                    icon = icon,
                    isSelected = selectedType == type,
                    onClick = { onTypeChange(type) },
                    mainColor = mainColor
                )
            }
        }
    }
}

@Composable
fun TypeChip(
    type: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    mainColor: Color
) {
    Surface(
        modifier = Modifier.clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        color = if (isSelected) mainColor else Color(0xFFF3F4F6),
        contentColor = if (isSelected) Color.White else Color(0xFF6B7280)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = type,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DaysSelector(
    selectedDays: List<String>,
    onDaysChange: (List<String>) -> Unit,
    mainColor: Color,
    hasError: Boolean = false
) {
    val daysOfWeek = listOf(
        "Monday", "Tuesday", "Wednesday", "Thursday",
        "Friday", "Saturday", "Sunday"
    )

    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Days *",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = if (hasError) Color.Red else Color(0xFF1F2937),
                modifier = Modifier.padding(bottom = 8.dp)
            )
            if (hasError) {
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.Default.Error,
                    contentDescription = "Error",
                    tint = Color.Red,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(daysOfWeek) { day ->
                val isSelected = selectedDays.contains(day)

                FilterChip(
                    selected = isSelected,
                    onClick = {
                        val newDays = if (isSelected) {
                            selectedDays - day
                        } else {
                            selectedDays + day
                        }
                        onDaysChange(newDays)
                    },
                    label = { Text(day.take(3), fontSize = 12.sp) },
                    enabled = true,
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = if (hasError) Color.Red else mainColor,
                        selectedLabelColor = Color.White,
                        containerColor = if (hasError) Color.Red.copy(alpha = 0.1f) else Color(0xFFF3F4F6)
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        borderColor = if (hasError) Color.Red else Color.Transparent,
                        selectedBorderColor = if (hasError) Color.Red else mainColor,
                        enabled = true,
                        selected = isSelected
                    )
                )
            }
        }
    }
}

@Composable
fun ReminderMethodsSelector(
    selectedMethods: List<String>,
    onMethodsChange: (List<String>) -> Unit,
    mainColor: Color,
    hasError: Boolean = false
) {
    val methods = listOf(
        "Push Notification" to Icons.Outlined.Notifications,
        "Email" to Icons.Outlined.Email,
        "In-App Alert" to Icons.Outlined.NotificationImportant
    )

    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Reminder Methods *",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = if (hasError) Color.Red else Color(0xFF1F2937),
                modifier = Modifier.padding(bottom = 8.dp)
            )
            if (hasError) {
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.Default.Error,
                    contentDescription = "Error",
                    tint = Color.Red,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(methods) { (method, icon) ->
                MethodChip(
                    method = method,
                    icon = icon,
                    isSelected = selectedMethods.contains(method),
                    onClick = {
                        val newMethods = if (selectedMethods.contains(method)) {
                            selectedMethods - method
                        } else {
                            selectedMethods + method
                        }
                        onMethodsChange(newMethods)
                    },
                    mainColor = if (hasError) Color.Red else mainColor,
                    hasError = hasError
                )
            }
        }
    }
}

@Composable
fun MethodChip(
    method: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    mainColor: Color,
    hasError: Boolean = false
) {
    Surface(
        modifier = Modifier.clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        color = when {
            isSelected -> mainColor
            hasError -> Color.Red.copy(alpha = 0.1f)
            else -> Color(0xFFF3F4F6)
        },
        contentColor = if (isSelected) Color.White else Color(0xFF6B7280),
        border = if (hasError && !isSelected) androidx.compose.foundation.BorderStroke(1.dp, Color.Red) else null
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = method,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateTimeSection(
    selectedDate: Calendar,
    onDateChange: (Calendar) -> Unit,
    startTime: Calendar,
    onStartTimeChange: (Calendar) -> Unit,
    endTime: Calendar,
    onEndTimeChange: (Calendar) -> Unit,
    isAllDay: Boolean,
    onAllDayChange: (Boolean) -> Unit,
    mainColor: Color,
    hasError: Boolean = false
) {
    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Date & Time",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = if (hasError) Color.Red else Color(0xFF1F2937),
                modifier = Modifier.padding(bottom = 8.dp)
            )
            if (hasError) {
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.Default.Error,
                    contentDescription = "Error",
                    tint = Color.Red,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("All Day", fontSize = 14.sp, color = Color(0xFF374151))
            Switch(
                checked = isAllDay,
                onCheckedChange = onAllDayChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = if (hasError) Color.Red else mainColor
                )
            )
        }

        DatePickerRow(
            label = "Date",
            date = selectedDate,
            onDateChange = onDateChange,
            mainColor = if (hasError) Color.Red else mainColor
        )

        if (!isAllDay) {
            TimePickerRow(
                label = "Start Time",
                time = startTime,
                onTimeChange = onStartTimeChange,
                mainColor = if (hasError) Color.Red else mainColor
            )

            TimePickerRow(
                label = "End Time",
                time = endTime,
                onTimeChange = onEndTimeChange,
                mainColor = if (hasError) Color.Red else mainColor
            )
        }
    }
}

@Composable
fun DatePickerRow(
    label: String,
    date: Calendar,
    onDateChange: (Calendar) -> Unit,
    mainColor: Color
) {
    val context = LocalContext.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                val datePickerDialog = DatePickerDialog(
                    context,
                    { _, year, month, dayOfMonth ->
                        val newDate = Calendar.getInstance().apply {
                            set(Calendar.YEAR, year)
                            set(Calendar.MONTH, month)
                            set(Calendar.DAY_OF_MONTH, dayOfMonth)
                        }
                        onDateChange(newDate)
                    },
                    date.get(Calendar.YEAR),
                    date.get(Calendar.MONTH),
                    date.get(Calendar.DAY_OF_MONTH)
                )
                datePickerDialog.show()
            }
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            color = Color(0xFF374151)
        )

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = SimpleDateFormat("EEE, MMM dd, yyyy", Locale.getDefault()).format(date.time),
                fontSize = 14.sp,
                color = mainColor,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = null,
                tint = Color(0xFF9CA3AF),
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
    mainColor: Color
) {
    val context = LocalContext.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
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
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            color = Color(0xFF374151)
        )

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(time.time),
                fontSize = 14.sp,
                color = mainColor,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = null,
                tint = Color(0xFF9CA3AF),
                modifier = Modifier.size(16.dp)
            )
        }
    }
}