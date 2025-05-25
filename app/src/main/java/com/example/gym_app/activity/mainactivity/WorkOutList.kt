package com.example.gym_app.activity.mainactivity

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import com.example.gym_app.model.Workout
import com.example.gym_app.R

@Composable

fun WorkOutList(workouts: List<Workout>)  {
     val context = LocalContext.current
    LazyRow (
        contentPadding = PaddingValues(horizontal = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ){
        items(workouts) { workout ->
            WorkOutCard(workout = workout) {
            }
        }
    }
}

@Composable
fun WorkOutCard(workout: Workout, onClick: () -> Unit) {
    val context = LocalContext.current
    val resId = remember(workout.picPath) {
        context.resources.getIdentifier(workout.picPath, "drawable", context.packageName)
    }
    ConstraintLayout(modifier = Modifier.size(220.dp).clip(RoundedCornerShape(10.dp)).
    background(colorResource(R.color.darkBlue)).clickable{onClick()}
    ){
        val (picRef, titleRef, infoRef) = createRefs()
        Image(painter = painterResource(id=resId),
            contentDescription = workout.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier.clip(RoundedCornerShape(10.dp))
                .constrainAs(picRef) {
                    top.linkTo(parent.top, margin = 8.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
                .size(width = 205.dp, height = 150.dp)
            )
        Text(text = workout.title,
            color  = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            modifier = Modifier.constrainAs(titleRef) {
                top.linkTo(picRef.bottom, margin = 8.dp)
                start.linkTo(picRef.start)
            }
        )
        Row(verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.constrainAs(infoRef) {
                top.linkTo(titleRef.bottom)
                start.linkTo(titleRef.start)
            }) {
            Text(text = workout.durationAll.toString(), color = Color.White, fontSize = 12.sp)
            Text(text = " • ", color = colorResource(R.color.orange))
            Text(text = "${workout.lessons.size} Exercise", color = Color.White, fontSize = 12.sp)
            Text(text = " • ", color = colorResource(R.color.orange))
            Text(text = workout.kcal.toString() + " kcal", color = Color.White, fontSize = 12.sp)

        }
    }

}