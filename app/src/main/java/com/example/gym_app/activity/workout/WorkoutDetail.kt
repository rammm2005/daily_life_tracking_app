package com.example.gym_app.activity.workout

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.gym_app.R
import com.example.gym_app.model.Lession
import com.example.gym_app.model.Workout
import com.example.gym_app.repository.WorkoutRepository
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import kotlinx.coroutines.launch
import androidx.core.net.toUri
import com.example.gym_app.repository.FavoriteRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutDetail(navController: NavController, workoutId: String) {
    val context = LocalContext.current
    val repo = remember { WorkoutRepository(context) }
    var workout by remember { mutableStateOf<Workout?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var selectedLessonIndex by remember { mutableStateOf(0) }

    val bottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    var showVideoBottomSheet by remember { mutableStateOf(false) }
    var currentVideoUrl by remember { mutableStateOf("") }
    var currentVideoTitle by remember { mutableStateOf("") }

    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val favoriteRepo = remember { FavoriteRepository(context) }
    var liked by remember { mutableStateOf(false) }



    LaunchedEffect(workoutId) {
        coroutineScope.launch {
            workout = repo.getWorkoutById(workoutId)
            val favoriteList = favoriteRepo.getFavorites()
            liked = favoriteList?.any { it.workouts.any { it._id == workoutId } } == true
            isLoading = false
        }
    }


    Box(modifier = Modifier
        .fillMaxSize()
        .background(colorResource(R.color.mainColor))) {

        workout?.let { w ->
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 100.dp)
            ) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp)
                    ) {
                        AsyncImage(
                            model = w.picPath,
                            contentDescription = w.title,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )

                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(
                                            Color.Transparent,
                                            Color.Black.copy(alpha = 0.7f)
                                        )
                                    )
                                )
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            IconButton(
                                onClick = { navController.popBackStack() },
                                modifier = Modifier
                                    .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                            ) {
                                Icon(
                                    Icons.Default.ArrowBack,
                                    contentDescription = "Back",
                                    tint = Color.White
                                )
                            }

                            Row {
                                val context = LocalContext.current

                                IconButton(
                                    onClick = {
                                        coroutineScope.launch {
                                            if (liked) {
                                                val success = favoriteRepo.removeFromFavorite(workoutId = workoutId)
                                                if (success) {
                                                    liked = false
                                                    snackbarHostState.showSnackbar("Dihapus dari favorit")
                                                } else {
                                                    snackbarHostState.showSnackbar("Gagal menghapus dari favorit")
                                                }
                                            } else {
                                                val result = favoriteRepo.addToFavorite(workoutId = workoutId)
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
                                            if (liked) Color.Red.copy(alpha = 0.5f) else Color.Black.copy(alpha = 0.5f),
                                            CircleShape
                                        )
                                        .padding(4.dp)
                                        .shadow(if (liked) 8.dp else 0.dp, CircleShape)
                                        .animateContentSize()
                                ) {
                                    Icon(
                                        imageVector = if (liked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                        contentDescription = "Like",
                                        tint = Color.White,
                                        modifier = Modifier
                                            .size(if (liked) 30.dp else 24.dp)
                                            .animateContentSize()
                                    )
                                }



                                IconButton(
                                    onClick = {
                                        val intent = Intent().apply {
                                            action = Intent.ACTION_SEND
                                            putExtra(Intent.EXTRA_TEXT, "Coba workout ini: ${w.title}")
                                            type = "text/plain"
                                        }
                                        context.startActivity(Intent.createChooser(intent, "Bagikan via"))
                                    },
                                    modifier = Modifier
                                        .padding(start = 8.dp)
                                        .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Share,
                                        contentDescription = "Share",
                                        tint = Color.White
                                    )
                                }
                            }
                        }


                        Column(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(20.dp)
                        ) {
                            Text(
                                text = w.title,
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                ElegantInfoChip(
                                    icon = Icons.Default.LocalFireDepartment,
                                    text = "${w.kcal} kcal",
                                    backgroundColor = colorResource(R.color.orange)
                                )
                                ElegantInfoChip(
                                    icon = Icons.Default.Timer,
                                    text = w.durationAll ?: "0 min",
                                    backgroundColor = colorResource(R.color.mainColor)
                                )
                                ElegantInfoChip(
                                    icon = Icons.Default.TrendingUp,
                                    text = w.difficulty ?: "Medium",
                                    backgroundColor = when (w.difficulty?.lowercase()) {
                                        "beginner" -> Color(0xFF10B981)
                                        "intermediate" -> Color(0xFFF59E0B)
                                        "advanced" -> Color(0xFFEF4444)
                                        else -> Color.Gray
                                    }
                                )
                            }
                        }
                    }
                }

                if (!w.video_url.isNullOrEmpty()) {
                    item {
                        val context = LocalContext.current
                        val videoId = extractYouTubeVideoId(w.video_url ?: "")

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color.White.copy(alpha = 0.05f)
                            ),
                            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.PlayCircle,
                                        contentDescription = null,
                                        tint = colorResource(R.color.orange),
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(
                                        text = "Video Utama: ${w.title}",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                AndroidView(
                                    factory = { ctx ->
                                        YouTubePlayerView(ctx).apply {
                                            addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
                                                override fun onReady(youTubePlayer: YouTubePlayer) {
                                                    if (videoId.isNotEmpty()) {
                                                        youTubePlayer.cueVideo(videoId, 0f)
                                                    }
                                                }
                                            })
                                        }
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(200.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                OutlinedButton(
                                    onClick = {
                                        val intent = Intent(Intent.ACTION_VIEW, w.video_url.toUri())
                                        context.startActivity(intent)
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        contentColor = colorResource(R.color.orange)
                                    ),
                                    border = BorderStroke(1.dp, colorResource(R.color.orange))
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.OpenInNew,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Buka di YouTube")
                                }
                            }
                        }
                    }
                }


                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .offset(y = (-20).dp),
                        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = colorResource(R.color.mainColor)
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp)
                        ) {
                            StatsSection(workout = w)
                            Spacer(modifier = Modifier.height(24.dp))

                            SectionTitle(title = "Deskripsi", icon = Icons.Default.Description)
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = w.description,
                                style = MaterialTheme.typography.bodyLarge,
                                color = Color.White.copy(alpha = 0.9f),
                                lineHeight = 24.sp
                            )

                            Spacer(modifier = Modifier.height(32.dp))

                            if (w.lessons.isNotEmpty()) {
                                SectionTitle(
                                    title = "Lessons (${w.lessons.size})",
                                    icon = Icons.Default.PlayCircle
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                            }
                        }
                    }
                }

                if (workout?.lessons?.isNotEmpty() == true) {
                    itemsIndexed(workout!!.lessons) { index, lesson ->
                        LessonCard(
                            lesson = lesson,
                            index = index,
                            isSelected = selectedLessonIndex == index,
                            onClick = {
                                selectedLessonIndex = index
                                if (lesson.videoUrl.isNotEmpty()) {
                                    currentVideoUrl = lesson.videoUrl
                                    currentVideoTitle = lesson.title
                                    showVideoBottomSheet = true
                                }
                            },
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }



        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        )


        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(
                        color = colorResource(R.color.orange),
                        strokeWidth = 3.dp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "Loading workout details...",
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }

    if (showVideoBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = {
                showVideoBottomSheet = false
            },
            sheetState = bottomSheetState,
            containerColor = colorResource(R.color.mainColor),
            contentColor = Color.White,
            dragHandle = {
                Surface(
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .size(width = 32.dp, height = 4.dp),
                    shape = RoundedCornerShape(2.dp),
                    color = Color.White.copy(alpha = 0.3f)
                ) {}
            }
        ) {
            YouTubeVideoBottomSheetContent(
                videoUrl = currentVideoUrl,
                videoTitle = currentVideoTitle,
                onDismiss = { showVideoBottomSheet = false }
            )
        }
    }
}

