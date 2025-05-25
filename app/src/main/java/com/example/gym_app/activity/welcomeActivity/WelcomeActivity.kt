package com.example.gym_app.activity.welcomeActivity

import SessionManager
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.lifecycleScope
import com.example.gym_app.activity.auth.LoginRegisterActivity
import com.example.gym_app.activity.mainactivity.MainActivity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class WelcomeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sessionManager = SessionManager(this)

        lifecycleScope.launch {
            val email = sessionManager.userEmail.first()
            val role = sessionManager.userRole.first()

            Log.d("WelcomeActivity", "email: $email, role: $role")

            if (!email.isNullOrEmpty() && !role.isNullOrEmpty()) {
                startActivity(Intent(this@WelcomeActivity, MainActivity::class.java))
                finish()
            } else {
                setContent {
                    WelcomeScreen(
                        onStarClick = {
                            startActivity(Intent(this@WelcomeActivity, LoginRegisterActivity::class.java))
                            finish()
                        }
                    )
                }
            }
        }
    }
}
