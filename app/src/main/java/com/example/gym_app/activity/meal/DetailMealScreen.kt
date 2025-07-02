package com.example.gym_app.activity.meal

import android.util.Log
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.gym_app.R
import com.example.gym_app.component.ParsedText
import com.example.gym_app.model.Meal
import com.example.gym_app.repository.FavoriteRepository
import com.example.gym_app.repository.MealRepository
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun DetailMealScreen(navController: NavController, mealId: String) {
    val context = LocalContext.current
    val repository = remember { MealRepository(context) }
    var meal by remember { mutableStateOf<Meal?>(null) }
    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    val snackbarHostState = remember { SnackbarHostState() }

    val favoriteRepo = remember { FavoriteRepository(context) }
    var liked by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            meal = repository.getMealById(mealId)
            val favoriteList = favoriteRepo.getFavorites()
            liked = favoriteList?.any { it.meals.any { it._id == mealId } } == true
            Log.d("DetailMealScreen", "Meal: $meal")
        }
    }

    val scrollPercent = remember(scrollState.value, scrollState.maxValue) {
        if (scrollState.maxValue > 0) scrollState.value.toFloat() / scrollState.maxValue else 0f
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        colorResource(id = R.color.mainColor),
                        colorResource(id = R.color.mainColor).copy(alpha = 0.9f)
                    )
                )
            )
    ) {

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        )

        if (meal == null) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    modifier = Modifier
                        .size(120.dp)
                        .shadow(8.dp, CircleShape),
                    shape = CircleShape,
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White.copy(alpha = 0.1f)
                    )
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = Color.White,
                            strokeWidth = 3.dp,
                            modifier = Modifier.size(40.dp)
                        )
                    }
                }
            }
        } else {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(4.dp),
                    shape = RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White.copy(alpha = 0.1f)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = { navController.popBackStack() },
                                modifier = Modifier
                                    .background(
                                        Color.White.copy(alpha = 0.2f),
                                        CircleShape
                                    )
                            ) {
                                Icon(
                                    Icons.Default.ArrowBack,
                                    contentDescription = "Back",
                                    tint = Color.White
                                )
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            Text(
                                text = "Detail Meal",
                                style = MaterialTheme.typography.headlineSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            )
                        }

                        IconButton(
                            onClick = {
                                coroutineScope.launch {
                                    if (liked) {
                                        val success = favoriteRepo.removeFromFavorite(mealId = mealId)
                                        if (success) {
                                            liked = false
                                            snackbarHostState.showSnackbar("Dihapus dari favorit")
                                        } else {
                                            snackbarHostState.showSnackbar("Gagal menghapus dari favorit")
                                        }
                                    } else {
                                        val result = favoriteRepo.addToFavorite(mealId = mealId)
                                        if (result != null) {
                                            liked = true
                                            snackbarHostState.showSnackbar("Ditambahkan ke favorit")
                                        } else {
                                            snackbarHostState.showSnackbar("Gagal menambahkan ke favorit")
                                        }
                                    }
                                }
                            },
                            modifier = Modifier
                                .background(
                                    if (liked) Color.Red.copy(alpha = 0.5f) else Color.White.copy(alpha = 0.2f),
                                    CircleShape
                                )
                                .padding(4.dp)
                        ) {
                            Icon(
                                imageVector = if (liked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = "Favorite",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }

            }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White.copy(alpha = 0.1f)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Progress Membaca",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = Color.White,
                                    fontWeight = FontWeight.Medium
                                )
                            )
                            Text(
                                text = "${(scrollPercent * 100).toInt()}%",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = Color(0xFFFF9800),
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        LinearProgressIndicator(
                            progress = scrollPercent,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = Color(0xFFFF9800),
                            trackColor = Color.White.copy(alpha = 0.2f)
                        )
                    }
                }

                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn(tween(500)) + slideInVertically(
                        tween(500),
                        initialOffsetY = { it }
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(scrollState)
                            .padding(16.dp)
                    ) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .shadow(12.dp, RoundedCornerShape(20.dp)),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color.White
                            )
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(280.dp)
                            ) {
                                AsyncImage(
                                    model = meal?.image,
                                    contentDescription = "Meal Image",
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(RoundedCornerShape(20.dp)),
                                    contentScale = ContentScale.Crop
                                )

                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(
                                            Brush.verticalGradient(
                                                colors = listOf(
                                                    Color.Transparent,
                                                    Color.Black.copy(alpha = 0.3f)
                                                ),
                                                startY = 200f
                                            )
                                        )
                                        .clip(RoundedCornerShape(20.dp))
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .shadow(8.dp, RoundedCornerShape(16.dp)),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color.White.copy(alpha = 0.15f)
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(20.dp)
                            ) {
                                Text(
                                    text = meal?.title ?: "",
                                    style = MaterialTheme.typography.headlineMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        lineHeight = 32.sp
                                    )
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    AssistChip(
                                        onClick = {},
                                        label = {
                                            Text(
                                                text = meal?.category ?: "Tidak Terkategori",
                                                fontWeight = FontWeight.Medium,
                                                fontSize = 12.sp
                                            )
                                        },
                                        leadingIcon = {
                                            Icon(
                                                imageVector = Icons.Default.Fastfood,
                                                contentDescription = "Category",
                                                modifier = Modifier.size(16.dp)
                                            )
                                        },
                                        colors = AssistChipDefaults.assistChipColors(
                                            containerColor = Color(0xFFFF9800),
                                            labelColor = Color.White,
                                            leadingIconContentColor = Color.White
                                        ),
                                        modifier = Modifier.shadow(4.dp, RoundedCornerShape(20.dp))
                                    )

                                    AssistChip(
                                        onClick = {},
                                        label = {
                                            Text(
                                                text = "${meal?.calories ?: 0} kcal",
                                                fontWeight = FontWeight.Medium,
                                                fontSize = 12.sp
                                            )
                                        },
                                        leadingIcon = {
                                            Icon(
                                                imageVector = Icons.Default.LocalFireDepartment,
                                                contentDescription = "Calories",
                                                modifier = Modifier.size(16.dp)
                                            )
                                        },
                                        colors = AssistChipDefaults.assistChipColors(
                                            containerColor = Color(0xFFE53E3E),
                                            labelColor = Color.White,
                                            leadingIconContentColor = Color.White
                                        ),
                                        modifier = Modifier.shadow(4.dp, RoundedCornerShape(20.dp))
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .shadow(8.dp, RoundedCornerShape(16.dp)),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color.White.copy(alpha = 0.15f)
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(20.dp)
                            ) {
                                Text(
                                    text = "Deskripsi",
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                ParsedText(text = meal?.description ?: "Tidak ada deskripsi tersedia.")
                            }
                        }

                        Spacer(modifier = Modifier.height(32.dp))

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .shadow(8.dp, RoundedCornerShape(16.dp)),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color.White.copy(alpha = 0.15f)
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(20.dp)
                            ) {
                                Text(
                                    text = "Bahan-Bahan",
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                ParsedText(text = meal?.ingredients.toString() ?: "Tidak ada list bahan-bahan tersedia.")
                            }
                        }


                        Spacer(modifier = Modifier.height(32.dp))

                        AnimatedVisibility(
                            visible = scrollPercent > 0.1f,
                            enter = fadeIn(tween(300)) + scaleIn(tween(300)),
                            exit = fadeOut(tween(300)) + scaleOut(tween(300))
                        ) {
                            Card(
                                modifier = Modifier
                                    .align(Alignment.CenterHorizontally)
                                    .shadow(8.dp, CircleShape),
                                shape = CircleShape,
                                colors = CardDefaults.cardColors(
                                    containerColor = Color(0xFFFF9800)
                                )
                            ) {
                                IconButton(
                                    onClick = {
                                        coroutineScope.launch {
                                            scrollState.animateScrollTo(0)
                                        }
                                    },
                                    modifier = Modifier.padding(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.KeyboardArrowUp,
                                        contentDescription = "Back to Top",
                                        tint = Color.White,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(32.dp))
                    }
                }
            }
        }
    }
}