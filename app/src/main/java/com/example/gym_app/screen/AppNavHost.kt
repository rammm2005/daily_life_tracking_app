package com.example.gym_app.screen

import SessionManager
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.gym_app.activity.mainactivity.MainContent
import com.example.gym_app.activity.meal.MealScreen
import com.example.gym_app.activity.tip.TipScreen
import com.example.gym_app.activity.workout.WorkoutScreen
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.gym_app.activity.mainactivity.MainButtomBar
import com.example.gym_app.activity.mainactivity.WorkoutDataProvider.getData

@Composable
fun AppNavHost(sessionManager: SessionManager) {
    val navController = rememberNavController()
    val role by sessionManager.userRole.collectAsState(initial = null)
    val isAdmin = role == "admin"
    val workouts = getData();


    NavHost(navController = navController, startDestination = "main_screen") {
        composable("main_screen") {
            Scaffold(
                    containerColor = Color(0Xff101322),
                    bottomBar = { MainButtomBar() },
                   ) { innerPadding ->
                    MainContent(modifier = Modifier.padding(innerPadding)
                        .fillMaxSize(),
                        workouts = workouts,
                        navController = navController  )
                }
        }
        composable("workout_screen") {
            WorkoutScreen(navController)
        }
        composable("meal_screen") {
            MealScreen(navController, isAdmin)
        }
        composable("tip_screen") {
            TipScreen(navController, isAdmin)
        }
    }
}
