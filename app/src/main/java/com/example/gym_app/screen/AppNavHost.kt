package com.example.gym_app.screen

import SessionManager
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.gym_app.activity.mainactivity.MainContent
import com.example.gym_app.activity.meal.MealScreen
import com.example.gym_app.activity.tip.TipScreen
import com.example.gym_app.activity.workout.WorkoutScreen
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.example.gym_app.activity.mainactivity.MainButtomBar
import com.example.gym_app.activity.mainactivity.WorkoutDataProvider.getData
import com.example.gym_app.activity.tip.CreateUpdateTipScreen
import com.example.gym_app.activity.tip.DetailTipScreen
import com.example.gym_app.model.Tip
import com.example.gym_app.repository.TipRepository

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

        composable("create_tip") {
            val context = LocalContext.current
            val repo = TipRepository(context)

            CreateUpdateTipScreen(navController = navController) { tip, imagePaths  ->
                repo.createTip(tip, imagePaths)
            }
        }

        composable("edit_tip/{tipId}") { backStackEntry ->
            val context = LocalContext.current
            val repo = TipRepository(context)
            var tip by remember { mutableStateOf<Tip?>(null) }

            LaunchedEffect(Unit) {
                tip = repo.getTips()?.find { it._id == backStackEntry.arguments?.getString("tipId") }
            }

            tip?.let {
                CreateUpdateTipScreen(navController = navController, tip = it) { updated, imagePaths ->
                    repo.updateTip(it._id ?: "", updated, imagePaths)
                }
            }
        }

        composable("detail_tip/{tipId}") { backStackEntry ->
            val tipId = backStackEntry.arguments?.getString("tipId") ?: return@composable
            DetailTipScreen(navController = navController, tipId = tipId)
        }

    }
}
