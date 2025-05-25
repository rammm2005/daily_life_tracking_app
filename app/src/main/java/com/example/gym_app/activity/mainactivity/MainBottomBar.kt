package com.example.gym_app.activity.mainactivity

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
import com.example.gym_app.R
@Composable

fun MainButtomBar () {
    NavigationBar(containerColor = colorResource(R.color.darkBlue)) {
        val items = listOf(
            R.drawable.btn_1 to "Home",
            R.drawable.btn_2 to "Favorite",
            R.drawable.faq to "FAQ",
            R.drawable.btn_4 to "Profile",
        )
        items.forEach { (icon, label) ->
            NavigationBarItem(
                selected = false,
                onClick = {

                },
                icon = {
                    Icon(
                        painter = painterResource(icon),
                        contentDescription = label,
                        tint = colorResource(R.color.white),
                        modifier = Modifier.size(24.dp)
                    )
                },
                label = {
                    Text(
                        text = label,
                        color = colorResource(R.color.white),
                        fontSize = 12.sp
                    )
                }
            )
        }
    }
}