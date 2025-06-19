package com.example.gym_app.activity.meal

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
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
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp),
        colors = CardDefaults.cardColors(containerColor = colorResource(id = com.example.gym_app.R.color.darkBlue))
    ) {
        Column(
            modifier = Modifier
        ) {
            Image(
                painter = rememberAsyncImagePainter(meal.image),
                contentDescription = "Meal Image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            )

            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = meal.title,
                    style = MaterialTheme.typography.titleLarge.copy(
                        color = colorResource(id = com.example.gym_app.R.color.white)
                    )
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AssistChip(
                        onClick = {},
                        label = {
                            Text(
                                text = "Kategori: ${meal.category}",
                                fontSize = 11.sp
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Fastfood,
                                contentDescription = "Kategori",
                                tint = colorResource(id = com.example.gym_app.R.color.white),
                                modifier = Modifier.size(13.dp)
                            )
                        },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = colorResource(id = com.example.gym_app.R.color.orange),
                            labelColor = colorResource(id = com.example.gym_app.R.color.white)
                        )
                    )

                    AssistChip(
                        onClick = {},
                        label = {
                            Text(
                                text = "Kalori: ${meal.calories}",
                                fontSize = 11.sp
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.LocalFireDepartment,
                                contentDescription = "Kalori",
                                tint = colorResource(id = com.example.gym_app.R.color.white),
                                modifier = Modifier.size(13.dp)
                            )
                        },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = colorResource(id = com.example.gym_app.R.color.orange),
                            labelColor = colorResource(id = com.example.gym_app.R.color.white)
                        )
                    )
                }

            Spacer(modifier = Modifier.height(8.dp))

            if (isAdmin) {
                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TextButton(onClick = {
                        navController.navigate("edit_meal/${meal._id}")
                    }) {
                        Text("Edit", color = colorResource(id = com.example.gym_app.R.color.white))
                    }
                    TextButton(onClick = {
                        CoroutineScope(Dispatchers.IO).launch {
                            mealRepository.deleteMeal(meal._id ?: return@launch)
                            onDeleted()
                        }
                    }) {
                        Text("Hapus", color = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }
    }
    }
}


