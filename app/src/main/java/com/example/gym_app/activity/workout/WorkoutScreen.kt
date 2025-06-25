package com.example.gym_app.activity.workout

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.gym_app.R
import com.example.gym_app.model.Workout
import com.example.gym_app.repository.WorkoutRepository
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutScreen(navController: NavController, isAdmin: Boolean) {
    val context = LocalContext.current
    val workoutRepository = remember { WorkoutRepository(context) }
    val coroutineScope = rememberCoroutineScope()

    var workouts by remember { mutableStateOf<List<Workout>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    var searchQuery by remember { mutableStateOf("") }
    var selectedDifficulty by remember { mutableStateOf("All") }
    var selectedCategory by remember { mutableStateOf("All") }

    val filteredWorkouts = workouts.filter {
        val matchesSearch = it.title.contains(searchQuery, ignoreCase = true) ||
                it.category?.contains(searchQuery, ignoreCase = true) == true
        val matchesDifficulty = selectedDifficulty == "All" || it.difficulty == selectedDifficulty
        val matchesCategory = selectedCategory == "All" || it.category == selectedCategory
        matchesSearch && matchesDifficulty && matchesCategory
    }

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            isLoading = true
            workouts = workoutRepository.getAllWorkouts() ?: emptyList()
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Workout Programs",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
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
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colorResource(id = R.color.mainColor)
                )
            )
        },
        floatingActionButton = {
            if (isAdmin) {
                FloatingActionButton(
                    onClick = {
                        navController.navigate("create_workout")
                    },
                    containerColor = colorResource(id = R.color.mainColor),
                    contentColor = Color.White,
                    modifier = Modifier.size(56.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Workout",
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        },
        containerColor = Color(0xFFF8F9FA)
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(colorResource(R.color.smoothMainColor))
                .padding(padding)
        ) {
            when {
                isLoading -> {
                    LoadingState()
                }
                workouts.isEmpty() -> {
                    EmptyState(isAdmin = isAdmin, navController = navController)
                }
                else -> {
                    Column(modifier = Modifier.fillMaxSize()) {
                        SearchAndFilterSection(
                            searchQuery = searchQuery,
                            onSearchQueryChange = { searchQuery = it },
                            selectedDifficulty = selectedDifficulty,
                            onDifficultyChange = { selectedDifficulty = it },
                            selectedCategory = selectedCategory,
                            onCategoryChange = { selectedCategory = it }
                        )

                        WorkoutList(
                            workouts = filteredWorkouts,
                            isAdmin = isAdmin,
                            navController = navController,
                            workoutRepository = workoutRepository,
                            onWorkoutsUpdated = { workouts = it }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun LoadingState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .size(120.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(32.dp),
                    color = colorResource(id = R.color.mainColor),
                    strokeWidth = 3.dp
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Loading...",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun EmptyState(isAdmin: Boolean, navController: NavController) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(40.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Surface(
                    modifier = Modifier.size(80.dp),
                    shape = RoundedCornerShape(40.dp),
                    color = colorResource(id = R.color.mainColor).copy(alpha = 0.1f)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Icon(
                            imageVector = Icons.Default.SearchOff,
                            contentDescription = "No workouts found",
                            modifier = Modifier.size(40.dp),
                            tint = colorResource(id = R.color.mainColor)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "No Workouts Found",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2D3748),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = if (isAdmin) {
                        "Start building your workout library by creating your first workout program."
                    } else {
                        "No workout programs are available at the moment. Please check back later."
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp
                )

                if (isAdmin) {
                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = {
                            navController.navigate(route = "create_workout")
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colorResource(id = R.color.mainColor)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Create First Workout",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 16.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun WorkoutList(
    workouts: List<Workout>,
    isAdmin: Boolean,
    navController: NavController,
    workoutRepository: WorkoutRepository,
    onWorkoutsUpdated: (List<Workout>) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = colorResource(id = R.color.mainColor)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.FitnessCenter,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = "Available Workouts",
                            color = Color.White,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${workouts.size} workout${if (workouts.size != 1) "s" else ""} available",
                            color = Color.White.copy(alpha = 0.8f),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }

        items(workouts) { workout ->
            WorkoutCard(
                workout = workout,
                isAdmin = isAdmin,
                onClick = {
                    navController.navigate("workout_detail/${workout._id}")
                },
                onEditClick = {
                    navController.navigate("edit_workout/${workout._id}")
                },
                onDeleteClick = {
                    coroutineScope.launch {
                        workoutRepository.deleteWorkout(workout._id ?: "")
                        val updated = workoutRepository.getAllWorkouts() ?: emptyList()
                        onWorkoutsUpdated(updated)
                    }
                }
            )
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}