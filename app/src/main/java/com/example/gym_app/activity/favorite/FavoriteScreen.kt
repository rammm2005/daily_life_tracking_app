package com.example.gym_app.activity.favorite

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.gym_app.R
import com.example.gym_app.component.shimmerBrush
import com.example.gym_app.model.FavoriteItem
import com.example.gym_app.repository.FavoriteRepository
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoriteScreen(navController: NavController, modifier: Modifier = Modifier) {
    var isLoading by remember { mutableStateOf(true) }
    var search by remember { mutableStateOf("") }
    var favorites by remember { mutableStateOf(emptyList<FavoriteItem>()) }
    var showDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val favoriteRepo = remember { FavoriteRepository(context) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        isLoading = true
        val result = favoriteRepo.getFavorites()
        favorites = result?.flatMap { fav ->
            fav.workouts.map {
                FavoriteItem(
                    id = it._id!!,
                    title = it.title,
                    type = "Workout",
                    imageUrl = it.picPath ?: ""
                )
            } + fav.meals.map {
                FavoriteItem(
                    id = it._id!!,
                    title = it.title,
                    type = "Meal",
                    imageUrl = it.image
                )
            } + fav.tips.map {
                FavoriteItem(
                    id = it._id!!,
                    title = it.title,
                    type = "Tip",
                    imageUrl = it.images[0]
                )
            }
        } ?: emptyList()
        isLoading = false
    }

    val filtered = favorites.filter {
        it.title.contains(search, ignoreCase = true) || it.type.contains(search, ignoreCase = true)
    }

    Column(
        modifier = modifier
            .background(colorResource(R.color.smoothMainColor))
            .padding(16.dp)
    ) {
        OutlinedTextField(
            value = search,
            onValueChange = { search = it },
            placeholder = { Text("Cari favorit...", color = Color.Gray) },
            leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = "Search", tint = Color.Gray)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            shape = RoundedCornerShape(12.dp),
            colors = TextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                cursorColor = Color.White,
                focusedContainerColor = Color(0xFF2C2C2E),
                unfocusedContainerColor = Color(0xFF2C2C2E),
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                focusedLeadingIconColor = Color.Gray,
                unfocusedLeadingIconColor = Color.Gray,
                focusedPlaceholderColor = Color.Gray,
                unfocusedPlaceholderColor = Color.Gray
            )
        )

        if (filtered.isNotEmpty()) {
            Button(
                onClick = {
                    showDialog = true
//                    coroutineScope.launch {
//                        filtered.forEach { item ->
//                            favoriteRepo.removeFromFavorite(
//                                tipId = if (item.type == "Tip") item.id else null,
//                                mealId = if (item.type == "Meal") item.id else null,
//                                workoutId = if (item.type == "Workout") item.id else null
//                            )
//                        }
//                        favorites = emptyList()
//                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
            ) {
                Icon(
                    imageVector = Icons.Default.DeleteSweep,
                    contentDescription = "Hapus Semua",
                    tint = Color.White,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text("Hapus Semua Favorit", color = Color.White)
            }
        }

        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("Konfirmasi Hapus") },
                text = { Text("Apakah kamu yakin ingin menghapus semua favorit? Tindakan ini tidak bisa dibatalkan.") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showDialog = false
                            coroutineScope.launch {
                                filtered.forEach { item ->
                                    favoriteRepo.removeFromFavorite(
                                        tipId = if (item.type == "Tip") item.id else null,
                                        mealId = if (item.type == "Meal") item.id else null,
                                        workoutId = if (item.type == "Workout") item.id else null
                                    )
                                }
                                favorites = emptyList()
                            }
                        }
                    ) {
                        Text("Hapus", color = Color.Red)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDialog = false }) {
                        Text("Batal")
                    }
                },
                containerColor = Color.White,
                titleContentColor = Color.Black,
                textContentColor = Color.DarkGray
            )
        }

        when {
            isLoading -> repeat(4) {
                LoadingFavoriteCard()
                Spacer(modifier = Modifier.height(12.dp))
            }

            filtered.isEmpty() -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.bookmark),
                        contentDescription = "Empty Box",
                        modifier = Modifier.size(150.dp)
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Text("You have no favorites 😢", color = Color.White, fontSize = 16.sp)
                }
            }

            else -> {
                filtered.forEach { item ->
                    FavoriteCard(item, navController) { id, type ->
                        coroutineScope.launch {
                            favoriteRepo.removeFromFavorite(
                                tipId = if (type == "Tip") id else null,
                                mealId = if (type == "Meal") id else null,
                                workoutId = if (type == "Workout") id else null
                            )
                            favorites = favorites.filterNot { it.id == id && it.type == type }
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}

@Composable
fun FavoriteCard(item: FavoriteItem, navController: NavController, onDelete: (String, String) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                when (item.type) {
                    "Meal" -> navController.navigate("detail_meal/${item.id}")
                    "Workout" -> navController.navigate("workout_detail/${item.id}")
                    "Tip" -> navController.navigate("detail_tip/${item.id}")
                }
            },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1C1C1E))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = item.imageUrl,
                contentDescription = item.title,
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.title,
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = item.type,
                    color = Color.Gray,
                    fontSize = 12.sp
                )
            }

            IconButton(onClick = { onDelete(item.id, item.type) }) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = Color.White
                )
            }
        }
    }
}

@Composable
fun LoadingFavoriteCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1C1C1E))
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(brush = shimmerBrush())
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Box(
                    modifier = Modifier
                        .height(16.dp)
                        .fillMaxWidth(0.5f)
                        .clip(RoundedCornerShape(4.dp))
                        .background(brush = shimmerBrush())
                )
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .height(12.dp)
                        .fillMaxWidth(0.3f)
                        .clip(RoundedCornerShape(4.dp))
                        .background(brush = shimmerBrush())
                )
            }
        }
    }
}
