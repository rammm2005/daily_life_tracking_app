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

    // Load schedules
    LaunchedEffect(Unit) {
        coroutineScope.launch {
            isLoading = true
            schedules = reminderRepository.getAllReminders() ?: emptyList()
            isLoading = false
        }
    }

    // Filter schedules based on search query and selected date
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
            GoogleCalendarTopBar(
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
            FloatingActionButton(
                onClick = { navController.navigate("create_schedule") },
                containerColor = mainColor,
                contentColor = Color.White,
                modifier = Modifier.size(56.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Event",
                    modifier = Modifier.size(24.dp)
                )
            }
        },
        containerColor = mainColor.copy(alpha = 0.05f)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(mainColor.copy(alpha = 0.05f))
        ) {
            // Search bar
            AnimatedVisibility(
                visible = showSearchBar,
                enter = slideInVertically() + fadeIn(),
                exit = slideOutVertically() + fadeOut()
            ) {
                SearchBar(
                    query = searchQuery,
                    onQueryChange = { searchQuery = it },
                    mainColor = mainColor
                )
            }

            CalendarHeader(
                selectedDate = selectedDate,
                onDateChange = { selectedDate = it },
                mainColor = mainColor,
                scheduleCount = filteredSchedules.size
            )

            ViewModeTabs(
                selectedMode = viewMode,
                onModeChange = { viewMode = it },
                mainColor = mainColor
            )

            when (viewMode) {
                "Day" -> DayView(
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
                "Week" -> WeekView(
                    schedules = filteredSchedules,
                    selectedDate = selectedDate,
                    navController = navController,
                    mainColor = mainColor,
                    isLoading = isLoading
                )
                "Month" -> MonthView(
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
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    mainColor: Color
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            placeholder = { Text("Search schedules...") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = mainColor
                )
            },
            trailingIcon = {
                if (query.isNotEmpty()) {
                    IconButton(onClick = { onQueryChange("") }) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Clear",
                            tint = Color.Gray
                        )
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = mainColor,
                focusedLabelColor = mainColor
            ),
            singleLine = true
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoogleCalendarTopBar(
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
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(selectedDate.time),
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = Color.White
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = {
                        val newDate = Calendar.getInstance().apply {
                            time = selectedDate.time
                            when (viewMode) {
                                "Day" -> add(Calendar.DAY_OF_MONTH, -1)
                                "Week" -> add(Calendar.WEEK_OF_YEAR, -1)
                                "Month" -> add(Calendar.MONTH, -1)
                            }
                        }
                        onDateChange(newDate)
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowLeft,
                        contentDescription = "Previous",
                        tint = Color.White
                    )
                }
                IconButton(
                    onClick = {
                        val newDate = Calendar.getInstance().apply {
                            time = selectedDate.time
                            when (viewMode) {
                                "Day" -> add(Calendar.DAY_OF_MONTH, 1)
                                "Week" -> add(Calendar.WEEK_OF_YEAR, 1)
                                "Month" -> add(Calendar.MONTH, 1)
                            }
                        }
                        onDateChange(newDate)
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowRight,
                        contentDescription = "Next",
                        tint = Color.White
                    )
                }
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
            IconButton(onClick = {
                onDateChange(Calendar.getInstance())
            }) {
                Icon(
                    imageVector = Icons.Outlined.Today,
                    contentDescription = "Today",
                    tint = Color.White
                )
            }
            IconButton(onClick = onToggleSearch) {
                Icon(
                    imageVector = if (showSearchBar) Icons.Default.SearchOff else Icons.Default.Search,
                    contentDescription = if (showSearchBar) "Hide Search" else "Search",
                    tint = Color.White
                )
            }
            IconButton(onClick = { /* More options */ }) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "More",
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
fun CalendarHeader(
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
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
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
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1F2937)
                    )
                    Text(
                        text = "$scheduleCount ${if (scheduleCount == 1) "event" else "events"}",
                        fontSize = 12.sp,
                        color = Color(0xFF6B7280)
                    )
                }

                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(mainColor.copy(alpha = 0.1f), CircleShape)
                        .clickable {
                            // Open date picker
                            onDateChange(Calendar.getInstance())
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.CalendarToday,
                        contentDescription = "Calendar",
                        tint = mainColor,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ViewModeTabs(
    selectedMode: String,
    onModeChange: (String) -> Unit,
    mainColor: Color
) {
    val modes = listOf("Day", "Week", "Month")

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp)
        ) {
            modes.forEach { mode ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(40.dp)
                        .background(
                            if (selectedMode == mode) mainColor else Color.Transparent,
                            RoundedCornerShape(8.dp)
                        )
                        .clickable { onModeChange(mode) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = mode,
                        color = if (selectedMode == mode) Color.White else Color(0xFF6B7280),
                        fontWeight = if (selectedMode == mode) FontWeight.Bold else FontWeight.Normal,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

@Composable
fun DayView(
    schedules: List<Reminder>,
    selectedDate: Calendar,
    navController: NavController,
    mainColor: Color,
    isLoading: Boolean,
    onRefresh: () -> Unit
) {
    if (isLoading) {
        LoadingState(mainColor)
        return
    }

    if (schedules.isEmpty()) {
        EmptyDayState(navController, mainColor, onRefresh)
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(schedules) { schedule ->
            ScheduleCard(
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
    }
}

@Composable
fun ScheduleCard(
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
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
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
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = schedule.title,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1F2937)
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = schedule.type,
                        fontSize = 12.sp,
                        color = mainColor,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier
                            .background(
                                mainColor.copy(alpha = 0.1f),
                                RoundedCornerShape(4.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }

                IconButton(onClick = onEdit) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit",
                        tint = Color(0xFF6B7280),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            if (schedule.description.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = schedule.description,
                    fontSize = 14.sp,
                    color = Color(0xFF6B7280),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Schedule,
                        contentDescription = "Schedule",
                        tint = Color(0xFF6B7280),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = schedule.schedule,
                        fontSize = 12.sp,
                        color = Color(0xFF6B7280)
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Repeat,
                        contentDescription = "Repeat",
                        tint = Color(0xFF6B7280),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = schedule.repeat.replaceFirstChar { it.uppercase() },
                        fontSize = 12.sp,
                        color = Color(0xFF6B7280)
                    )
                }
            }

            val gson = remember { Gson() }
            val parsedDays = remember(schedule.days) {
                if (schedule.days.isNotEmpty()) {
                    try {
                        gson.fromJson(
                            schedule.days.first(),
                            object : TypeToken<List<String>>() {}.type
                        ) as List<String>
                    } catch (e: Exception) {
                        emptyList()
                    }
                } else {
                    emptyList()
                }
            }

            if (parsedDays.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))

                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.CalendarMonth,
                            contentDescription = "Days",
                            tint = Color(0xFF6B7280),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Active Days",
                            fontSize = 12.sp,
                            color = Color(0xFF6B7280),
                            fontWeight = FontWeight.Medium
                        )
                    }

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(parsedDays) { day ->
                            DayChip(
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
}


fun isToday(day: String): Boolean {
    val today = Calendar.getInstance()
    val todayName = SimpleDateFormat("EEEE", Locale.getDefault()).format(today.time)
    return todayName.equals(day, ignoreCase = true)
}


@Composable
fun DayChip(
    day: String,
    mainColor: Color,
    isHighlighted: Boolean = false
) {
    val dayAbbr = day.take(3).uppercase()
    val backgroundColor = when {
        isHighlighted -> mainColor
        else -> mainColor.copy(alpha = 0.08f)
    }
    val textColor = when {
        isHighlighted -> Color.White
        else -> mainColor
    }
    val borderColor = when {
        isHighlighted -> mainColor
        else -> mainColor.copy(alpha = 0.2f)
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .border(
                width = 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .animateContentSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = dayAbbr,
            fontSize = 11.sp,
            color = textColor,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 0.5.sp
        )
    }
}


@Composable
fun WeekView(
    schedules: List<Reminder>,
    selectedDate: Calendar,
    navController: NavController,
    mainColor: Color,
    isLoading: Boolean
) {
    if (isLoading) {
        LoadingState(mainColor)
        return
    }

    Column {
        WeekHeader(selectedDate, mainColor)

        if (schedules.isEmpty()) {
            EmptyWeekState(navController, mainColor)
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(schedules) { schedule ->
                    ScheduleCard(
                        schedule = schedule,
                        mainColor = mainColor,
                        onClick = { navController.navigate("schedule_detail/${schedule._id}") },
                        onEdit = { navController.navigate("edit_schedule/${schedule._id}") }
                    )
                }
            }
        }
    }
}

@Composable
fun WeekHeader(selectedDate: Calendar, mainColor: Color) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
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
                        color = Color(0xFF6B7280)
                    )
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
fun MonthView(
    schedules: List<Reminder>,
    selectedDate: Calendar,
    navController: NavController,
    mainColor: Color,
    isLoading: Boolean
) {
    if (isLoading) {
        LoadingState(mainColor)
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            MonthGrid(selectedDate, schedules, mainColor)
        }

        if (schedules.isNotEmpty()) {
            item {
                Text(
                    text = "Events this month",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1F2937),
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            items(schedules) { schedule ->
                ScheduleCard(
                    schedule = schedule,
                    mainColor = mainColor,
                    onClick = { navController.navigate("schedule_detail/${schedule._id}") },
                    onEdit = { navController.navigate("edit_schedule/${schedule._id}") }
                )
            }
        }
    }
}

@Composable
fun MonthGrid(selectedDate: Calendar, events: List<Reminder>, mainColor: Color) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

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

            val totalCells = 42 // 6 weeks * 7 days

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

                        MonthDayCell(
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
fun MonthDayCell(
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
            )
            .clickable { /* Select day */ },
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

@Composable
fun LoadingState(mainColor: Color) {
    Box(
        modifier = Modifier.fillMaxSize(),
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
                text = "Loading schedules...",
                color = Color(0xFF6B7280),
                fontSize = 14.sp
            )
        }
    }
}

@Composable
fun EmptyDayState(navController: NavController, mainColor: Color, onRefresh: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.EventNote,
                contentDescription = null,
                tint = Color(0xFF9CA3AF),
                modifier = Modifier.size(64.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "No events today",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF1F2937)
            )

            Text(
                text = "Tap + to create an event",
                fontSize = 14.sp,
                color = Color(0xFF6B7280),
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onRefresh,
                colors = ButtonDefaults.buttonColors(containerColor = mainColor)
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Refresh",
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Refresh")
            }
        }
    }
}

@Composable
fun EmptyWeekState(navController: NavController, mainColor: Color) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.EventNote,
                contentDescription = null,
                tint = Color(0xFF9CA3AF),
                modifier = Modifier.size(64.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "No events this week",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF1F2937)
            )

            Text(
                text = "Tap + to create an event",
                fontSize = 14.sp,
                color = Color(0xFF6B7280),
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

// Helper functions
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