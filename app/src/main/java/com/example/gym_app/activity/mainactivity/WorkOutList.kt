package com.example.gym_app.activity.mainactivity

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import com.example.gym_app.R
import com.example.gym_app.model.Workout

@Composable
fun WorkOutList(navController: NavController, workouts: List<Workout>) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(workouts) { workout ->
            WorkOutCard(workout = workout) {
                navController.navigate("workout_detail/${workout._id}")
            }
        }
    }
}

@Composable
fun WorkOutCard(workout: Workout, onClick: () -> Unit) {
    ConstraintLayout(
        modifier = Modifier
            .size(220.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(colorResource(R.color.darkBlue))
            .clickable { onClick() }
    ) {
        val (picRef, titleRef, infoRef) = createRefs()

        val painter = rememberAsyncImagePainter(model = workout.picPath)
        val imageState = painter.state

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .constrainAs(picRef) {
                    top.linkTo(parent.top, margin = 8.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
                .size(width = 205.dp, height = 150.dp)
                .background(Color.Gray)
        ) {
            AsyncImage(
                model = workout.picPath,
                contentDescription = workout.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.matchParentSize()
            )

//            if (imageState is AsyncImagePainter.State.Loading) {
//                CircularProgressIndicator(
//                    color = Color.White,
//                    modifier = Modifier
//                        .align(Alignment.Center)
//                )
//            }
        }

        Text(
            text = workout.title,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            modifier = Modifier.constrainAs(titleRef) {
                top.linkTo(picRef.bottom, margin = 8.dp)
                start.linkTo(picRef.start)
            }
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.constrainAs(infoRef) {
                top.linkTo(titleRef.bottom)
                start.linkTo(titleRef.start)
            }
        ) {
            Text(text = workout.durationAll ?: "-", color = Color.White, fontSize = 12.sp)
            Text(text = " • ", color = colorResource(R.color.orange))
            Text(text = "${workout.lessons.size} Exercise", color = Color.White, fontSize = 12.sp)
            Text(text = " • ", color = colorResource(R.color.orange))
            Text(text = "${workout.kcal} kcal", color = Color.White, fontSize = 12.sp)
        }
    }
}

