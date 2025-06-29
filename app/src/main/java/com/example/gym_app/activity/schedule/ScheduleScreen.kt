package com.example.gym_app.activity.schedule

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
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

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun ScheduleScreen(navController: NavController) {
    val context = LocalContext.current
    val reminderRepository = remember { ReminderRepository(context) }
    val coroutineScope = rememberCoroutineScope()

    var schedules by remember { mutableStateOf<List<Reminder>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var selectedDate by remember { mutableStateOf(Calendar.getInstance()) }
    var viewMode by remember { mutableStateOf("Day") }
    var searchQuery by remember { mutableStateOf("") }
    var showSearchBar by remember { mutableStateOf(false) }

    val mainColor = colorResource(id = R.color.mainColor)

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            isLoading = true
            schedules = reminderRepository.getAllReminders() ?: emptyList()
            isLoading = false
        }
    }

    val filteredSchedules = remember(schedules, searchQuery, selectedDate, viewMode) {
        schedules.filter { schedule ->
            val matchesSearch = if (searchQuery.isBlank()) true else {
                schedule.title.contains(searchQuery, ignoreCase = true) ||
                        schedule.description.contains(searchQuery, ignoreCase = true) ||
                        schedule.type.contains(searchQuery, ignoreCase = true)
            }

            val matchesDate = when (viewMode) {
                "Day" -> isScheduleOnDate(schedule, selectedDate)
                "Week" -> isScheduleInWeek(schedule, selectedDate)
                "Month" -> isScheduleInMonth(schedule, selectedDate)
                else -> true
            }

            matchesSearch && matchesDate
        }
    }

    Scaffold(
        topBar = {
            ModernTopBar(
                selectedDate = selectedDate,
                onDateChange = { selectedDate = it },
                viewMode = viewMode,
                onViewModeChange = { viewMode = it },
                navController = navController,
                mainColor = mainColor,
                searchQuery = searchQuery,
                onSearchQueryChange = { searchQuery = it },
                showSearchBar = showSearchBar,
                onToggleSearch = { showSearchBar = !showSearchBar }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { navController.navigate("create_schedule") },
                containerColor = mainColor,
                contentColor = Color.White,
                modifier = Modifier.shadow(8.dp, RoundedCornerShape(16.dp))
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Event",
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "New Event",
                    fontWeight = FontWeight.Medium
                )
            }
        },
        containerColor = Color(0xFFF8FAFC)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFF8FAFC))
        ) {
            AnimatedVisibility(
                visible = showSearchBar,
                enter = slideInVertically(
                    animationSpec = tween(300),
                    initialOffsetY = { -it }
                ) + fadeIn(animationSpec = tween(300)),
                exit = slideOutVertically(
                    animationSpec = tween(300),
                    targetOffsetY = { -it }
                ) + fadeOut(animationSpec = tween(300))
            ) {
                ModernSearchBar(
                    query = searchQuery,
                    onQueryChange = { searchQuery = it },
                    mainColor = mainColor
                )
            }

            // Quick date navigation
            QuickDateNavigation(
                selectedDate = selectedDate,
                onDateChange = { selectedDate = it },
                mainColor = mainColor,
                scheduleCount = filteredSchedules.size
            )

            // Simplified view mode tabs
            SimpleViewModeTabs(
                selectedMode = viewMode,
                onModeChange = { viewMode = it },
                mainColor = mainColor
            )

            // Content based on view mode
            when (viewMode) {
                "Day" -> ImprovedDayView(
                    schedules = filteredSchedules,
                    selectedDate = selectedDate,
                    navController = navController,
                    mainColor = mainColor,
                    isLoading = isLoading,
                    onRefresh = {
                        coroutineScope.launch {
                            isLoading = true
                            schedules = reminderRepository.getAllReminders() ?: emptyList()
                            isLoading = false
                        }
                    }
                )
                "Week" -> ImprovedWeekView(
                    schedules = filteredSchedules,
                    selectedDate = selectedDate,
                    navController = navController,
                    mainColor = mainColor,
                    isLoading = isLoading
                )
                "Month" -> ImprovedMonthView(
                    schedules = filteredSchedules,
                    selectedDate = selectedDate,
                    navController = navController,
                    mainColor = mainColor,
                    isLoading = isLoading
                )
            }
        }
    }
}

