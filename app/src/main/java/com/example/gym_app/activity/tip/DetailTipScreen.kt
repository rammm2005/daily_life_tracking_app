package com.example.gym_app.activity.tip

import android.util.Log
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.with
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.gym_app.R
import com.example.gym_app.model.Tip
import com.example.gym_app.repository.TipRepository
import kotlinx.coroutines.launch
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.clickable
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.zIndex
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clipToBounds

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun DetailTipScreen(navController: NavController, tipId: String) {
    val context = LocalContext.current
    val repository = remember { TipRepository(context) }
    var tip by remember { mutableStateOf<Tip?>(null) }
    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            tip = repository.getTipById(tipId)
            Log.d("DetailTipScreen", "Tip: $tip")
        }
    }

    val scrollPercent = remember(scrollState.value, scrollState.maxValue) {
        if (scrollState.maxValue > 0) scrollState.value.toFloat() / scrollState.maxValue else 0f
    }

    Scaffold(
        containerColor = colorResource(id = R.color.mainColor),
        topBar = {
            TopAppBar(
                title = { Text("Detail Tip", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colorResource(id = R.color.mainColor)
                )
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(colorResource(id = R.color.mainColor))
        ) {
            if (tip == null) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color.White)
                }
            } else {
                Column {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = "${(scrollPercent * 100).toInt()}%",
                            style = MaterialTheme.typography.labelMedium,
                            color = Color.White,
                            modifier = Modifier.align(Alignment.End)
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        LinearProgressIndicator(
                            progress = scrollPercent,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(4.dp)
                                .zIndex(1f),
                            color = Color(0xFFFF9800),
                            trackColor = Color.White.copy(alpha = 0.1f)
                        )
                    }

                    AnimatedVisibility(
                        visible = true,
                        enter = fadeIn(animationSpec = tween(300)) + slideInVertically(animationSpec = tween(300), initialOffsetY = { it }),
                        exit = fadeOut(animationSpec = tween(300)) + slideOutVertically(animationSpec = tween(300), targetOffsetY = { it })
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(scrollState)
                                .padding(16.dp)
                        ) {
                            val images = tip?.images ?: emptyList()
                            val pagerState = rememberPagerState(
                                initialPage = 0,
                                pageCount = { images.size }
                            )

                            if (images.isNotEmpty()) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(220.dp)
                                        .clip(MaterialTheme.shapes.medium)
                                ) {
                                    if (images.size == 1) {
                                        AsyncImage(
                                            model = images.first(),
                                            contentDescription = "Tip Image",
                                            modifier = Modifier.matchParentSize(),
                                            contentScale = ContentScale.Crop
                                        )
                                    } else {
                                        HorizontalPager(
                                            state = pagerState,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(220.dp)
                                        ) { page ->
                                            AsyncImage(
                                                model = images[page],
                                                contentDescription = "Tip Image $page",
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(220.dp)
                                                    .clip(MaterialTheme.shapes.medium),
                                                contentScale = ContentScale.Crop
                                            )
                                        }

                                        Row(
                                            modifier = Modifier
                                                .align(Alignment.BottomCenter)
                                                .padding(8.dp),
                                            horizontalArrangement = Arrangement.Center
                                        ) {
                                            repeat(images.size) { index ->
                                                val color = if (pagerState.currentPage == index) Color.White else Color.Gray
                                                Box(
                                                    modifier = Modifier
                                                        .padding(horizontal = 4.dp)
                                                        .size(8.dp)
                                                        .background(color = color, shape = CircleShape)
                                                )
                                            }
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(16.dp))
                            }

                            Text(
                                text = tip?.title ?: "",
                                style = MaterialTheme.typography.headlineMedium,
                                color = Color.White
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            AssistChip(
                                onClick = { },
                                label = { Text(tip?.type ?: "Tidak Terkategori") },
                                colors = AssistChipDefaults.assistChipColors(
                                    containerColor = colorResource(R.color.orange),
                                    labelColor = Color.White
                                ),
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = tip?.description_short ?: "",
                                style = MaterialTheme.typography.bodyLarge,
                                color = Color.White
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = tip?.content ?: "",
                                style = MaterialTheme.typography.bodyLarge,
                                color = Color.White
                            )

                            Spacer(modifier = Modifier.height(24.dp))

                            Button(
                                onClick = {
                                    coroutineScope.launch {
                                        scrollState.animateScrollTo(0)
                                    }
                                },
                                modifier = Modifier.align(Alignment.CenterHorizontally)
                            ) {
                                Text("Kembali ke Atas")
                            }

                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                }
            }
        }
    }
}


