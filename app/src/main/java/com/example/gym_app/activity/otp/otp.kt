package com.example.gym_app.activity.otp

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gym_app.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OTPVerificationScreen(
    email: String,
    onOTPVerified: () -> Unit,
    onBlocked: () -> Unit,
    onResendOTP: (String) -> Unit,
    onVerifyOTP: (String, (Boolean, String?) -> Unit) -> Unit
) {
    val orange = colorResource(R.color.orange)
    val darkBlue = colorResource(R.color.darkBlue)
    val errorRed = colorResource(R.color.errorRed)
    val white = colorResource(R.color.white)

    var otp by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    var attempts by remember { mutableStateOf(0) }
    var isOtpFocused by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    val maxAttempts = 3

    val cardScale by animateFloatAsState(
        targetValue = if (isLoading) 0.98f else 1f,
        animationSpec = tween(300)
    )

    val otpFieldAlpha by animateFloatAsState(
        targetValue = if (isOtpFocused) 1f else 0.8f,
        animationSpec = tween(200)
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        colorResource(R.color.mainColor),
                        colorResource(R.color.mainColor).copy(alpha = 0.8f)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .scale(cardScale),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = darkBlue),
                elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = "Security",
                        modifier = Modifier.size(64.dp),
                        tint = orange
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Verifikasi OTP",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 28.sp
                        ),
                        color = white,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Masukkan kode 6 digit yang telah dikirim ke email Anda",
                        style = MaterialTheme.typography.bodyMedium,
                        color = white.copy(alpha = 0.8f),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .background(
                                color = white.copy(alpha = 0.1f),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Email,
                            contentDescription = "Email",
                            tint = orange,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = email,
                            color = white,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Medium
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    OutlinedTextField(
                        value = otp,
                        onValueChange = {
                            if (it.length <= 6 && it.all { char -> char.isDigit() }) {
                                otp = it
                                errorMessage = ""
                            }
                        },
                        label = {
                            Text(
                                "Masukkan Kode OTP",
                                color = if (isOtpFocused) orange else white.copy(alpha = 0.7f)
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .alpha(otpFieldAlpha)
                            .onFocusChanged { focusState ->
                                isOtpFocused = focusState.isFocused
                            }
                            .border(
                                width = if (isOtpFocused) 2.dp else 1.dp,
                                color = if (isOtpFocused) orange else white.copy(alpha = 0.3f),
                                shape = RoundedCornerShape(12.dp)
                            ),
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Number
                        ),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = white,
                            unfocusedTextColor = white,
                            focusedBorderColor = orange,
                            unfocusedBorderColor = white.copy(alpha = 0.3f),
                            cursorColor = orange,
                            focusedLabelColor = orange,
                            unfocusedLabelColor = white.copy(alpha = 0.7f)
                        ),
                        textStyle = MaterialTheme.typography.headlineSmall.copy(
                            textAlign = TextAlign.Center,
                            letterSpacing = 8.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )

                    if (errorMessage.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = errorRed.copy(alpha = 0.1f)
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = errorMessage,
                                color = errorRed,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(12.dp),
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    Button(
                        onClick = {
                            if (otp.length == 6) {
                                isLoading = true
                                onVerifyOTP(otp) { success, message ->
                                    isLoading = false
                                    if (success) {
                                        onOTPVerified()
                                    } else {
                                        val newAttempt = attempts + 1
                                        attempts = newAttempt
                                        if (newAttempt >= maxAttempts) {
                                            errorMessage = "Akun diblokir karena terlalu banyak percobaan salah."
                                            onBlocked()
                                        } else {
                                            errorMessage = message ?: "OTP salah. Percobaan ke-$newAttempt dari $maxAttempts."
                                        }
                                    }
                                }
                            } else {
                                errorMessage = "OTP harus terdiri dari 6 digit"
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = orange,
                            contentColor = white
                        ),
                        shape = RoundedCornerShape(16.dp),
                        enabled = !isLoading && otp.length == 6
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = white,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                text = "Verifikasi OTP",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    TextButton(
                        onClick = {
                            onResendOTP(email)
                            errorMessage = ""
                            attempts = 0
                        },
                        enabled = !isLoading
                    ) {
                        Text(
                            text = "Kirim Ulang Kode OTP",
                            color = orange,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.Medium
                            )
                        )
                    }

                    if (attempts > 0) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Percobaan: $attempts/$maxAttempts",
                            color = white.copy(alpha = 0.6f),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }
    }
}
