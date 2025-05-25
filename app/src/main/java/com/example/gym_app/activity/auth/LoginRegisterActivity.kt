package com.example.gym_app.activity.auth

import SessionManager
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.gym_app.activity.mainactivity.MainActivity
import com.example.gym_app.activity.otp.BlockedAccountScreen
import com.example.gym_app.activity.otp.OTPVerificationScreen
import com.example.gym_app.repository.AuthRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginRegisterActivity : ComponentActivity() {

    private val repository = AuthRepository()
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sessionManager = SessionManager(this)

        setContent {
            val navController = rememberNavController()

            NavHost(
                navController = navController,
                startDestination = "auth"
            ) {
                composable("auth") {
                    AuthScreen(
                        navController = navController,
                        onLoginSuccess = {
                            startActivity(Intent(this@LoginRegisterActivity, MainActivity::class.java))
                            finish()
                        },
                        onNavigateToOTP = { email ->
                            navController.navigate("otp_verification/$email")
                        }
                    )
                }

                composable("otp_verification/{email}") { backStackEntry ->
                    val email = backStackEntry.arguments?.getString("email") ?: ""

                    OTPVerificationScreen(
                        email = email,
                        onOTPVerified = {
                            lifecycleScope.launch(Dispatchers.IO) {
                                withContext(Dispatchers.Main) {
                                    startActivity(Intent(this@LoginRegisterActivity, MainActivity::class.java))
                                    finish()
                                }
                            }
                        },
                        onBlocked = {
                            navController.navigate("blocked_account") {
                                popUpTo("auth") { inclusive = false }
                            }
                        },
                        onResendOTP = { emailToResend ->
                            lifecycleScope.launch(Dispatchers.IO) {
                                val response = repository.resendOtp(emailToResend)
                                withContext(Dispatchers.Main) {
                                    if (response?.success == true) {
                                        Log.d("OTP", "OTP berhasil dikirim ulang ke $emailToResend")
                                        Toast.makeText(
                                            this@LoginRegisterActivity,
                                            "OTP berhasil dikirim ulang ke $emailToResend",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    } else {
                                        Log.e("OTP", "Gagal kirim ulang OTP: ${response?.message ?: "Unknown error"}")
                                        Toast.makeText(
                                            this@LoginRegisterActivity,
                                            "Gagal kirim ulang OTP: ${response?.message ?: "Unknown error"}",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            }
                        },
                        onVerifyOTP = { otpCode, callback ->
                            val onBlockedCallback = {
                                navController.navigate("blocked_account") {
                                    popUpTo("auth") { inclusive = false }
                                }
                            }
                            lifecycleScope.launch(Dispatchers.IO) {
                                val response = repository.verifyOtp(email, otpCode)
                                withContext(Dispatchers.Main) {
                                    if (response?.success == true) {
                                        val role = response.role ?: ""
                                        Log.d("OTP", "OTP verified for email: $email, Role: $role")
                                        sessionManager.saveUserSession(email, role)
                                        callback(true, null)
                                    } else {
                                        val isBlocked = response?.message?.contains("blokir", ignoreCase = true) == true
                                        callback(false, response?.message ?: "Verifikasi gagal")

                                        if (isBlocked) {
                                            onBlockedCallback()
                                        }
                                    }
                                }
                            }
                        }
                    )
                }

                composable("blocked_account") {
                    BlockedAccountScreen (
                        onBackToLogin = {
                            navController.navigate("auth") {
                                popUpTo("auth") { inclusive = true }
                            }
                        }
                    )
                }
            }
        }
    }
}
