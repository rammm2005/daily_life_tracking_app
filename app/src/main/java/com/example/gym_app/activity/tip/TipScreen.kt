package com.example.gym_app.activity.tip

import androidx.compose.foundation.Image
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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.gym_app.R
import com.example.gym_app.model.Tip
import com.example.gym_app.repository.TipRepository
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TipScreen(navController: NavController, isAdmin: Boolean) {
    val context = LocalContext.current
    val tipRepository = remember { TipRepository(context) }
    var tips by remember { mutableStateOf<List<Tip>>(emptyList()) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(true) {
        tips = tipRepository.getTips() ?: emptyList()
    }

    Scaffold(
        containerColor = colorResource(id = R.color.mainColor),
        topBar = {
            TopAppBar(
                title = { Text("Tips", color = MaterialTheme.colorScheme.onPrimary) },
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
                    onClick = { navController.navigate("create_tip") },
                    modifier = Modifier.padding(16.dp),
                    containerColor = colorResource(id = R.color.darkBlue)
                ) {
                    Text("+", color = MaterialTheme.colorScheme.onPrimary)
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(colorResource(id = R.color.mainColor))
                .padding(16.dp)
        ) {
            items(tips) { tip ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        AsyncImage(
                            model = tip.Image,
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(180.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = tip.title, style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = tip.description_short ?: "", style = MaterialTheme.typography.bodySmall)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "Tipe: ${tip.type}", style = MaterialTheme.typography.bodySmall)

                        if (isAdmin) {
                            Row(
                                horizontalArrangement = Arrangement.End,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 8.dp)
                            ) {
                                TextButton(onClick = {
                                    navController.navigate("edit_tip/${tip._id}")
                                }) {
                                    Text("Edit")
                                }
                                TextButton(onClick = {
                                    coroutineScope.launch {
                                        tip._id?.let {
                                            tipRepository.deleteTip(it)
                                            tips = tipRepository.getTips() ?: emptyList()
                                        }
                                    }
                                }) {
                                    Text("Hapus", color = MaterialTheme.colorScheme.error)
                                }
                            }
                        }
                    }
                }
            }

            if (tips.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Belum ada tips tersedia.",
                            color = MaterialTheme.colorScheme.onPrimary,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        }
    }
}
