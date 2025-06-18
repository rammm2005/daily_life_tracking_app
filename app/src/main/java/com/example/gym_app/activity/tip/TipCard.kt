package com.example.gym_app.activity.tip

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.gym_app.R
import com.example.gym_app.model.Tip
import com.example.gym_app.repository.TipRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun TipCard(
    tip: Tip,
    isAdmin: Boolean,
    navController: NavController,
    tipRepository: TipRepository,
    onDeleted: () -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable {
                navController.navigate("detail_tip/${tip._id}")
            },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .background(colorResource(id = R.color.mainColor))
                .padding(16.dp)
        ) {
            val imageUrl = tip.images.firstOrNull()
            if (imageUrl != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(12.dp))
                ) {
                    AsyncImage(
                        model = imageUrl,
                        contentDescription = "Tip Image",
                        modifier = Modifier.matchParentSize(),
                        contentScale = ContentScale.Crop
                    )

                    if (isAdmin) {
                        Row(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            IconButton(
                                onClick = {
                                    tip._id?.let { id ->
                                        navController.navigate("edit_tip/$id")
                                    }
                                },
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(
                                        color = Color.Black.copy(alpha = 0.6f),
                                        shape = CircleShape
                                    )
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Edit",
                                    tint = Color.White
                                )
                            }

                            IconButton(
                                onClick = {
                                    showDialog = true
                                },
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(
                                        color = MaterialTheme.colorScheme.error.copy(alpha = 0.8f),
                                        shape = CircleShape
                                    )
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Hapus",
                                    tint = Color.White
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            Text(
                text = tip.title,
                style = MaterialTheme.typography.titleLarge,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = tip.description_short ?: "",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White
            )
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                TextButton(onClick = {
                    showDialog = false
                    tip._id?.let { id ->
                        CoroutineScope(Dispatchers.IO).launch {
                            val deleted = tipRepository.deleteTip(id)
                            if (deleted) {
                                Log.d("TipCard", "Tip berhasil dihapus")
                                onDeleted()
                            } else {
                                Log.e("TipCard", "Gagal menghapus tip")
                            }
                        }
                    }
                }) {
                    Text("Ya, Hapus")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Batal")
                }
            },
            title = { Text("Konfirmasi Penghapusan") },
            text = { Text("Apakah kamu yakin ingin menghapus tip dengan judul \"${tip.title}\" ini?") },
            containerColor = Color.White
        )
    }
}
