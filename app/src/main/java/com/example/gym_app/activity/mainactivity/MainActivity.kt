package com.example.gym_app.activity.mainactivity

import SessionManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.example.gym_app.activity.mainactivity.WorkoutDataProvider.getData
import com.example.gym_app.screen.AppNavHost
import com.example.gym_app.ui.theme.Gym_appTheme

class MainActivity : ComponentActivity() {
//    private val workouts = getData();
    private val sessionManager by lazy { SessionManager(this) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
//            val navController = rememberNavController()
                Gym_appTheme {
                    AppNavHost(sessionManager = sessionManager)
                }
//                Scaffold(
//                    containerColor = Color(0Xff101322),
//                    bottomBar = { MainButtomBar() },
//                   ) { innerPadding ->
//                    MainContent(modifier = Modifier.padding(innerPadding)
//                        .fillMaxSize(),
//                        workouts = workouts,
//                        navController = navController  )
//                }
        }
    }
}
