package com.example.gym_app.activity.goal

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddChart
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.AddChart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.gym_app.model.Goal
import com.example.gym_app.repository.GoalRepository
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.example.gym_app.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalScreen(navController: NavController) {
    val context = LocalContext.current
    val goalRepository = remember { GoalRepository(context) }
    val coroutineScope = rememberCoroutineScope()

    var goals by remember { mutableStateOf<List<Goal>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var searchQuery by remember { mutableStateOf("") }
    var showSearchBar by remember { mutableStateOf(false) }
    var showBottomSheet by remember { mutableStateOf(false) }
    var showDeleteConfirmation by remember { mutableStateOf(false) }
    var currentGoal by remember { mutableStateOf<Goal?>(null) }
    var isProcessing by remember { mutableStateOf(false) }
    var showDetailSheet by remember { mutableStateOf(false) }
    var selectedGoal by remember { mutableStateOf<Goal?>(null) }

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            isLoading = true
            goals = goalRepository.getAllGoals() ?: emptyList()
            isLoading = false
        }
    }

    val filteredGoals = remember(goals, searchQuery) {
        goals.filter { goal ->
            searchQuery.isBlank() || goal.title.contains(searchQuery, ignoreCase = true)
        }
    }

    AnimatedVisibility(visible = showDetailSheet, enter = slideInHorizontally { it }, exit = slideOutHorizontally { it }) {
        GoalDetailSideSheet(goal = selectedGoal!!, onClose = { showDetailSheet = false })
    }


    Scaffold(
        containerColor = colorResource(R.color.mainColor),
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colorResource(R.color.mainColor),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White,
                    actionIconContentColor = Color.White
                ),
                title = {
                    if (showSearchBar) {
                        TextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            placeholder = { Text("Search goals...") },
                            leadingIcon = {
                                Icon(Icons.Default.Search, contentDescription = null)
                            },
                            singleLine = true,
                            shape = RoundedCornerShape(24.dp),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White,
                                disabledContainerColor = Color.White,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                disabledIndicatorColor = Color.Transparent,
                                cursorColor = Color.Black,
                                focusedTextColor = Color.Black,
                                unfocusedTextColor = Color.Black,
                                disabledTextColor = Color.Gray,
                                focusedLeadingIconColor = Color.Gray,
                                unfocusedLeadingIconColor = Color.Gray,
                                focusedPlaceholderColor = Color.Gray,
                                unfocusedPlaceholderColor = Color.Gray
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                        )
                    } else {
                        Text("My Goals")
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showSearchBar = !showSearchBar }) {
                        Icon(Icons.Default.Search, contentDescription = "Search")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    currentGoal = null
                    showBottomSheet = true
                },
                containerColor = colorResource(R.color.orange),
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Goal")
            }
        },
        snackbarHost = { SnackbarHost(remember { SnackbarHostState() }) }
    ) { paddingValues ->
    Box(modifier = Modifier.padding(paddingValues).fillMaxSize().background(colorResource(R.color.smoothMainColor))) {
            when {
                isLoading -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
                filteredGoals.isEmpty() -> Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    NoGoalsState(onAddGoalClicked = {
                        currentGoal = null
                        showBottomSheet = true
                    })
                }
                else -> LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                    items(filteredGoals, key = { it._id ?: "" }) { goal ->
                        GoalItem(
                            goal = goal,
                            onEdit = {
                                currentGoal = goal
                                showBottomSheet = true
                            },
                            onDetail = {
                                selectedGoal = goal
                                showDetailSheet = true
                            },
                            onDelete = {
                                currentGoal = goal
                                showDeleteConfirmation = true
                            },
                            onToggleStatus = { newStatus ->
                                coroutineScope.launch {
                                    goal._id?.let { id ->
                                        val updatedGoal = goal.copy(
                                            isActive = newStatus,
                                            updatedAt = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                                        )
                                        goalRepository.updateGoal(id, updatedGoal)
                                        goals = goalRepository.getAllGoals() ?: emptyList()
                                    }
                                }
                            }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }

        if (showDetailSheet && selectedGoal != null) {
            GoalDetailSideSheet(
                goal = selectedGoal!!,
                onClose = { showDetailSheet = false }
            )
        }

        if (showBottomSheet) {
            GoalInputBottomSheet(
                goal = currentGoal,
                onDismiss = { showBottomSheet = false },
                onSave = { updatedGoal ->
                    isProcessing = true
                    coroutineScope.launch {
                        if (updatedGoal._id == null) {
                            goalRepository.createGoal(updatedGoal.copy(
                                createdAt = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                            ))
                        } else {
                            goalRepository.updateGoal(
                                id = updatedGoal._id,
                                goal = updatedGoal.copy(
                                    updatedAt = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                                )
                            )
                        }
                        goals = goalRepository.getAllGoals() ?: emptyList()
                        isProcessing = false
                        showBottomSheet = false
                    }
                },
                isProcessing = isProcessing
            )
        }


        if (showDeleteConfirmation) {
                DeleteConfirmationBottomSheet(
                    goal = currentGoal,
                    onDismiss = { showDeleteConfirmation = false },
                    isProcessing = isProcessing,
                    onConfirm = {
                        isProcessing = true
                        coroutineScope.launch {
                            currentGoal?._id?.let { id ->
                                goalRepository.deleteGoal(id)
                                goals = goalRepository.getAllGoals() ?: emptyList()
                            }
                            isProcessing = false
                            showDeleteConfirmation = false
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun GoalItem(
    goal: Goal,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onToggleStatus: (Boolean) -> Unit,
    onDetail: () -> Unit
) {
    var menuExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .shadow(4.dp, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = colorResource(R.color.mainColor)
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Text(
                        text = goal.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )

                    Spacer(Modifier.height(4.dp))

                    Text(
                        text = if (goal.isActive) "Aktif" else "Nonaktif",
                        color = if (goal.isActive) Color.Green else Color.Red,
                        fontSize = 12.sp,
                        modifier = Modifier
                            .background(
                                color = if (goal.isActive) Color(0x1A00C853) else Color(0x1AD50000),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                Box {
                    IconButton(onClick = { menuExpanded = true }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "More options",
                            tint = Color.White
                        )
                    }

                    DropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = { menuExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Lihat Detail") },
                            onClick = {
                                menuExpanded = false
                                onDetail()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Edit") },
                            onClick = {
                                menuExpanded = false
                                onEdit()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Hapus") },
                            onClick = {
                                menuExpanded = false
                                onDelete()
                            }
                        )
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    IconWithLabel(icon = Icons.Default.Edit, label = goal.type)
                    IconWithLabel(icon = Icons.Default.AddChart, label = goal.category)
                }

                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    if (goal.targetValue != null) {
                        IconWithLabel(icon = Icons.Default.Add, label = "${goal.targetValue} ${goal.unit ?: ""}")
                    }
                    IconWithLabel(icon = Icons.Default.ArrowBack, label = "Start: ${goal.startDate}")
                    goal.endDate?.let {
                        IconWithLabel(icon = Icons.Default.ArrowBack, label = "End: $it")
                    }
                }
            }
        }
    }
}


@Composable
fun IconWithLabel(icon: ImageVector, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            icon,
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            tint = Color.White.copy(alpha = 0.7f)
        )
        Spacer(Modifier.width(6.dp))
        Text(
            text = label.replaceFirstChar { it.uppercase() },
            fontSize = 13.sp,
            color = Color.White.copy(alpha = 0.9f)
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalInputBottomSheet(
    goal: Goal?,
    onDismiss: () -> Unit,
    onSave: (Goal) -> Unit,
    isProcessing: Boolean
) {
    val goalTypes = listOf("DAILY", "WEEKLY", "CUSTOM")
    val categories = listOf("FITNESS", "NUTRITION", "WELLNESS", "PERFORMANCE", "HABIT", "OTHER")

    var title by remember { mutableStateOf(goal?.title ?: "") }
    var type by remember { mutableStateOf(goal?.type ?: goalTypes[0]) }
    var targetValue by remember { mutableStateOf(goal?.targetValue?.toString() ?: "") }
    var unit by remember { mutableStateOf(goal?.unit ?: "") }
    var category by remember { mutableStateOf(goal?.category ?: categories[0]) }
    var startDate by remember { mutableStateOf(goal?.startDate ?: "") }
    var endDate by remember { mutableStateOf(goal?.endDate ?: "") }
    var isActive by remember { mutableStateOf(goal?.isActive ?: true) }

    var isTypeExpanded by remember { mutableStateOf(false) }
    var isCategoryExpanded by remember { mutableStateOf(false) }

    ModalBottomSheet(
        modifier = Modifier.background(colorResource(R.color.smoothMainColor)),
        onDismissRequest = onDismiss,
        containerColor = colorResource(R.color.smoothMainColor),
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .background(colorResource(R.color.smoothMainColor))
        ) {
            Text(text = if (goal == null) "Add New Goal" else "Edit Goal", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = Color.White)

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Title") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = colorResource(R.color.orange),
                    unfocusedContainerColor = colorResource(R.color.smoothMainColor),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = Color.White,
                    unfocusedBorderColor = Color.White,
                    focusedLabelColor = Color.White,
                    unfocusedLabelColor = Color.White,
                    focusedPlaceholderColor = Color.LightGray,
                    unfocusedPlaceholderColor = Color.LightGray
                )
            )

            ExposedDropdownMenuBox(
                expanded = isTypeExpanded,
                onExpandedChange = { isTypeExpanded = it }
            ) {
                OutlinedTextField(
                    readOnly = true,
                    value = type.replaceFirstChar { it.uppercase() },
                    onValueChange = { },
                    label = { Text("Type") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = colorResource(R.color.orange),
                        unfocusedContainerColor = colorResource(R.color.smoothMainColor),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color.White,
                        unfocusedBorderColor = Color.White,
                        focusedLabelColor = Color.White,
                        unfocusedLabelColor = Color.White,
                        focusedPlaceholderColor = Color.LightGray,
                        unfocusedPlaceholderColor = Color.LightGray
                    ),
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = isTypeExpanded)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )

                ExposedDropdownMenu(
                    expanded = isTypeExpanded,
                    onDismissRequest = { isTypeExpanded = false }
                ) {
                    goalTypes.forEach { selectionOption ->
                        DropdownMenuItem(
                            text = { Text(selectionOption.replaceFirstChar { it.uppercase() }) },
                            onClick = {
                                type = selectionOption
                                isTypeExpanded = false
                            }
                        )
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = targetValue,
                    onValueChange = { targetValue = it },
                    label = { Text("Target Value") },
                    modifier = Modifier.weight(1f),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = colorResource(R.color.orange),
                        unfocusedContainerColor = colorResource(R.color.smoothMainColor),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color.White,
                        unfocusedBorderColor = Color.White,
                        focusedLabelColor = Color.White,
                        unfocusedLabelColor = Color.White,
                        focusedPlaceholderColor = Color.LightGray,
                        unfocusedPlaceholderColor = Color.LightGray
                    ),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    )
                )
                OutlinedTextField(
                    value = unit,
                    onValueChange = { unit = it },
                    label = { Text("Unit") },
                    modifier = Modifier.weight(1f),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = colorResource(R.color.orange),
                        unfocusedContainerColor = colorResource(R.color.smoothMainColor),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color.White,
                        unfocusedBorderColor = Color.White,
                        focusedLabelColor = Color.White,
                        unfocusedLabelColor = Color.White,
                        focusedPlaceholderColor = Color.LightGray,
                        unfocusedPlaceholderColor = Color.LightGray
                    )
                )
            }

            ExposedDropdownMenuBox(
                expanded = isCategoryExpanded,
                onExpandedChange = { isCategoryExpanded = it }
            ) {
                OutlinedTextField(
                    readOnly = true,
                    value = category.replaceFirstChar { it.uppercase() },
                    onValueChange = { },
                    label = { Text("Category") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = colorResource(R.color.orange),
                        unfocusedContainerColor = colorResource(R.color.smoothMainColor),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color.White,
                        unfocusedBorderColor = Color.White,
                        focusedLabelColor = Color.White,
                        unfocusedLabelColor = Color.White,
                        focusedPlaceholderColor = Color.LightGray,
                        unfocusedPlaceholderColor = Color.LightGray
                    ),
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = isCategoryExpanded)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )

                ExposedDropdownMenu(
                    expanded = isCategoryExpanded,
                    onDismissRequest = { isCategoryExpanded = false }
                ) {
                    categories.forEach { selectionOption ->
                        DropdownMenuItem(
                            text = { Text(selectionOption.replaceFirstChar { it.uppercase() }) },
                            onClick = {
                                category = selectionOption
                                isCategoryExpanded = false
                            }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = startDate,
                onValueChange = { startDate = it },
                label = { Text("Start Date (yyyy-MM-dd)") },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("2023-11-01") },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = colorResource(R.color.orange),
                    unfocusedContainerColor = colorResource(R.color.smoothMainColor),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = Color.White,
                    unfocusedBorderColor = Color.White,
                    focusedLabelColor = Color.White,
                    unfocusedLabelColor = Color.White,
                    focusedPlaceholderColor = Color.LightGray,
                    unfocusedPlaceholderColor = Color.LightGray
                )
            )

            OutlinedTextField(
                value = endDate,
                onValueChange = { endDate = it },
                label = { Text("End Date (optional)") },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("2023-12-31") },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = colorResource(R.color.orange),
                    unfocusedContainerColor = colorResource(R.color.smoothMainColor),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = Color.White,
                    unfocusedBorderColor = Color.White,
                    focusedLabelColor = Color.White,
                    unfocusedLabelColor = Color.White,
                    focusedPlaceholderColor = Color.LightGray,
                    unfocusedPlaceholderColor = Color.LightGray
                )
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Active", color = colorResource(R.color.white))
                Switch(
                    checked = isActive,
                    onCheckedChange = { isActive = it }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (isProcessing) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircularProgressIndicator(color = Color.White)
                    Spacer(modifier = Modifier.width(5.dp))
                    Text("Processing...", color = Color.White)
                }
            } else {
                Button(
                    onClick = {
                        val newGoal = Goal(
                            _id = goal?._id,
                            title = title,
                            type = type,
                            targetValue = targetValue.takeIf { it.isNotBlank() }?.toDoubleOrNull(),
                            unit = unit.takeIf { it.isNotBlank() },
                            category = category,
                            startDate = startDate,
                            endDate = endDate.takeIf { it.isNotBlank() },
                            isActive = isActive,
                            createdAt = goal?.createdAt,
                            updatedAt = goal?.updatedAt
                        )
                        onSave(newGoal)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Save")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeleteConfirmationBottomSheet(
    goal: Goal?,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    isProcessing: Boolean
) {
    ModalBottomSheet(
        modifier = Modifier.background(colorResource(R.color.smoothMainColor)),
        containerColor = colorResource(R.color.smoothMainColor),
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .background(colorResource(R.color.mainColor))
        ) {
            Text(text = "Are you sure you want to delete this goal ${goal?.title} ?", fontWeight = FontWeight.Bold, fontSize = 20.sp, color= colorResource(R.color.white))
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onConfirm,
                enabled = !isProcessing,
                modifier = Modifier
                    .fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorResource(R.color.errorRed),
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                if (isProcessing) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        strokeWidth = 2.dp,
                        color = Color.White
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("Menghapus...")
                } else {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = Color.White
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("Hapus Goal")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedButton(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth(),
                border = ButtonDefaults.outlinedButtonBorder.copy(width = 1.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = Color.Transparent,
                    contentColor = Color.White
                )
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Cancel",
                    tint = Color.White
                )
                Spacer(Modifier.width(8.dp))
                Text("Batal")
            }
        }
    }
}

@Composable
fun NoGoalsState(
    onAddGoalClicked: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Outlined.AddChart,
            contentDescription = "No goals",
            modifier = Modifier
                .size(80.dp)
                .padding(bottom = 16.dp),
            tint = colorResource(R.color.white)
        )

        Text(
            text = "No Goals Yet",
            style = MaterialTheme.typography.titleLarge,
            color =  colorResource(R.color.white),
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Text(
            text = "Start by adding your first goal",
            style = MaterialTheme.typography.bodyMedium,
            color = colorResource(R.color.gray),
            modifier = Modifier.padding(bottom = 24.dp)
        )

        FilledTonalButton(
            onClick = onAddGoalClicked,
            colors = ButtonDefaults.filledTonalButtonColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            )
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add goal",
                modifier = Modifier.size(18.dp)
            )
            Spacer(Modifier.width(8.dp))
            Text("Add Goal")
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalDetailSideSheet(
    goal: Goal,
    onClose: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f))
            .clickable { onClose() }
    ) {
        AnimatedVisibility(
            visible = true,
            enter = slideInHorizontally(
                initialOffsetX = { it },
                animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
            ) + fadeIn(animationSpec = tween(durationMillis = 300)),
            exit = slideOutHorizontally(
                targetOffsetX = { it },
                animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
            ) + fadeOut(animationSpec = tween(durationMillis = 300))
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(320.dp)
                    .align(Alignment.CenterEnd)
                    .shadow(12.dp, RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp))
                    .clickable(enabled = false) { },
                shape = RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp),
                color = colorResource(R.color.mainColor)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Detail Goal",
                        style = MaterialTheme.typography.titleLarge,
                        color = colorResource(R.color.white)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    DetailItem("Title", goal.title)
                    DetailItem("Type", goal.type)
                    DetailItem("Category", goal.category)
                    DetailItem("Target", "${goal.targetValue ?: "-"} ${goal.unit ?: ""}")
                    DetailItem("Start Date", goal.startDate)
                    DetailItem("End Date", goal.endDate ?: "-")
                    DetailItem("Status", if (goal.isActive) "Aktif" else "Nonaktif")

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = onClose,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Tutup")
                    }
                }
            }
        }
    }
}




@Composable
fun DetailItem(label: String, value: String) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = Color.Gray
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium,
            color = Color.White
        )
    }
}



