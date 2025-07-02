package com.example.gym_app.activity.meal

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.gym_app.model.Meal
import com.example.gym_app.repository.MealRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun MealCard(
    meal: Meal,
    isAdmin: Boolean,
    navController: NavController,
    mealRepository: MealRepository,
    onDeleted: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable { navController.navigate("detail_meal/${meal._id}") },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
            ) {
                Image(
                    painter = rememberAsyncImagePainter(meal.image),
                    contentDescription = "Meal Image",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(Color.Transparent, Color.Black.copy(alpha = 0.25f))
                            )
                        )
                )
            }

            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = meal.title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A202C)
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    AssistChip(
                        onClick = {},
                        label = { Text(meal.category, fontSize = 12.sp) },
                        leadingIcon = {
                            Icon(Icons.Default.Fastfood, contentDescription = "Kategori", modifier = Modifier.size(16.dp))
                        },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = Color(0xFFE3F2FD),
                            labelColor = Color(0xFF1565C0),
                            leadingIconContentColor = Color(0xFF1565C0)
                        ),
                        border = BorderStroke(1.dp, Color(0xFF1565C0).copy(alpha = 0.3f))
                    )

                    AssistChip(
                        onClick = {},
                        label = { Text("${meal.calories} kcal", fontSize = 12.sp) },
                        leadingIcon = {
                            Icon(Icons.Default.LocalFireDepartment, contentDescription = "Kalori", modifier = Modifier.size(16.dp))
                        },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = Color(0xFFFFF3E0),
                            labelColor = Color(0xFFE65100),
                            leadingIconContentColor = Color(0xFFE65100)
                        ),
                        border = BorderStroke(1.dp, Color(0xFFE65100).copy(alpha = 0.3f))
                    )
                }

                if (isAdmin) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilledTonalButton(
                            onClick = { navController.navigate("edit_meal/${meal._id}") },
                            colors = ButtonDefaults.filledTonalButtonColors(
                                containerColor = Color(0xFFE8F5E9),
                                contentColor = Color(0xFF2E7D32)
                            ),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit", modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Edit", fontSize = 12.sp)
                        }

                        FilledTonalButton(
                            onClick = { showDeleteDialog = true },
                            colors = ButtonDefaults.filledTonalButtonColors(
                                containerColor = Color(0xFFFFEBEE),
                                contentColor = Color(0xFFD32F2F)
                            ),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Hapus", fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            icon = {
                Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFFD32F2F), modifier = Modifier.size(32.dp))
            },
            title = {
                Text("Konfirmasi Hapus", fontWeight = FontWeight.Bold, color = Color(0xFF1A202C), fontSize = 18.sp)
            },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Apakah Anda yakin ingin menghapus meal ini?", textAlign = TextAlign.Center)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("\"${meal.title}\"", fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("Tindakan ini tidak dapat dibatalkan.", fontSize = 12.sp, color = Color.Gray)
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteDialog = false
                        meal._id?.let { id ->
                            CoroutineScope(Dispatchers.IO).launch {
                                val deleted = mealRepository.deleteMeal(id)
                                if (deleted) onDeleted()
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFD32F2F),
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Hapus")
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showDeleteDialog = false },
                    border = BorderStroke(1.dp, Color(0xFF4A5568)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Batal", color = Color(0xFF4A5568))
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(14.dp)
        )
    }
}
