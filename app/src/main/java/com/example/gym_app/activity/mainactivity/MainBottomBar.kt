package com.example.gym_app.activity.mainactivity

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.gym_app.R
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import androidx.navigation.compose.currentBackStackEntryAsState


@Composable
fun currentRoute(navController: NavController): String? {
    val navBackStackEntry = navController.currentBackStackEntryAsState()
    return navBackStackEntry.value?.destination?.route
}

@Composable
fun MainButtomBar(navController: NavController) {
    val currentRoute = currentRoute(navController)

    val items = listOf(
        Triple(R.drawable.btn_1, "Home", "main_screen"),
        Triple(R.drawable.btn_2, "Favorite", "favorite_screen"),
        Triple(R.drawable.faq, "FAQ", "faq_screen"),
        Triple(R.drawable.btn_4, "Profile", "profile_screen"),
    )

    NavigationBar(containerColor = colorResource(R.color.darkBlue)) {
        items.forEach { (icon, label, route) ->
            val isSelected = currentRoute == route
            NavigationBarItem(
                selected = isSelected,
                onClick = {
                    if (!isSelected) {
                        navController.navigate(route) {
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                icon = {
                    Icon(
                        painter = painterResource(icon),
                        contentDescription = label,
                        tint = if (isSelected) colorResource(R.color.purple_200) else colorResource(R.color.white),
                        modifier = Modifier.size(24.dp)
                    )
                },
                label = {
                    Text(
                        text = label,
                        color = if (isSelected) colorResource(R.color.purple_200) else colorResource(R.color.white),
                        fontSize = 12.sp
                    )
                }
            )
        }
    }
}
