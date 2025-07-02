package com.example.gym_app.activity.meal

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.gym_app.R
import com.example.gym_app.model.Meal
import com.example.gym_app.repository.MealRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MealScreen(navController: NavController, isAdmin: Boolean) {
    val context = LocalContext.current
    val repo = remember { MealRepository(context) }
    var meals by remember { mutableStateOf<List<Meal>>(emptyList()) }
    var filteredMeals by remember { mutableStateOf<List<Meal>>(emptyList()) }
    var searchQuery by remember { mutableStateOf(TextFieldValue("")) }
    var isLoading by remember { mutableStateOf(true) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        delay(1000) // simulate loading delay
        val response = repo.getAllMeals()
        Log.d("MEALS", "Result: $response")
        meals = response ?: emptyList()
        filteredMeals = meals
        isLoading = false
    }

    LaunchedEffect(searchQuery.text) {
        filteredMeals = meals.filter {
            it.title.contains(searchQuery.text, ignoreCase = true) ||
                    it.category.contains(searchQuery.text, ignoreCase = true)
        }
    }

    Scaffold(
        containerColor = colorResource(id = R.color.mainColor),
        topBar = {
            TopAppBar(
                title = { Text("Meal", color = MaterialTheme.colorScheme.onPrimary) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onPrimary
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
                    onClick = { navController.navigate("meal_create") },
                    modifier = Modifier.padding(16.dp),
                    containerColor = colorResource(id = R.color.darkBlue)
                ) {
                    Text("+", color = MaterialTheme.colorScheme.onPrimary)
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(colorResource(id = R.color.mainColor))
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Cari meal...", color = MaterialTheme.colorScheme.onPrimary) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.onPrimary,
                    cursorColor = MaterialTheme.colorScheme.primary,
                    focusedTextColor = MaterialTheme.colorScheme.onPrimary,
                    unfocusedTextColor = MaterialTheme.colorScheme.onPrimary,
                    focusedContainerColor = colorResource(id = R.color.mainColor),
                    unfocusedContainerColor = colorResource(id = R.color.mainColor)
                )
            )

            LazyColumn(modifier = Modifier.fillMaxSize()) {
                if (isLoading) {
                    items(3) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(180.dp)
                                .padding(vertical = 8.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.Gray.copy(alpha = 0.1f))
                        ) {}
                    }
                } else if (filteredMeals.isEmpty()) {
                    item { EmptyMealView() }
                } else {
                    items(filteredMeals) { meal ->
                        MealCard(
                            meal = meal,
                            isAdmin = isAdmin,
                            navController = navController,
                            mealRepository = repo,
                            onDeleted = {
                                CoroutineScope(Dispatchers.IO).launch {
                                    val response = repo.getAllMeals()
                                    meals = response ?: emptyList()
                                    filteredMeals = meals
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyMealView() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Text("Meal tidak ditemukan.", color = MaterialTheme.colorScheme.onPrimary)
    }
}
