package com.example.gym_app.activity.mainactivity

import MenuCard
import android.widget.Space
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.gym_app.model.Workout

@Composable


fun MainContent (
    modifier: Modifier = Modifier,
    workouts: List<Workout>,
    navController: NavController
) {
    Column(modifier = modifier.verticalScroll(rememberScrollState())) {
        Spacer(Modifier.height(16.dp))
        Header()
        SearchBar()
        Spacer(Modifier.height(16.dp))
        MonitoringSection()
        Spacer(Modifier.height(6.dp))
        BannerCard()
        Spacer(Modifier.height(16.dp))
        MenuCard(navController = navController)
        Spacer(Modifier.height(16.dp))
        OtherWorkOutHeader(navController = navController)
        Spacer(Modifier.height(16.dp))
        WorkOutList(navController = navController, workouts)
        Spacer(Modifier.height(16.dp))

    }
}