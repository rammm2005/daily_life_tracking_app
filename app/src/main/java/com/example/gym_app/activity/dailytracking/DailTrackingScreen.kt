package com.example.gym_app.activity.dailytracking

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.gym_app.model.DailyTracker
import com.example.gym_app.repository.DailyTrackerRepository

@Composable
fun DailyTrackingScreen(navController: NavController) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val repo = remember { DailyTrackerRepository(context) }

    var trackers by remember { mutableStateOf<List<DailyTracker>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        isLoading = true
        try {
            val result = repo.getAllTrackers()
            if (!result.isNullOrEmpty()) {
                trackers = result
                error = null
            } else {
                error = "Tidak ada data tersedia."
            }
        } catch (e: Exception) {
            error = e.localizedMessage ?: "Terjadi kesalahan"
        } finally {
            isLoading = false
        }
    }

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(start = 16.dp, end = 16.dp, top = 32.dp, bottom = 16.dp)) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
            Text("Daily Tracking", style = MaterialTheme.typography.titleLarge)
            IconButton(onClick = { navController.navigate("createUpdateDailyTracking") }) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Tambah")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        when {
            isLoading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            !error.isNullOrEmpty() -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = error ?: "Terjadi kesalahan", style = MaterialTheme.typography.bodyLarge)
                }
            }
            else -> {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(trackers.size) { index ->
                        DailyTrackingCard(
                            tracker = trackers[index],
                            onEditClick = {
                                navController.navigate("createUpdateDailyTracking/${trackers[index]._id}")
                            },
                            context = LocalContext.current
                        )
                    }
                }
            }
        }
    }
}
