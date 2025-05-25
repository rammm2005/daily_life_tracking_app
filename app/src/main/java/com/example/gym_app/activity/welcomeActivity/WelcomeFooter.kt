package com.example.gym_app.activity.welcomeActivity

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gym_app.R

@Composable
fun WelcomeFooter() {
    Text(
        text = buildAnnotatedString {
            append("Are you already a member or our community member ?")
            withStyle(SpanStyle(color = colorResource(R.color.orange))) {
                append("Sign In")
            }
        },
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        textAlign = TextAlign.Center,
        color = Color.White,
        fontSize = 18.sp
    )
}