@Composable
fun MainVideoCard(
    videoUrl: String,
    title: String,
    onPlayClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.05f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.PlayCircle,
                    contentDescription = null,
                    tint = colorResource(R.color.orange),
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Video Utama: $title",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.Black.copy(alpha = 0.3f))
                    .clickable { onPlayClick() }
            ) {
                // AsyncImage(model = thumbnailUrl, ...)

                Surface(
                    modifier = Modifier.align(Alignment.Center),
                    shape = CircleShape,
                    color = colorResource(R.color.orange).copy(alpha = 0.9f),
                    shadowElevation = 8.dp
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Play Video",
                        tint = Color.White,
                        modifier = Modifier
                            .size(60.dp)
                            .padding(16.dp)
                    )
                }

                Surface(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(8.dp),
                    shape = RoundedCornerShape(4.dp),
                    color = Color.Black.copy(alpha = 0.7f)
                ) {
                    Text(
                        text = "Tonton Video",
                        color = Color.White,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun YouTubeVideoBottomSheetContent(
    videoUrl: String,
    videoTitle: String,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .navigationBarsPadding()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = Icons.Default.PlayCircle,
                    contentDescription = null,
                    tint = colorResource(R.color.orange),
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = videoTitle,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    maxLines = 2
                )
            }

            IconButton(onClick = onDismiss) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close",
                    tint = Color.White.copy(alpha = 0.7f)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        AndroidView(
            factory = { context ->
                YouTubePlayerView(context).apply {
                    addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
                        override fun onReady(youTubePlayer: YouTubePlayer) {
                            val videoId = extractYouTubeVideoId(videoUrl)
                            if (videoId.isNotEmpty()) {
                                youTubePlayer.loadVideo(videoId, 0f)
                            }
                        }
                    })
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .clip(RoundedCornerShape(12.dp))
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = {
                    val intent = Intent(Intent.ACTION_VIEW, videoUrl.toUri())
                    context.startActivity(intent)
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = colorResource(R.color.orange)
                ),
                border = BorderStroke(1.dp, colorResource(R.color.orange))
            ) {
                Icon(
                    imageVector = Icons.Default.OpenInNew,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Buka di YouTube")
            }

            Button(
                onClick = onDismiss,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White.copy(alpha = 0.1f),
                    contentColor = Color.White
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Tutup")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun ElegantInfoChip(
    icon: ImageVector,
    text: String,
    backgroundColor: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        color = backgroundColor.copy(alpha = 0.9f),
        shadowElevation = 4.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = text,
                color = Color.White,
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.sp
            )
        }
    }
}

