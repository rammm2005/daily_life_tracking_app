package com.example.gym_app.activity.welcomeActivity

import WelcomeDescription
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.gym_app.R

@Composable
@Preview
fun WelcomeScreen(onStarClick:()-> Unit={}) {
    LazyColumn(modifier = Modifier.fillMaxSize().background(colorResource(R.color.mainColor))){
        item{ WelcomeHeader()}
        item { WelcomeDescription() }
        item {WelcomeActionButton(onStarClick)}
        item {WelcomeFooter()}
    }
}