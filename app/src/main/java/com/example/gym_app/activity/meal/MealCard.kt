package com.example.gym_app.activity.meal

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
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
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = meal.title, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Kategori: ${meal.category}")
            Text(text = "Kalori: ${meal.calories}")
            Spacer(modifier = Modifier.height(8.dp))

            if (isAdmin) {
                Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                    TextButton(onClick = {
                        navController.navigate("meal_edit/${meal._id}")
                    }) {
                        Text("Edit")
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

