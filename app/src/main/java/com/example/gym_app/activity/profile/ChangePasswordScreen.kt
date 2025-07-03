package com.example.gym_app.activity.profile

import SessionManager
import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.gym_app.R
import com.example.gym_app.activity.welcomeActivity.WelcomeActivity
import com.example.gym_app.repository.UserRepository
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

enum class ChangePasswordStep {
    CONFIRMATION,
    OTP_INPUT,
    NEW_PASSWORD
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangePasswordScreen(
    navController: NavController,
    context: Context = LocalContext.current,
    userRepository: UserRepository = UserRepository()
) {
    val sessionManager = remember { SessionManager(context) }
    val coroutineScope = rememberCoroutineScope()

    var email by remember { mutableStateOf("") }
    var otp by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var step by remember { mutableStateOf(ChangePasswordStep.CONFIRMATION) }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        val savedEmail = sessionManager.userEmail.firstOrNull()
        if (!savedEmail.isNullOrBlank()) {
            email = savedEmail
        }
    }

    Scaffold(
        containerColor = colorResource(R.color.smoothMainColor),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Reset Password",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
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
            modifier = Modifier
                .padding(innerPadding)
                .padding(20.dp)
                .fillMaxSize()
                .background(colorResource(R.color.smoothMainColor)),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            OutlinedTextField(
                value = email,
                onValueChange = {},
                enabled = false,
                label = { Text("Email") },
                leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = Color.White) },
                modifier = Modifier.fillMaxWidth(),
                textStyle = LocalTextStyle.current.copy(color = Color.White)
            )

            Spacer(modifier = Modifier.height(16.dp))

            when (step) {
                ChangePasswordStep.CONFIRMATION -> {
                    Text(
                        text = "Apakah Anda yakin ingin mengganti password?",
                        color = Color.White,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                    Button(
                        onClick = {
                            coroutineScope.launch {
                                isLoading = true
                                val result = userRepository.forgotPassword(email)
                                isLoading = false
                                if (result?.success == true) {
                                    snackbarHostState.showSnackbar("OTP telah dikirim ke email 📧")
                                    step = ChangePasswordStep.OTP_INPUT
                                } else {
                                    snackbarHostState.showSnackbar(result?.message ?: "Gagal mengirim OTP ❌")
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isLoading,
                        colors = ButtonDefaults.buttonColors(containerColor = colorResource(R.color.orange))
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                color = Color.White,
                                strokeWidth = 2.dp,
                                modifier = Modifier.size(20.dp)
                            )
                        } else {
                            Text("Ya, Kirim OTP", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                ChangePasswordStep.OTP_INPUT -> {
                    Text("Masukkan OTP", color = Color.White, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    OtpTextField(
                        otpValue = otp,
                        onOtpChange = { otp = it }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = {
                            if (otp.length == 6) {
                                step = ChangePasswordStep.NEW_PASSWORD
                            } else {
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar("OTP harus 6 digit")
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = colorResource(R.color.orange))
                    ) {
                        Text("Lanjut", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }

                ChangePasswordStep.NEW_PASSWORD -> {
                    Text("Masukkan Password Baru", color = Color.White, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = newPassword,
                        onValueChange = { newPassword = it },
                        label = { Text("New Password") },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = Color.White) },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        textStyle = LocalTextStyle.current.copy(color = Color.White)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = {
                            coroutineScope.launch {
                                isLoading = true
                                val result = userRepository.resetPassword(email, otp, newPassword)
                                isLoading = false
                                if (result?.success == true) {
                                    snackbarHostState.showSnackbar("Password berhasil direset ✅")

                                    coroutineScope.launch {
                                        sessionManager.clearSession()
                                        context.startActivity(Intent(context, WelcomeActivity::class.java))
                                        (context as? Activity)?.finish()
                                    }

                                } else {
                                    snackbarHostState.showSnackbar(result?.message ?: "Reset password gagal ❌")
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isLoading,
                        colors = ButtonDefaults.buttonColors(containerColor = colorResource(R.color.orange))
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                color = Color.White,
                                strokeWidth = 2.dp,
                                modifier = Modifier.size(20.dp)
                            )
                        } else {
                            Text("Reset Password", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun OtpTextField(
    otpValue: String,
    onOtpChange: (String) -> Unit,
    otpLength: Int = 6
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        for (i in 0 until otpLength) {
            SingleOtpBox(
                char = otpValue.getOrNull(i)?.toString() ?: "",
                onCharChange = { c ->
                    val newOtp = otpValue.toCharArray().toMutableList()
                    if (c.isNotEmpty()) {
                        if (otpValue.length < otpLength) {
                            onOtpChange(otpValue + c.last())
                        }
                    } else {
                        if (otpValue.isNotEmpty()) {
                            onOtpChange(otpValue.dropLast(1))
                        }
                    }
                }
            )
        }
    }
}

@Composable
private fun SingleOtpBox(
    char: String,
    onCharChange: (String) -> Unit
) {
    OutlinedTextField(
        value = char,
        onValueChange = { value ->
            if (value.length <= 1 && value.all { it.isDigit() }) {
                onCharChange(value)
            }
        },
        modifier = Modifier
            .width(48.dp)
            .height(56.dp)
            .border(1.dp, Color.White)
            .clip(RoundedCornerShape(16.dp)),
        singleLine = true,
        textStyle = TextStyle(
            fontSize = 20.sp,
            textAlign = TextAlign.Center,
            color = Color.White
        ),
        keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = KeyboardType.Number
        ),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color.White,
            unfocusedBorderColor = Color.Gray,
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White
        )
    )
}
