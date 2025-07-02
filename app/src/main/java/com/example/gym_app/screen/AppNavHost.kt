package com.example.gym_app.screen

import SessionManager
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import com.example.gym_app.activity.mainactivity.MainButtomBar
import com.example.gym_app.activity.meal.CreateUpdateMealScreen
import com.example.gym_app.activity.tip.CreateUpdateTipScreen
import com.example.gym_app.activity.tip.DetailTipScreen
import com.example.gym_app.model.Meal
import com.example.gym_app.model.Tip
import com.example.gym_app.repository.MealRepository
import com.example.gym_app.repository.TipRepository
import androidx.core.net.toUri
import com.example.gym_app.R
import com.example.gym_app.activity.chatbot.ChatBotScreen
import com.example.gym_app.activity.dailytracking.CreateUpdateDailyTrackingScreen
import com.example.gym_app.activity.dailytracking.DailyTrackingScreen
import com.example.gym_app.activity.faq.FaqScreen
import com.example.gym_app.activity.favorite.FavoriteScreen
import com.example.gym_app.activity.goal.GoalScreen
import com.example.gym_app.activity.meal.DetailMealScreen
import com.example.gym_app.activity.profile.ProfileScreen
import com.example.gym_app.activity.schedule.CreateUpdateScheduleScreen
import com.example.gym_app.activity.schedule.DetailSchedule
import com.example.gym_app.activity.schedule.ScheduleScreen
import com.example.gym_app.activity.workout.CreateUpdateWorkoutScreen
import com.example.gym_app.activity.workout.WorkoutDetail
import com.example.gym_app.model.Workout
import com.example.gym_app.repository.ReminderRepository
import com.example.gym_app.repository.WorkoutRepository
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavHost(sessionManager: SessionManager) {
    val navController = rememberNavController()
    val role by sessionManager.userRole.collectAsState(initial = null)
    val isAdmin = role == "admin"
    val context = LocalContext.current
    val repository = remember { WorkoutRepository(context) }
    var workouts by remember { mutableStateOf<List<Workout>>(emptyList()) }

    LaunchedEffect(Unit) {
        workouts = repository.getAllWorkouts() ?: emptyList()
    }


    NavHost(navController = navController, startDestination = "main_screen") {
        composable("main_screen") {
            Scaffold(
                    containerColor = Color(0Xff101322),
                    bottomBar = { MainButtomBar(navController = navController) },
                   ) { innerPadding ->
                    MainContent(modifier = Modifier.padding(innerPadding)
                        .fillMaxSize(),
                        workouts = workouts,
                        navController = navController  )
                }
        }



        composable("chat_screen") {
            ChatBotScreen(navController)
        }


        composable("faq_screen") {
            Scaffold(
                containerColor = Color(0Xff101322),
                bottomBar = { MainButtomBar(navController = navController) },
            ) { innerPadding ->
                FaqScreen(
                    navController = navController,
                    modifier = Modifier.padding(innerPadding)
                )
            }
        }

        composable("profile_screen") {
            Scaffold(
                containerColor = Color(0Xff101322),
                bottomBar = { MainButtomBar(navController = navController) },
            ) { innerPadding ->
                ProfileScreen(
                    navController = navController,
                    modifier = Modifier.padding(innerPadding)
                )
            }
        }



        composable("favorite_screen") {
            Scaffold(
                containerColor = Color(0xFF101322),
                bottomBar = { MainButtomBar(navController = navController) },
                topBar = {
                    TopAppBar(
                        title = { Text("Favorites", color = Color.White) },
                        navigationIcon = {
                            IconButton(onClick = { navController.popBackStack() }) {
                                Icon(Icons.Default.Close, contentDescription = "Back", tint = Color.White)
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = colorResource(R.color.smoothMainColor)
                        )
                    )
                }
            ) { innerPadding ->
                FavoriteScreen(
                    navController = navController,
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                )
            }
        }





        composable("goal_screen") {
            GoalScreen(navController)
        }


        composable("progress_screen") {
            DailyTrackingScreen(navController)
        }

        composable("createUpdateDailyTracking") {
            CreateUpdateDailyTrackingScreen(
                navController = navController
            )
        }

        composable("createUpdateDailyTracking/{trackerId}") { backStackEntry ->
            val trackerId = backStackEntry.arguments?.getString("trackerId")

            CreateUpdateDailyTrackingScreen(
                navController = navController,
                trackerId = trackerId,
            )
        }

        composable("schedule_screen") {
            ScheduleScreen(navController)
        }

        composable("create_schedule") {
            val context = LocalContext.current

            CreateUpdateScheduleScreen(
                navController = navController,
                onCreate = { reminder ->
                    Toast.makeText(context, "Reminder berhasil dibuat!", Toast.LENGTH_SHORT).show()
                },
//                onUpdate = { reminder ->
//                    Toast.makeText(context, "Reminder berhasil diperbarui!", Toast.LENGTH_SHORT).show()
//                }
            )
        }

        composable("edit_schedule/{scheduleId}") { backStackEntry ->
            val context = LocalContext.current
            val scheduleId = backStackEntry.arguments?.getString("scheduleId")

            CreateUpdateScheduleScreen(
                navController = navController,
                scheduleId = scheduleId,
                onUpdate = { reminder ->
                    Toast.makeText(context, "Reminder berhasil diperbarui!", Toast.LENGTH_SHORT).show()
                }
            )
        }


        composable("schedule_detail/{scheduleId}") { backStackEntry ->
            val scheduleId = backStackEntry.arguments?.getString("scheduleId") ?: return@composable
            DetailSchedule(navController = navController, scheduleId = scheduleId)
        }


        composable("workout_screen") {
            WorkoutScreen(navController, isAdmin)
        }

        composable("create_workout") {
            val context = LocalContext.current
            val repo = WorkoutRepository(context)

            CreateUpdateWorkoutScreen(navController = navController) { workout, imageUri ->
                if (imageUri != null) {
                    repo.createWorkout(workout, imageUri)
                } else {
                    Log.e("CreateMeal", "Image is required but was null")
                }
            }
        }

        composable("workout_detail/{workoutId}") { backStackEntry ->
            val workoutId = backStackEntry.arguments?.getString("workoutId") ?: return@composable
            WorkoutDetail(navController = navController, workoutId = workoutId)
        }

        composable("edit_workout/{workoutId}") { backStackEntry ->
            val context = LocalContext.current
            val repo = WorkoutRepository(context)
            var workout by remember { mutableStateOf<Workout?>(null) }

            val workoutId = backStackEntry.arguments?.getString("workoutId")
            Log.d("AppNavHost", "Navigating to edit_workout with WorkoutId: $workoutId")

            LaunchedEffect(workoutId) {
                workoutId?.let {
                    val result = repo.getWorkoutById(it)
                    if (result != null) {
                        Log.d("AppNavHost", "workout found: ${result.title}")
                        workout = result
                    } else {
                        Log.e("AppNavHost", "workout with ID $it not found.")
                    }
                }
            }


            workout?.let {
                CreateUpdateWorkoutScreen(
                    navController = navController,
                    workout = it
                ) { updated, imageUri ->
                    Log.d("AppNavHost", "Updating workout with ID: ${it._id}")
                    Log.d("AppNavHost", "Updating workout with Image URI: $imageUri")
                    repo.updateWorkout(it._id ?: "", updated, imageUri)
                }
            }
        }

        composable("meal_screen") {
            MealScreen(navController, isAdmin)
        }

        composable("meal_create") {
            val context = LocalContext.current
            val repo = MealRepository(context)

            CreateUpdateMealScreen(navController = navController) { meal, imageUri ->
                if (imageUri != null) {
                    repo.createMeal(meal, imageUri)
                } else {
                    Log.e("CreateMeal", "Image is required but was null")
                }
            }
        }

        composable("detail_meal/{mealId}") { backStackEntry ->
            val mealId = backStackEntry.arguments?.getString("mealId") ?: return@composable
            DetailMealScreen(navController = navController, mealId = mealId)
        }


        composable("edit_meal/{mealId}") { backStackEntry ->
            val context = LocalContext.current
            val repo = MealRepository(context)
            var meal by remember { mutableStateOf<Meal?>(null) }

            val mealId = backStackEntry.arguments?.getString("mealId")
            Log.d("AppNavHost", "Navigating to edit_meal with mealId: $mealId")

            LaunchedEffect(mealId) {
                mealId?.let {
                    val result = repo.getMealById(it)
                    if (result != null) {
                        Log.d("AppNavHost", "Meal found: ${result.title}")
                        meal = result
                    } else {
                        Log.e("AppNavHost", "Meal with ID $it not found.")
                    }
                }
            }


            meal?.let {
                CreateUpdateMealScreen(
                    navController = navController,
                    meal = it
                ) { updated, imageUri ->
                    val parsedUri = meal?.image!!.toUri()
                    Log.d("AppNavHost", "Updating Meal with ID: ${it._id}")
                    repo.updateMeal(it._id ?: "", updated, parsedUri)
                }
            }
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

            val tipId = backStackEntry.arguments?.getString("tipId")
            Log.d("AppNavHost", "Navigating to edit_tip with tipId: $tipId")

            LaunchedEffect(tipId) {
                tipId?.let {
                    val result = repo.getTipById(it)
                    if (result != null) {
                        Log.d("AppNavHost", "Tip found: ${result.title}")
                        tip = result
                    } else {
                        Log.e("AppNavHost", "Tip with ID $it not found.")
                    }
                }
            }

            tip?.let {
                CreateUpdateTipScreen(navController = navController, tip = it) { updated, imagePaths ->
                    Log.d("AppNavHost", "Updating tip with ID: ${it._id}")
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
