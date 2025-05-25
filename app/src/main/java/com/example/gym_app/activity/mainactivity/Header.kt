package com.example.gym_app.activity.mainactivity

import SessionManager
import android.app.Activity
import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.example.gym_app.R
import com.example.gym_app.activity.welcomeActivity.WelcomeActivity
import kotlinx.coroutines.launch

@Composable
fun Header() {
    val context = LocalContext.current
    val session = remember { SessionManager(context) }
    val scope = rememberCoroutineScope()

    val email by session.userEmail.collectAsState(initial = null)
    val role by session.userRole.collectAsState(initial = null)

    val name = remember(email) {
        email?.substringBefore("@")?.replaceFirstChar { it.uppercase() } ?: "User"
    }
    val isAdmin = remember(role) { role == "admin" }

    var showLogoutDialog by remember { mutableStateOf(false) }

    ConstraintLayout(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp)
    ) {
        val (welcomeRef, nameRef, profileRef, logoutRef) = createRefs()

        Text(
            text = if (isAdmin) "Welcome Admin" else "Welcome Back",
            color = colorResource(R.color.white),
            modifier = Modifier.constrainAs(welcomeRef) {
                top.linkTo(parent.top)
                start.linkTo(parent.start)
            }
        )

        Text(
            text = name,
            color = colorResource(R.color.white),
            fontWeight = FontWeight.Bold,
            modifier = Modifier.constrainAs(nameRef) {
                top.linkTo(welcomeRef.bottom)
                start.linkTo(parent.start)
                bottom.linkTo(profileRef.bottom)
            }
        )

        Image(
            painter = painterResource(R.drawable.profile),
            contentDescription = "Profile",
            modifier = Modifier.constrainAs(profileRef) {
                top.linkTo(parent.top)
                end.linkTo(parent.end)
            },
            contentScale = ContentScale.Crop
        )

        Text(
            text = "Logout",
            color = colorResource(R.color.white),
            modifier = Modifier
                .constrainAs(logoutRef) {
                    top.linkTo(profileRef.bottom)
                    end.linkTo(parent.end)
                }
                .padding(top = 8.dp)
                .clickable {
                    showLogoutDialog = true
                }
        )
    }

    if (showLogoutDialog) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            containerColor = colorResource(R.color.mainColor),
            shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
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
                    androidx.compose.material3.OutlinedButton(
                        onClick = { showLogoutDialog = false },
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(6.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, colorResource(R.color.white)),
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 4.dp)
                    ) {
                        Text(
                            text = "No",
                            color = colorResource(R.color.white)
                        )
                    }

                    androidx.compose.material3.Button(
                        onClick = {
                            scope.launch {
                                session.clearSession()
                                context.startActivity(Intent(context, WelcomeActivity::class.java))
                                (context as? Activity)?.finish()
                            }
                            showLogoutDialog = false
                        },
                        colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                            containerColor = colorResource(R.color.smoothRed),
                            contentColor = colorResource(R.color.white)
                        ),
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(6.dp),
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
