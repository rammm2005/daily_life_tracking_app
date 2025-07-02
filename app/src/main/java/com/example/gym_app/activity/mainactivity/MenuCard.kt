import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.gym_app.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch









data class MenuItem(val iconRes: Int, val label: String)

val menuItems = listOf(
    MenuItem(R.drawable.excercise, "Workout"),
    MenuItem(R.drawable.healthy_food, "Meal"),
    MenuItem(R.drawable.lifestyle, "Progress"),
    MenuItem(R.drawable.robot, "Chat"),
    MenuItem(R.drawable.chat, "Tips"),
    MenuItem(R.drawable.schedule, "Schedule"),
    MenuItem(R.drawable.success, "Goal"),
    MenuItem(R.drawable.apps, "More")
)

@Composable
fun MenuCard(navController: NavController) {
    val coroutineScope = rememberCoroutineScope()
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        delay(1000)
        isLoading = false
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = colorResource(R.color.smoothMainColor)),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .height(200.dp),
            contentPadding = PaddingValues(8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (isLoading) {
                items(8) {
                    MenuItemSkeleton()
                }
            } else {
                items(menuItems) { item ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                coroutineScope.launch {
                                    when (item.label) {
                                        "Meal" -> navController.navigate("meal_screen")
                                        "Tips" -> navController.navigate("tip_screen")
                                        "Workout" -> navController.navigate("workout_screen")
                                        "Schedule" -> navController.navigate("schedule_screen")
                                        "Progress" -> navController.navigate("progress_screen")
                                        "Chat" -> navController.navigate("chat_screen")
                                        "Goal" -> navController.navigate("goal_screen")
                                        else -> {
                                        }
                                    }
                                }
                            },
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(60.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(
                                            colorResource(R.color.mainColor),
                                            colorResource(R.color.darkBlue)
                                        )
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(id = item.iconRes),
                                contentDescription = item.label,
                                modifier = Modifier.size(30.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = item.label,
                            fontSize = 12.sp,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MenuItemSkeleton() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color.Gray.copy(alpha = 0.3f))
        ) {}
        Spacer(modifier = Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .width(40.dp)
                .height(12.dp)
                .background(Color.Gray.copy(alpha = 0.3f), RoundedCornerShape(4.dp))
        )
    }
}
