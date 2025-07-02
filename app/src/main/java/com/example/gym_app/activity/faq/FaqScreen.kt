package com.example.gym_app.activity.faq

import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.gym_app.R
import com.example.gym_app.component.shimmerBrush
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FaqScreen(navController: NavController, modifier: Modifier = Modifier) {
    var searchQuery by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(true) }
    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()
    var selectedFaq by remember { mutableStateOf<FaqItem?>(null) }
    val scrollState = rememberScrollState()
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(1500)
        isLoading = false
    }

    if (selectedFaq != null) {
        ModalBottomSheet(
            onDismissRequest = { selectedFaq = null },
            sheetState = bottomSheetState
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = selectedFaq!!.title,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = selectedFaq!!.description,
                    fontSize = 14.sp
                )
            }
        }
    }

    Scaffold(
        modifier.fillMaxSize(),
        containerColor = colorResource(R.color.smoothMainColor),
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colorResource(R.color.smoothMainColor),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                ),
                title = {
                    Text("FAQ", fontSize = 20.sp)
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .verticalScroll(scrollState)
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .fillMaxSize()
                .background(colorResource(R.color.smoothMainColor))
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            TextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Apa yang kamu cari?", color = Color.Gray) },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray)
                },
                modifier = Modifier.fillMaxWidth(),
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
            Text("Popular questions", color = Color.White, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(12.dp))
            PopularQuestion("Apa itu Daily Tracking App?", onClick = {
                selectedFaq = FaqItem(
                    Icons.Default.FitnessCenter,
                    "Apa itu Daily Tracking App?",
                    "Fitur untuk melacak kegiatan harian seperti workout, meal, dan tips kesehatan kamu setiap hari.",
                    Color(0xFF03A9F4)
                )
                scope.launch { bottomSheetState.show() }
            })
            Spacer(modifier = Modifier.height(8.dp))
            PopularQuestion("Siapa itu Bebanteman?", onClick = {
                selectedFaq = FaqItem(
                    Icons.Default.Chat,
                    "Siapa itu Bebanteman?",
                    "Bebanteman adalah chatbot yang menemanimu melakukan tracking harian dan menjawab pertanyaanmu.",
                    Color(0xFFE91E63)
                )
                scope.launch { bottomSheetState.show() }
            })
            Spacer(modifier = Modifier.height(24.dp))
            FaqList(searchQuery, isLoading) {
                selectedFaq = it
                scope.launch { bottomSheetState.show() }
            }
        }
    }
}

@Composable
fun PopularQuestion(text: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1C1C1E))
    ) {
        Text(
            text = text,
            color = Color.White,
            fontSize = 13.sp,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Composable
fun FaqList(searchQuery: String, isLoading: Boolean, onItemClick: (FaqItem) -> Unit) {
    val faqItems = listOf(
        FaqItem(Icons.Default.FitnessCenter, "Apa itu Daily Tracking App?", "Fitur untuk memantau aktivitas harian pengguna seperti workout, meal, dan tips kesehatan setiap hari.", Color(0xFF03A9F4)),
        FaqItem(Icons.Default.Chat, "Siapa itu Bebanteman?", "Bebanteman adalah chatbot yang menemani pengguna untuk tetap konsisten dan semangat dalam melakukan aktivitas harian.", Color(0xFFE91E63)),
        FaqItem(Icons.Default.VerifiedUser, "Cara Menggunakan Daily Tracking me, Bagaimana?", "Daily Tracking Me adalah fitur utama yang membantumu memantau aktivitas harian seperti workout, konsumsi makanan (meal), dan membaca tips kesehatan.\n" +
                "\n" +
                "\uD83D\uDD38 Langkah-langkah penggunaan:\n" +
                "\uD83D\uDD38 Sebelum MASUK Harap Login / Register Terlebih dahulu:\n" +
                "\n" +
                "    Buka aplikasi dan masuk ke halaman utama.\n" +
                "\n" +
                "    Pilih menu Workout, Meal, atau Tips sesuai kebutuhan harianmu.\n" +
                "\n" +
                "    Tap ikon ❤\uFE0F atau “Tambah ke Favorit” untuk menyimpan aktivitas yang kamu lakukan.\n" +
                "\n" +
                "    Kembali ke halaman Daily Tracking, lalu tandai kegiatan yang sudah kamu lakukan hari ini.\n" +
                "\n" +
                "    Kamu juga bisa melihat progres harianmu melalui grafik atau catatan aktivitas.\n" +
                "\n" +
                "\uD83D\uDCAC Tips: Gunakan chatbot Bebanteman untuk bantu menyarankan workout atau meal harian berdasarkan tujuan kamu!", Color(0xFF3F51B5)),
        FaqItem(Icons.Default.TipsAndUpdates, "Apa yang bisa dilakukan Bebanteman?", "Memberikan saran harian, mencatat kegiatanmu, dan membantu kamu tetap termotivasi.", Color(0xFF009688)),
        FaqItem(Icons.Default.FeaturedPlayList, "Apa Saja Fitur Daily Tracking Me?", "Fiturnya ada banyak seperti Chat bot, Tips, Workout, Daily Tracking dan Banyak lagi.", Color(0xFFF7DB00)),
    )
    val filteredItems = faqItems.filter {
        it.title.contains(searchQuery, ignoreCase = true) ||
                it.description.contains(searchQuery, ignoreCase = true)
    }

    Column {
        if (isLoading) {
            repeat(3) {
                LoadingFaqCard()
                Spacer(modifier = Modifier.height(12.dp))
            }
        } else {
            filteredItems.forEach { item ->
                FaqCard(item) { onItemClick(item) }
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
fun FaqCard(item: FaqItem, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1C1C1E))
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(
                modifier = Modifier.size(40.dp),
                color = item.iconColor,
                shape = CircleShape
            ) {
                Icon(
                    imageVector = item.icon,
                    contentDescription = item.title,
                    tint = Color.White,
                    modifier = Modifier.padding(8.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = item.title, color = Color.White, fontSize = 15.sp)
                Text(
                    text = item.description,
                    color = Color.Gray,
                    fontSize = 12.sp,
                    maxLines = 2,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                )
            }
        }
    }
}


@Composable
fun LoadingFaqCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1C1C1E))
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(brush = shimmerBrush())
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.fillMaxWidth()) {
                Box(
                    modifier = Modifier
                        .height(16.dp)
                        .fillMaxWidth(0.5f)
                        .clip(RoundedCornerShape(4.dp))
                        .background(brush = shimmerBrush())
                )
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .height(12.dp)
                        .fillMaxWidth(0.8f)
                        .clip(RoundedCornerShape(4.dp))
                        .background(brush = shimmerBrush())
                )
            }
        }
    }
}

data class FaqItem(
    val icon: ImageVector,
    val title: String,
    val description: String,
    val iconColor: Color
)