@Composable
fun ModernSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    mainColor: Color
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            placeholder = {
                Text(
                    "Search your events...",
                    color = Color(0xFF9CA3AF)
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = mainColor,
                    modifier = Modifier.size(20.dp)
                )
            },
            trailingIcon = {
                if (query.isNotEmpty()) {
                    IconButton(onClick = { onQueryChange("") }) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Clear",
                            tint = Color(0xFF6B7280),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = mainColor,
                unfocusedBorderColor = Color.Transparent,
                focusedLabelColor = mainColor
            ),
            singleLine = true,
            shape = RoundedCornerShape(12.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModernTopBar(
    selectedDate: Calendar,
    onDateChange: (Calendar) -> Unit,
    viewMode: String,
    onViewModeChange: (String) -> Unit,
    navController: NavController,
    mainColor: Color,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    showSearchBar: Boolean,
    onToggleSearch: () -> Unit
) {
    TopAppBar(
        title = {
            Column {
                Text(
                    text = "My Schedule",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = Color.White
                )
                Text(
                    text = SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(selectedDate.time),
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }
        },
        navigationIcon = {
            IconButton(onClick = { navController.navigateUp() }) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }
        },
        actions = {
            // Today button
            TextButton(
                onClick = { onDateChange(Calendar.getInstance()) },
                colors = ButtonDefaults.textButtonColors(
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = "Today",
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp
                )
            }

            // Search toggle
            IconButton(onClick = onToggleSearch) {
                Icon(
                    imageVector = if (showSearchBar) Icons.Default.SearchOff else Icons.Default.Search,
                    contentDescription = if (showSearchBar) "Hide Search" else "Search",
                    tint = Color.White
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = mainColor
        )
    )
}

@Composable
fun QuickDateNavigation(
    selectedDate: Calendar,
    onDateChange: (Calendar) -> Unit,
    mainColor: Color,
    scheduleCount: Int
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = SimpleDateFormat("EEEE", Locale.getDefault()).format(selectedDate.time),
                    fontSize = 14.sp,
                    color = mainColor,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = SimpleDateFormat("dd MMMM", Locale.getDefault()).format(selectedDate.time),
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1F2937)
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Event,
                        contentDescription = null,
                        tint = Color(0xFF6B7280),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "$scheduleCount ${if (scheduleCount == 1) "event" else "events"}",
                        fontSize = 14.sp,
                        color = Color(0xFF6B7280),
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Date navigation buttons
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        val newDate = Calendar.getInstance().apply {
                            time = selectedDate.time
                            add(Calendar.DAY_OF_MONTH, -1)
                        }
                        onDateChange(newDate)
                    },
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            mainColor.copy(alpha = 0.1f),
                            CircleShape
                        )
                ) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowLeft,
                        contentDescription = "Previous Day",
                        tint = mainColor,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(
                    onClick = {
                        val newDate = Calendar.getInstance().apply {
                            time = selectedDate.time
                            add(Calendar.DAY_OF_MONTH, 1)
                        }
                        onDateChange(newDate)
                    },
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            mainColor.copy(alpha = 0.1f),
                            CircleShape
                        )
                ) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowRight,
                        contentDescription = "Next Day",
                        tint = mainColor,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun SimpleViewModeTabs(
    selectedMode: String,
    onModeChange: (String) -> Unit,
    mainColor: Color
) {
    val modes = listOf(
        "Day" to Icons.Outlined.Today,
        "Week" to Icons.Outlined.DateRange,
        "Month" to Icons.Outlined.CalendarMonth
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            modes.forEach { (mode, icon) ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .background(
                            if (selectedMode == mode) mainColor else Color.Transparent,
                            RoundedCornerShape(12.dp)
                        )
                        .clickable { onModeChange(mode) },
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = mode,
                            tint = if (selectedMode == mode) Color.White else Color(0xFF6B7280),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = mode,
                            color = if (selectedMode == mode) Color.White else Color(0xFF6B7280),
                            fontWeight = if (selectedMode == mode) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ImprovedDayView(
    schedules: List<Reminder>,
    selectedDate: Calendar,
    navController: NavController,
    mainColor: Color,
    isLoading: Boolean,
    onRefresh: () -> Unit
) {
    if (isLoading) {
        ModernLoadingState(mainColor)
        return
    }

    if (schedules.isEmpty()) {
        ModernEmptyState(
            title = "No events today",
            subtitle = "Create your first event to get started",
            icon = Icons.Outlined.EventNote,
            buttonText = "Create Event",
            onButtonClick = { navController.navigate("create_schedule") },
            onRefresh = onRefresh,
            mainColor = mainColor
        )
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(schedules) { schedule ->
            ModernScheduleCard(
                schedule = schedule,
                mainColor = mainColor,
                onClick = {
                    navController.navigate("schedule_detail/${schedule._id}")
                },
                onEdit = {
                    navController.navigate("edit_schedule/${schedule._id}")
                }
            )
        }

        // Add some bottom padding for FAB
        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
fun ModernScheduleCard(
    schedule: Reminder,
    mainColor: Color,
    onClick: () -> Unit,
    onEdit: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .background(
                                when (schedule.status) {
                                    "active" -> Color(0xFF10B981)
                                    "paused" -> Color(0xFFF59E0B)
                                    "done" -> Color(0xFF6B7280)
                                    else -> mainColor
                                },
                                CircleShape
                            )
                    )
                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = schedule.title,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1F2937)
                        )

                        Text(
                            text = schedule.type,
                            fontSize = 12.sp,
                            color = mainColor,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier
                                .background(
                                    mainColor.copy(alpha = 0.1f),
                                    RoundedCornerShape(8.dp)
                                )
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                IconButton(
                    onClick = onEdit,
                    modifier = Modifier
                        .size(36.dp)
                        .background(
                            Color(0xFFF3F4F6),
                            CircleShape
                        )
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit",
                        tint = Color(0xFF6B7280),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            if (schedule.description.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = schedule.description,
                    fontSize = 14.sp,
                    color = Color(0xFF6B7280),
                    lineHeight = 20.sp,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                InfoChip(
                    icon = Icons.Outlined.Schedule,
                    text = schedule.schedule,
                    mainColor = mainColor
                )

                InfoChip(
                    icon = Icons.Outlined.Repeat,
                    text = schedule.repeat.replaceFirstChar { it.uppercase() },
                    mainColor = mainColor
                )
            }

            val gson = remember { Gson() }

            val parsedDays = remember(schedule.days) {
                if (schedule.days.isNotEmpty()) {
                    try {
                        val raw = schedule.days.first()

                        val firstParse = gson.fromJson(raw, object : TypeToken<List<String>>() {}.type) as List<String>

                        val firstItem = firstParse.firstOrNull()
                        if (firstItem != null && firstItem.trim().startsWith("[") && firstItem.trim().endsWith("]")) {
                            val cleaned = firstItem.replace("\\", "")
                            gson.fromJson(cleaned, object : TypeToken<List<String>>() {}.type)
                        } else {
                            firstParse
                        }
                    } catch (e: Exception) {
                        emptyList()
                    }
                } else {
                    emptyList()
                }
            }



            if (parsedDays.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Active Days",
                    fontSize = 12.sp,
                    color = Color(0xFF6B7280),
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(parsedDays) { day ->
                        ModernDayChip(
                            day = day,
                            mainColor = mainColor,
                            isHighlighted = isToday(day)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun InfoChip(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String,
    mainColor: Color
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .background(
                Color(0xFFF8FAFC),
                RoundedCornerShape(8.dp)
            )
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = mainColor,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = text,
            fontSize = 12.sp,
            color = Color(0xFF374151),
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun ModernDayChip(
    day: String,
    mainColor: Color,
    isHighlighted: Boolean = false
) {
    val dayAbbr = day.take(3).uppercase()

    Box(
        modifier = Modifier
            .background(
                if (isHighlighted) mainColor else mainColor.copy(alpha = 0.1f),
                RoundedCornerShape(8.dp)
            )
            .padding(horizontal = 12.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = dayAbbr,
            fontSize = 11.sp,
            color = if (isHighlighted) Color.White else mainColor,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.5.sp
        )
    }
}

@Composable
fun ModernLoadingState(mainColor: Color) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator(
                color = mainColor,
                strokeWidth = 3.dp,
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Loading your events...",
                color = Color(0xFF6B7280),
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun ModernEmptyState(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    buttonText: String,
    onButtonClick: () -> Unit,
    onRefresh: () -> Unit,
    mainColor: Color
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            // Icon with background
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(
                        mainColor.copy(alpha = 0.1f),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = mainColor,
                    modifier = Modifier.size(40.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = title,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1F2937),
                textAlign = TextAlign.Center
            )

            Text(
                text = subtitle,
                fontSize = 14.sp,
                color = Color(0xFF6B7280),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 8.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Primary action button
            Button(
                onClick = onButtonClick,
                colors = ButtonDefaults.buttonColors(containerColor = mainColor),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.height(48.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = buttonText,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Secondary refresh button
            TextButton(onClick = onRefresh) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Refresh",
                    modifier = Modifier.size(16.dp),
                    tint = Color(0xFF6B7280)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Refresh",
                    color = Color(0xFF6B7280)
                )
            }
        }
    }
}

// Simplified Week and Month views for better UX
@Composable
fun ImprovedWeekView(
    schedules: List<Reminder>,
    selectedDate: Calendar,
    navController: NavController,
    mainColor: Color,
    isLoading: Boolean
) {
    if (isLoading) {
        ModernLoadingState(mainColor)
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            SimpleWeekHeader(selectedDate, mainColor)
        }

        if (schedules.isEmpty()) {
            item {
                ModernEmptyState(
                    title = "No events this week",
                    subtitle = "Add events to see them here",
                    icon = Icons.Outlined.EventNote,
                    buttonText = "Create Event",
                    onButtonClick = { navController.navigate("create_schedule") },
                    onRefresh = { },
                    mainColor = mainColor
                )
            }
        } else {
            items(schedules) { schedule ->
                ModernScheduleCard(
                    schedule = schedule,
                    mainColor = mainColor,
                    onClick = { navController.navigate("schedule_detail/${schedule._id}") },
                    onEdit = { navController.navigate("edit_schedule/${schedule._id}") }
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
fun ImprovedMonthView(
    schedules: List<Reminder>,
    selectedDate: Calendar,
    navController: NavController,
    mainColor: Color,
    isLoading: Boolean
) {
    if (isLoading) {
        ModernLoadingState(mainColor)
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            SimpleMonthGrid(selectedDate, schedules, mainColor)
        }

        if (schedules.isNotEmpty()) {
            item {
                Text(
                    text = "Events this month (${schedules.size})",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1F2937),
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
            }

            items(schedules) { schedule ->
                ModernScheduleCard(
                    schedule = schedule,
                    mainColor = mainColor,
                    onClick = { navController.navigate("schedule_detail/${schedule._id}") },
                    onEdit = { navController.navigate("edit_schedule/${schedule._id}") }
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
fun SimpleWeekHeader(selectedDate: Calendar, mainColor: Color) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            repeat(7) { dayIndex ->
                val dayDate = Calendar.getInstance().apply {
                    time = selectedDate.time
                    set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY + dayIndex)
                }

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = SimpleDateFormat("EEE", Locale.getDefault()).format(dayDate.time),
                        fontSize = 12.sp,
                        color = Color(0xFF6B7280),
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = dayDate.get(Calendar.DAY_OF_MONTH).toString(),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isToday(dayDate)) Color.White else Color(0xFF1F2937),
                        modifier = Modifier
                            .size(32.dp)
                            .background(
                                if (isToday(dayDate)) mainColor else Color.Transparent,
                                CircleShape
                            )
                            .wrapContentSize(Alignment.Center)
                    )
                }
            }
        }
    }
}

@Composable
fun SimpleMonthGrid(selectedDate: Calendar, events: List<Reminder>, mainColor: Color) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(selectedDate.time),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1F2937),
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Row(modifier = Modifier.fillMaxWidth()) {
                listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat").forEach { day ->
                    Text(
                        text = day,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF6B7280)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            val firstDayOfMonth = Calendar.getInstance().apply {
                time = selectedDate.time
                set(Calendar.DAY_OF_MONTH, 1)
            }

            val daysInMonth = firstDayOfMonth.getActualMaximum(Calendar.DAY_OF_MONTH)
            val startDayOfWeek = firstDayOfMonth.get(Calendar.DAY_OF_WEEK) - 1

            repeat(6) { week ->
                Row(modifier = Modifier.fillMaxWidth()) {
                    repeat(7) { day ->
                        val cellIndex = week * 7 + day
                        val dayNumber = cellIndex - startDayOfWeek + 1

                        val isCurrentMonth = dayNumber in 1..daysInMonth
                        val dayDate = if (isCurrentMonth) {
                            Calendar.getInstance().apply {
                                time = selectedDate.time
                                set(Calendar.DAY_OF_MONTH, dayNumber)
                            }
                        } else null

                        val hasEvents = dayDate?.let { date ->
                            events.any { isScheduleOnDate(it, date) }
                        } ?: false

                        SimpleMonthDayCell(
                            dayNumber = if (isCurrentMonth) dayNumber else 0,
                            isCurrentMonth = isCurrentMonth,
                            isToday = dayDate?.let { isToday(it) } ?: false,
                            hasEvents = hasEvents,
                            mainColor = mainColor,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SimpleMonthDayCell(
    dayNumber: Int,
    isCurrentMonth: Boolean,
    isToday: Boolean,
    hasEvents: Boolean,
    mainColor: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .padding(2.dp)
            .background(
                if (isToday) mainColor else Color.Transparent,
                RoundedCornerShape(8.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        if (isCurrentMonth && dayNumber > 0) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = dayNumber.toString(),
                    fontSize = 14.sp,
                    fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal,
                    color = when {
                        isToday -> Color.White
                        isCurrentMonth -> Color(0xFF1F2937)
                        else -> Color(0xFF9CA3AF)
                    }
                )

                if (hasEvents) {
                    Box(
                        modifier = Modifier
                            .size(4.dp)
                            .background(
                                if (isToday) Color.White else mainColor,
                                CircleShape
                            )
                    )
                }
            }
        }
    }
}

fun isToday(day: String): Boolean {
    val today = Calendar.getInstance()
    val todayName = SimpleDateFormat("EEEE", Locale.getDefault()).format(today.time)
    return todayName.equals(day, ignoreCase = true)
}

private fun isToday(date: Calendar): Boolean {
    val today = Calendar.getInstance()
    return date.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
            date.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR)
}

private fun isScheduleOnDate(schedule: Reminder, date: Calendar): Boolean {
    return try {
        val scheduleDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(schedule.schedule)
        val scheduleCal = Calendar.getInstance().apply {
            if (scheduleDate != null) {
                time = scheduleDate
            }
        }

        scheduleCal.get(Calendar.YEAR) == date.get(Calendar.YEAR) &&
                scheduleCal.get(Calendar.DAY_OF_YEAR) == date.get(Calendar.DAY_OF_YEAR)
    } catch (e: Exception) {
        false
    }
}

private fun isScheduleInWeek(schedule: Reminder, date: Calendar): Boolean {
    return try {
        val scheduleDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(schedule.schedule)
        val scheduleCal = Calendar.getInstance().apply { time = scheduleDate }

        val weekStart = Calendar.getInstance().apply {
            time = date.time
            set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)
        }
        val weekEnd = Calendar.getInstance().apply {
            time = weekStart.time
            add(Calendar.DAY_OF_WEEK, 6)
        }

        scheduleCal.timeInMillis >= weekStart.timeInMillis &&
                scheduleCal.timeInMillis <= weekEnd.timeInMillis
    } catch (e: Exception) {
        false
    }
}

private fun isScheduleInMonth(schedule: Reminder, date: Calendar): Boolean {
    return try {
        val scheduleDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(schedule.schedule)
        val scheduleCal = Calendar.getInstance().apply { time = scheduleDate }

        scheduleCal.get(Calendar.YEAR) == date.get(Calendar.YEAR) &&
                scheduleCal.get(Calendar.MONTH) == date.get(Calendar.MONTH)
    } catch (e: Exception) {
        false
    }
}