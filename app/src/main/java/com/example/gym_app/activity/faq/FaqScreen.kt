package com.example.gym_app.activity.faq

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.gym_app.R

data class FaqItem(
    val icon: ImageVector,
    val title: String,
    val description: String,
    val iconColor: Color
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FaqScreen(navController: NavController) {
    var searchQuery by remember { mutableStateOf("") }

    Scaffold(
        containerColor = colorResource(R.color.smoothMainColor),
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colorResource(R.color.smoothMainColor),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                ),
                title = {
                    Text("FAQ", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                }
            )
        }
    ) {
        Column(
            modifier = Modifier
                .padding(it)
                .padding(horizontal = 16.dp)
                .fillMaxSize()
                .background(colorResource(R.color.smoothMainColor))
        ) {
            Spacer(modifier = Modifier.height(16.dp))


            TextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = {
                    Text("Apa yang kamu cari?", color = Color.Gray)
                },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray)
                },
                modifier = Modifier
                    .fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = TextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    cursorColor = Color.White,
                    focusedContainerColor = Color(0xFF2C2C2E),
                    unfocusedContainerColor = Color(0xFF2C2C2E),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedLeadingIconColor = Color.Gray,
                    unfocusedLeadingIconColor = Color.Gray,
                    focusedPlaceholderColor = Color.Gray,
                    unfocusedPlaceholderColor = Color.Gray
                )
            )



            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Popular questions",
                color = Color.White,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            PopularQuestion(text = "Can I cancel my reservation and get a refund?")
            Spacer(modifier = Modifier.height(8.dp))
            PopularQuestion(text = "Can I split the payment with other guests?")
            Spacer(modifier = Modifier.height(24.dp))

            FaqList(searchQuery)
        }
    }
}

@Composable
fun PopularQuestion(text: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1C1C1E))
    ) {
        Text(
            text = text,
            color = Color.White,
            fontSize = 13.sp,
            fontWeight = FontWeight.W500,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Composable
fun FaqList(searchQuery: String) {
    val faqItems = listOf(
        FaqItem(Icons.Default.PlayArrow, "Getting Started", "Cara memulai dan menggunakan aplikasi", Color(0xFF4E8CFF)),
        FaqItem(Icons.Default.AttachMoney, "Payment & Pricing", "Metode pembayaran dan harga", Color(0xFFFFC107)),
        FaqItem(Icons.Default.Book, "Booking & Reservations", "Cara melakukan dan membatalkan reservasi", Color(0xFF4CAF50)),
        FaqItem(Icons.Default.Schedule, "Check-in & Check-out", "Panduan check-in dan check-out", Color(0xFFFF5722)),
        FaqItem(Icons.Default.Security, "Guest Safety", "Informasi tentang keamanan pengguna", Color(0xFF9C27B0))
    )

    val filteredItems = faqItems.filter {
        it.title.contains(searchQuery, ignoreCase = true) ||
                it.description.contains(searchQuery, ignoreCase = true)
    }

    Column {
        filteredItems.forEach { item ->
            FaqCard(item)
            Spacer(modifier = Modifier.height(12.dp))
        }
        if (filteredItems.isEmpty()) {
            Text(
                text = "Tidak ada hasil ditemukan.",
                color = Color.Gray,
                fontSize = 14.sp,
                modifier = Modifier.padding(top = 12.dp)
            )
        }
    }
}

@Composable
fun FaqCard(item: FaqItem) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { /* TODO: navigate to detail */ },
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1C1C1E)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(40.dp),
                color = item.iconColor,
                shape = CircleShape
            ) {
                Icon(
                    imageVector = item.icon,
                    contentDescription = item.title,
                    tint = Color.White,
                    modifier = Modifier
                        .padding(8.dp)
                        .align(Alignment.CenterVertically)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = item.title,
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = item.description,
                    color = Color.Gray,
                    fontSize = 12.sp
                )
            }
        }
    }
}
