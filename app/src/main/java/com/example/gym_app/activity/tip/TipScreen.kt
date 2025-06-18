package com.example.gym_app.activity.tip

import android.util.Log
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.gym_app.R
import com.example.gym_app.model.Tip
import com.example.gym_app.repository.TipRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TipScreen(navController: NavController, isAdmin: Boolean) {
    val context = LocalContext.current
    val api = remember { TipRepository(context) }
    var tips by remember { mutableStateOf<List<Tip>>(emptyList()) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        val response = api.getAllTips()
        Log.d("TIPS", "Result: $response")
        tips = response ?: emptyList()
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
            if (tips.isEmpty()) {
                item {
                    EmptyTipsView()
                }
            } else {
                items(tips) { tip ->
                    TipCard(tip = tip, isAdmin = isAdmin, navController = navController, tipRepository = api,
                        onDeleted = {
                            CoroutineScope(Dispatchers.IO).launch {
                                val response = api.getAllTips()
                                Log.d("TIPS", "Result After Delete: $response")
                                tips = response ?: emptyList()
                            }
                        }
                        )
                }
            }
        }
    }
}