@Composable
fun StatsSection(workout: Workout) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.1f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatItems(
                icon = Icons.Default.LocalFireDepartment,
                value = "${workout.kcal}",
                label = "Calories",
                color = colorResource(R.color.orange)
            )

            Divider(
                modifier = Modifier
                    .height(40.dp)
                    .width(1.dp),
                color = Color.White.copy(alpha = 0.3f)
            )

            StatItems(
                icon = Icons.Default.Timer,
                value = workout.durationAll ?: "0",
                label = "Duration",
                color = Color(0xFF3B82F6)
            )

            Divider(
                modifier = Modifier
                    .height(40.dp)
                    .width(1.dp),
                color = Color.White.copy(alpha = 0.3f)
            )

            StatItems(
                icon = Icons.Default.PlayCircle,
                value = "${workout.lessons.size}",
                label = "Lessons",
                color = colorResource(R.color.smoothRed)
            )
        }
    }
}

@Composable
fun StatItems(
    icon: ImageVector,
    value: String,
    label: String,
    color: Color = Color.White
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = Color.White.copy(alpha = 0.7f)
        )
    }
}

@Composable
fun SectionTitle(title: String, icon: ImageVector) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = colorResource(R.color.orange),
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LessonCard(
    lesson: Lession,
    index: Int,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val animatedElevation by animateDpAsState(
        targetValue = if (isSelected) 8.dp else 2.dp,
        animationSpec = tween(300),
        label = "elevation"
    )

    val animatedScale by animateFloatAsState(
        targetValue = if (isSelected) 1.02f else 1f,
        animationSpec = tween(300),
        label = "scale"
    )

    Card(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .graphicsLayer(scaleX = animatedScale, scaleY = animatedScale),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) {
                colorResource(R.color.orange).copy(alpha = 0.1f)
            } else {
                colorResource(R.color.mainColor)
            }
        ),
        border = if (isSelected) {
            BorderStroke(2.dp, colorResource(R.color.orange))
        } else null,
        elevation = CardDefaults.cardElevation(defaultElevation = animatedElevation)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Surface(
                shape = CircleShape,
                color = if (isSelected) {
                    colorResource(R.color.orange)
                } else {
                    colorResource(R.color.mainColor)
                },
                modifier = Modifier.size(40.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = "${index + 1}",
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 16.sp
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = lesson.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Timer,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.7f),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = lesson.duration,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }

                lesson.description.let { desc ->
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = desc,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.6f),
                        maxLines = 2
                    )
                }
            }

            if (lesson.videoUrl.isNotEmpty()) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Play video",
                    tint = if (isSelected) {
                        colorResource(R.color.orange)
                    } else {
                        Color.White.copy(alpha = 0.7f)
                    },
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

fun extractYouTubeVideoId(url: String): String {
    val patterns = listOf(
        "(?<=watch\\?v=)[^&\\n]*",
        "(?<=youtu.be/)[^?\\n]*",
        "(?<=embed/)[^&\\n]*"
    )

    for (pattern in patterns) {
        val regex = Regex(pattern)
        val matchResult = regex.find(url)
        if (matchResult != null) {
            return matchResult.value
        }
    }
    return ""
}