package com.example.gym_app.activity.mainactivity

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gym_app.R

@Composable

fun SearchBar() {
    var searchText by remember { mutableStateOf("") }
    OutlinedTextField(
        value = searchText,
        onValueChange = { searchText = it },
        modifier = Modifier.fillMaxWidth().height(50.dp).padding(horizontal = 20.dp),
        placeholder = { Text(text = "Search Workout...",
                        color = Color.White,
                        fontStyle = FontStyle.Italic,
                        fontSize = 16.sp
        )
        },
        trailingIcon = {
            Icon(painter = painterResource(R.drawable.search), contentDescription = null, tint = Color.White)
        },
        singleLine = true,
        shape = RoundedCornerShape(100.dp),
    )
}