package com.example.gym_app.activity.dailytracking

import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.gym_app.model.DailyTracker
import com.example.gym_app.repository.DailyTrackerRepository
import kotlinx.coroutines.launch
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.ui.res.colorResource
import com.example.gym_app.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailyTrackingScreen(navController: NavController) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val repo = remember { DailyTrackerRepository(context) }

    var trackers by remember { mutableStateOf<List<DailyTracker>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    var selectedTrackerForDeleteId by remember { mutableStateOf<String?>(null) }
    var selectedTrackerForDetailId by remember { mutableStateOf<String?>(null) }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    LaunchedEffect(Unit) {
        isLoading = true
        try {
            val result = repo.getAllTrackers()
            trackers = result ?: emptyList()
            error = if (result.isNullOrEmpty()) "Tidak ada data tersedia." else null
        } catch (e: Exception) {
            error = e.localizedMessage ?: "Terjadi kesalahan"
        } finally {
            isLoading = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(id = R.color.mainColor))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
            }
            Text("Daily Tracking", style = MaterialTheme.typography.titleLarge, color = Color.White)
            IconButton(onClick = { navController.navigate("createUpdateDailyTracking") }) {
                Icon(Icons.Default.Add, contentDescription = "Tambah", tint = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (selectedTrackerForDeleteId != null) {
            AlertDialog(
                onDismissRequest = { selectedTrackerForDeleteId = null },
                title = { Text("Konfirmasi Hapus") },
                text = { Text("Apakah kamu yakin ingin menghapus daily tracker ini?") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            scope.launch {
                                selectedTrackerForDeleteId?.let {
                                    repo.deleteTracker(it)
                                    trackers = repo.getAllTrackers() ?: emptyList()
                                }
                                selectedTrackerForDeleteId = null
                            }
                        }
                    ) {
                        Text("Hapus", color = Color.Red)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { selectedTrackerForDeleteId = null }) {
                        Text("Batal")
                    }
                }
            )
        }

        when {
            isLoading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color.White)
                }
            }
            !error.isNullOrEmpty() -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = error ?: "Terjadi kesalahan", color = Color.White)
                }
            }
            else -> {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(trackers.size) { index ->
                        DailyTrackingCard(
                            tracker = trackers[index],
                            context = context,
                            onEditClick = {
                                navController.navigate("createUpdateDailyTracking/${trackers[index]._id}")
                            },
                            onShowClick = {
                                selectedTrackerForDetailId = trackers[index]._id
                            },
                            onDeleteClick = {
                                selectedTrackerForDeleteId = trackers[index]._id
                            }
                        )
                    }
                }
            }
        }

        if (selectedTrackerForDetailId != null) {
            ModalBottomSheet(
                onDismissRequest = { selectedTrackerForDetailId = null },
                sheetState = sheetState,
                containerColor = colorResource(id = R.color.mainColor),
                content = {
                    DetailBottomSheetContent(trackerId = selectedTrackerForDetailId!!) {
                        selectedTrackerForDetailId = null
                    }
                }
            )
        }
    }
}


@Composable
fun DetailBottomSheetContent(trackerId: String, onClose: () -> Unit) {
    val context = LocalContext.current
    val repo = remember { DailyTrackerRepository(context) }
    var tracker by remember { mutableStateOf<DailyTracker?>(null) }

    LaunchedEffect(trackerId) {
        tracker = repo.getTrackerById(trackerId)
    }

    tracker?.let { item ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Text("Detail Tracker", style = MaterialTheme.typography.titleLarge, color = Color.White)
            Spacer(Modifier.height(16.dp))
            DetailRow("Tanggal", item.date)
            DetailRow("Mood", item.mood)
            DetailRow("Berat", "${item.weightKg} kg")
            DetailRow("Tidur", "${item.sleepHours} jam")
            DetailRow("Air Minum", "${item.waterIntakeLiters} L")
            DetailRow("Catatan", item.notes ?: "-")
        }
    } ?: Box(Modifier.fillMaxWidth().padding(24.dp)) {
        CircularProgressIndicator(color = Color.White)
    }
}

@Composable
fun DetailRow(label: String, value: String) {
    Column(modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)) {
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = Color.LightGray)
        Text(text = value, style = MaterialTheme.typography.bodyLarge, color = Color.White)
    }
}
