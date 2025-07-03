package com.example.gym_app.activity.profile

import SessionManager
import android.app.Activity
import android.content.Intent
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.gym_app.R
import com.example.gym_app.activity.welcomeActivity.WelcomeActivity
import com.example.gym_app.component.shimmerBrush
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavController, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }
    var email by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(true) }
    var showLogoutDialog by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(2000)
        email = runBlocking { sessionManager.userEmail.first() ?: "unknown@gymapp.ai" }
        isLoading = false
    }

    val avatarUrl = "https://api.dicebear.com/8.x/bottts/png?seed=$email"

    Scaffold(
        containerColor = colorResource(R.color.smoothMainColor),
        topBar = {
            TopAppBar(
                title = {
                    Text("Profile", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colorResource(R.color.smoothMainColor)
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .padding(innerPadding)
                .padding(20.dp)
                .fillMaxSize()
                .background(colorResource(R.color.smoothMainColor)),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            if (isLoading) {
                LoadingBox(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                )

                Spacer(modifier = Modifier.height(16.dp))

                LoadingBox(
                    modifier = Modifier
                        .height(20.dp)
                        .width(120.dp)
                        .clip(RoundedCornerShape(4.dp))
                )

                Spacer(modifier = Modifier.height(8.dp))

                LoadingBox(
                    modifier = Modifier
                        .height(14.dp)
                        .width(180.dp)
                        .clip(RoundedCornerShape(4.dp))
                )
            } else {
                Image(
                    painter = rememberAsyncImagePainter(avatarUrl),
                    contentDescription = "User Avatar",
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text("AI Assistant", fontSize = 20.sp, color = Color.White, fontWeight = FontWeight.Bold)
                Text(email, fontSize = 14.sp, color = Color.Gray)
            }

            Spacer(modifier = Modifier.height(24.dp))

            ProfileOption("Edit Profile", Icons.Default.Edit, "edit_profile", navController)
            ProfileOption("Change Password", Icons.Default.LockPerson, "change_password", navController)
            ProfileOption("Notifications", Icons.Default.NotificationsNone, "notifications", navController)
            ProfileOption("Help & Support", Icons.Default.Help, "faq_screen", navController)


            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = { showLogoutDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Logout", color = Color.White, fontWeight = FontWeight.Bold)
            }

            if (showLogoutDialog) {
                AlertDialog(
                    onDismissRequest = { showLogoutDialog = false },
                    containerColor = colorResource(R.color.mainColor),
                    shape = RoundedCornerShape(12.dp),
                    title = {
                        Text(
                            text = "Confirm Logout 😥",
                            color = colorResource(R.color.white),
                            fontWeight = FontWeight.Bold
                        )
                    },
                    text = {
                        Text(
                            text = "Are you sure you want to logout?",
                            color = colorResource(R.color.white),
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    },
                    confirmButton = {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                            horizontalArrangement = Arrangement.End
                        ) {
                            OutlinedButton(
                                onClick = { showLogoutDialog = false },
                                border = BorderStroke(1.dp, colorResource(R.color.white)),
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(end = 4.dp)
                            ) {
                                Text(text = "No", color = colorResource(R.color.white))
                            }

                            Button(
                                onClick = {
                                    coroutineScope.launch {
                                        sessionManager.clearSession()
                                        context.startActivity(Intent(context, WelcomeActivity::class.java))
                                        (context as? Activity)?.finish()
                                    }
                                    showLogoutDialog = false
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = colorResource(R.color.smoothRed),
                                    contentColor = colorResource(R.color.white)
                                ),
                                shape = RoundedCornerShape(6.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(start = 4.dp)
                            ) {
                                Text("Yes")
                            }
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun ProfileOption(
    title: String,
    icon: ImageVector,
    destination: String,
    navController: NavController? = null
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable {
                navController?.navigate(destination)
            },
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1C1C1E))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = title,
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}


@Composable
fun LoadingBox(modifier: Modifier = Modifier, shape: Shape = RoundedCornerShape(4.dp)) {
    Box(
        modifier = modifier
            .background(brush = shimmerBrush(), shape = shape)
    )
}
