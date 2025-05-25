package com.example.gym_app.activity.mainactivity

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.gym_app.R

@Composable

fun BannerCard() {
    Image(
        painter = painterResource(R.drawable.banner),
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = Modifier.background(colorResource(R.color.mainColor))
            .padding(horizontal = 16.dp)
            .height(150.dp)
            .clip(RoundedCornerShape(10.dp)),
    )
}