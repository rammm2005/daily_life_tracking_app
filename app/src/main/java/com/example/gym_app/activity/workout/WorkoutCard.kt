package com.example.gym_app.activity.workout

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gym_app.R
import com.example.gym_app.model.Workout

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchAndFilterSection(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    selectedDifficulty: String,
    onDifficultyChange: (String) -> Unit,
    selectedCategory: String,
    onCategoryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 12.dp),
        shape = RoundedCornerShape(0.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {
//            Text(
//                text = "Cari Workout",
//                style = MaterialTheme.typography.titleMedium,
//                fontWeight = FontWeight.Bold,
//                color = Color.White,
//                modifier = Modifier.padding(bottom = 12.dp)
//            )

            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                placeholder = {
                    Text(
                        "Cari berdasarkan nama atau kategori...",
                        color = Color.Gray
                    )
                },
                leadingIcon = {
                    Icon(
                        Icons.Default.Search,
                        contentDescription = null,
                        tint = colorResource(R.color.orange)
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { onSearchQueryChange("") }) {
                            Icon(
                                Icons.Default.Clear,
                                contentDescription = "Clear search",
                                tint = Color.Gray
                            )
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = colorResource(R.color.orange),
                    unfocusedBorderColor = Color.Gray.copy(alpha = 0.5f),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    cursorColor = colorResource(R.color.orange)
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Tingkat Kesulitan",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = Color.White,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(listOf("All", "Beginner", "Intermediate", "Advanced")) { difficulty ->
                    ElegantFilterChip(
                        text = difficulty,
                        isSelected = selectedDifficulty == difficulty,
                        onClick = { onDifficultyChange(difficulty) },
                        icon = when (difficulty) {
                            "Beginner" -> Icons.Default.TrendingUp
                            "Intermediate" -> Icons.Default.BarChart
                            "Advanced" -> Icons.Default.Whatshot
                            else -> Icons.Default.FilterList
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Kategori",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = Color.White,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(listOf("All", "Cardio", "Strength", "Flexibility", "HIIT", "Yoga")) { category ->
                    ElegantFilterChip(
                        text = category,
                        isSelected = selectedCategory == category,
                        onClick = { onCategoryChange(category) },
                        icon = when (category) {
                            "Cardio" -> Icons.Default.DirectionsRun
                            "Strength" -> Icons.Default.FitnessCenter
                            "Flexibility" -> Icons.Default.SelfImprovement
                            "HIIT" -> Icons.Default.Bolt
                            "Yoga" -> Icons.Default.Spa
                            else -> Icons.Default.Category
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun ElegantFilterChip(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    val animatedScale by animateFloatAsState(
        targetValue = if (isSelected) 1.05f else 1f,
        animationSpec = tween(durationMillis = 200),
        label = "chip_scale"
    )

    val animatedBackgroundColor by animateColorAsState(
        targetValue = if (isSelected) {
            colorResource(R.color.orange)
        } else {
            Color.Transparent
        },
        animationSpec = tween(durationMillis = 200),
        label = "chip_background"
    )

    val animatedTextColor by animateColorAsState(
        targetValue = if (isSelected) {
            Color.White
        } else {
            Color.Gray
        },
        animationSpec = tween(durationMillis = 200),
        label = "chip_text"
    )

    Surface(
        onClick = onClick,
        modifier = modifier.scale(animatedScale),
        shape = RoundedCornerShape(25.dp),
        color = animatedBackgroundColor,
        border = if (!isSelected) {
            androidx.compose.foundation.BorderStroke(
                1.dp,
                Color.Gray.copy(alpha = 0.3f)
            )
        } else null,
        shadowElevation = if (isSelected) 4.dp else 0.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = animatedTextColor,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = text,
                color = animatedTextColor,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutCard(
    workout: Workout,
    isAdmin: Boolean,
    onClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showDeleteDialog by remember { mutableStateOf(false) }


    Column(modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)) {
        if (showDeleteDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showDeleteDialog = false
                            onDeleteClick()
                        }
                    ) {
                        Text("Hapus", color = Color.Red)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteDialog = false }) {
                        Text("Batal", color = Color.White)
                    }
                },
                title = {
                    Text("Konfirmasi Hapus", color = Color.White)
                },
                text = {
                    Text("Apakah kamu yakin ingin menghapus workout \"${workout.title}\"?", color = Color.White)
                },
                containerColor = colorResource(id = R.color.smoothMainColor),
                shape = RoundedCornerShape(16.dp)
            )
        }

        Card(
            onClick = onClick,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = colorResource(R.color.smoothMainColor)),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = workout.title,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = colorResource(R.color.white)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = workout.description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray,
                            maxLines = 2
                        )
                    }
                    if (isAdmin) {
                        Column {
                            IconButton(
                                onClick = onEditClick,
                                modifier = Modifier.size(40.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Edit Workout",
                                    tint = colorResource(id = R.color.orange),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            IconButton(
                                onClick = { showDeleteDialog = true },
                                modifier = Modifier.size(40.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Delete Workout",
                                    tint = colorResource(id = R.color.smoothRed),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    workout.category?.let { category ->
                        InfoChip(
                            icon = Icons.Default.Category,
                            text = category,
                            backgroundColor = colorResource(id = R.color.orange).copy(alpha = 0.1f),
                            textColor = colorResource(id = R.color.white)
                        )
                    }
                    workout.difficulty?.let { difficulty ->
                        InfoChip(
                            icon = Icons.Default.TrendingUp,
                            text = difficulty,
                            backgroundColor = when (difficulty.lowercase()) {
                                "beginner" -> Color(0xFF10B981).copy(alpha = 0.1f)
                                "intermediate" -> Color(0xFFF59E0B).copy(alpha = 0.1f)
                                "advanced" -> Color(0xFFEF4444).copy(alpha = 0.1f)
                                else -> Color.Gray.copy(alpha = 0.1f)
                            },
                            textColor = when (difficulty.lowercase()) {
                                "beginner" -> Color(0xFF10B981)
                                "intermediate" -> Color(0xFFF59E0B)
                                "advanced" -> Color(0xFFEF4444)
                                else -> Color.Gray
                            }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    workout.kcal?.let { kcal ->
                        StatItem(
                            icon = Icons.Default.LocalFireDepartment,
                            value = "$kcal",
                            label = "kcal",
                            color = colorResource(R.color.orange)
                        )
                    }
                    workout.durationAll?.let { duration ->
                        StatItem(
                            icon = Icons.Default.Timer,
                            value = duration,
                            label = "duration",
                            color = Color(0xFF3B82F6)
                        )
                    }
                    if (workout.lessons.isNotEmpty()) {
                        StatItem(
                            icon = Icons.Default.PlayCircle,
                            value = "${workout.lessons.size}",
                            label = "lessons",
                            color = colorResource(R.color.smoothRed)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun InfoChip(
    icon: ImageVector,
    text: String,
    backgroundColor: Color,
    textColor: Color
) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = backgroundColor
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = textColor,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = text,
                color = textColor,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun StatItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    value: String,
    label: String,
    color: Color = Color.Gray
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Column {
            Text(
                text = value,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = Color.Gray
            )
        }
    }
}