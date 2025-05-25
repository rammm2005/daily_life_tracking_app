package com.example.gym_app.activity.auth

import SessionManager
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.gym_app.R
import com.example.gym_app.connection.MongoDBConnection
import com.example.gym_app.repository.AuthRepository
import com.mongodb.client.MongoCollection
import kotlinx.coroutines.launch
import org.bson.Document

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(onLoginSuccess: (Boolean) -> Unit, onNavigateToOTP: (String) -> Unit, navController: NavController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var isLogin by remember { mutableStateOf(true) }
    var isAdmin by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var nameError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    val sessionManager = remember { SessionManager(context) }


    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

//    val client = MongoDBConnection.getMongoClient()
//    val db = client.getDatabase("tracking_apps")
//    val collection = db.getCollection("users")
//    val databaseName = "tracking_apps"
//    val myDatabase = MongoDBConnection.getDatabase(databaseName)
//    val authRepo = myDatabase?.getCollection("users")?.let { AuthRepository(it) }
    val authRepo = remember { AuthRepository() }


    val gradientBrush = Brush.verticalGradient(
        colors = listOf(
            colorResource(id = R.color.black),
            colorResource(id = R.color.darkBlue),
            colorResource(id = R.color.black)
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = gradientBrush)
            .padding(24.dp)
    ) {
        BackgroundDecorations()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LogoSection()

            Spacer(modifier = Modifier.height(16.dp))

            TitleSection()

            Spacer(modifier = Modifier.height(32.dp))

            AuthCard(
                isLogin = isLogin,
                isAdmin = isAdmin,
                name = name,
                email = email,
                password = password,
                passwordVisible = passwordVisible,
                onNameChange = { name = it },
                onEmailChange = { email = it },
                onPasswordChange = { password = it },
                onPasswordVisibleChange = { passwordVisible = it },
                onAdminChange = { isAdmin = it },
                onIsLoginChange = { newIsLogin -> isLogin = newIsLogin },

                onLoginRegisterClick = {
                    nameError = null
                    emailError = null
                    passwordError = null

                    val emailPattern = (
                            "[a-zA-Z0-9+._%-]{1,256}" +
                                    "@[a-zA-Z0-9][a-zA-Z0-9-]{0,64}" +
                                    "(\\.[a-zA-Z0-9][a-zA-Z0-9-]{0,25})+"
                            ).toRegex()

                    var hasError = false

                    if (!isLogin && name.isBlank()) {
                        nameError = "Name is required"
                        hasError = true
                    }
                    if (email.isBlank()) {
                        emailError = "Email is required"
                        hasError = true
                    } else if (!email.matches(emailPattern)) {
                        emailError = "Invalid email format"
                        hasError = true
                    }
                    if (password.isBlank()) {
                        passwordError = "Password is required"
                        hasError = true
                    }

                    if (hasError) return@AuthCard

                    scope.launch {
                        isLoading = true
                        errorMessage = null
                        try {
                            if (isLogin) {
                                val user = authRepo.login(email, password)
                                if (user != null) {
                                    Log.d("Login", "Login Success as ${user.role}, Email: ${user.email}")
                                    sessionManager.saveUserSession(user.email, user.role)
                                    Toast.makeText(context, "Login Success as ${user.role}", Toast.LENGTH_SHORT).show()
                                    onLoginSuccess(true)
                                } else {
                                    errorMessage = "Login failed. Check credentials."
                                }
                            } else {
                                val newUser = com.example.gym_app.model.User(
                                    name = name,
                                    email = email,
                                    password = password,
                                    age = 0,
                                    gender = "Other",
                                    height_cm = 0,
                                    weight_kg = 0,
                                    role = if (isAdmin) "admin" else "user"
                                )
                                val registered = authRepo.registerUser(newUser)
                                if (registered) {
                                    Toast.makeText(context, "Register Success", Toast.LENGTH_SHORT).show()
                                    onNavigateToOTP(email)
                                } else {
                                    errorMessage = "User already exists"
                                }
                            }
                        } catch (e: Exception) {
                            errorMessage = "Error: ${e.localizedMessage ?: e.message}"
                        }
                        isLoading = false
                    }
                },
                nameError = nameError,
                emailError = emailError,
                passwordError = passwordError,
                errorMessage = errorMessage
            )


            if (isLoading) {
                Spacer(modifier = Modifier.height(16.dp))
                CircularProgressIndicator(color = colorResource(R.color.orange))
            }

            Spacer(modifier = Modifier.height(24.dp))

            TextButton(onClick = { isLogin = !isLogin }) {
                Text(
                    text = if (isLogin) "Don't have an account? Register here"
                    else "Already have an account? Login here",
                    color = Color.White,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun BackgroundDecorations() {
    Box(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .size(300.dp)
                .offset((-50).dp, (-50).dp)
                .background(colorResource(R.color.orange), shape = CircleShape)
        )
        Box(
            modifier = Modifier
                .size(200.dp)
                .align(Alignment.BottomEnd)
                .offset(50.dp, 50.dp)
                .background(colorResource(R.color.orange), shape = CircleShape)
        )
    }
}

@Composable
private fun LogoSection() {
    Box(
        modifier = Modifier
            .size(100.dp)
            .clip(CircleShape)
            .background(Color.White.copy(alpha = 0.1f)),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo_app),
            contentDescription = "Gym App Logo",
            modifier = Modifier.size(80.dp)
        )
    }
}

@Composable
private fun TitleSection() {
    Text(
        text = "Tracker Apps",
        fontSize = 32.sp,
        fontWeight = FontWeight.ExtraBold,
        color = Color.White,
        letterSpacing = 2.sp
    )
    Text(
        text = "Tracking your daily life for free & easy",
        fontSize = 14.sp,
        color = Color.White.copy(alpha = 0.75f),
        modifier = Modifier.padding(bottom = 32.dp)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AuthCard(
    isLogin: Boolean,
    isAdmin: Boolean,
    name: String,
    email: String,
    password: String,
    passwordVisible: Boolean,
    onNameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onPasswordVisibleChange: (Boolean) -> Unit,
    onAdminChange: (Boolean) -> Unit,
    onLoginRegisterClick: () -> Unit,
    onIsLoginChange: (Boolean) -> Unit,
    nameError: String?,
    emailError: String?,
    passwordError: String?,
    errorMessage: String?
) {
    val orange = colorResource(R.color.orange)
    val mainColor = colorResource(R.color.mainColor)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(10.dp, RoundedCornerShape(20.dp)),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = mainColor)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            errorMessage?.let {
                Text(
                    text = it,
                    color = Color.Red,
                    fontSize = 14.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    textAlign = TextAlign.Center
                )
            }

            TabSwitcher(isLogin = isLogin, onTabSelected = onIsLoginChange)

            Spacer(modifier = Modifier.height(24.dp))

            if (!isLogin) {
                AuthTextField(
                    value = name,
                    onValueChange = onNameChange,
                    label = "Full Name",
                    leadingIcon = Icons.Default.Person,
                    tint = orange,
                    errorText = nameError
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            AuthTextField(
                value = email,
                onValueChange = onEmailChange,
                label = "Email",
                leadingIcon = Icons.Default.Email,
                tint = orange,
                keyboardType = KeyboardType.Email,
                errorText = emailError
            )
            Spacer(modifier = Modifier.height(16.dp))

            AuthTextField(
                value = password,
                onValueChange = onPasswordChange,
                label = "Password",
                leadingIcon = Icons.Default.Lock,
                trailingIcon = {
                    IconButton(onClick = { onPasswordVisibleChange(!passwordVisible) }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = "Toggle Password Visibility",
                            tint = orange
                        )
                    }
                },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardType = KeyboardType.Password,
                tint = orange,
                errorText = passwordError
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (!isLogin) {
                AdminCheckbox(isAdmin = isAdmin, onCheckedChange = onAdminChange)
                Spacer(modifier = Modifier.height(8.dp))
                TermsCheckbox()
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onLoginRegisterClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(30.dp),
                colors = ButtonDefaults.buttonColors(containerColor = orange)
            ) {
                Text(
                    text = if (isLogin) "SIGN IN" else "CREATE ACCOUNT",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            if (isLogin) {
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "OR CONTINUE WITH",
                    fontSize = 12.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    SocialLoginButton(
                        icon = R.drawable.google,
                        backgroundColor = mainColor,
                        onClick = { /* TODO: Google login */ }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AuthTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    leadingIcon: ImageVector,
    trailingIcon: @Composable (() -> Unit)? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardType: KeyboardType = KeyboardType.Text,
    tint: Color,
    errorText: String? = null
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, color = tint) },
        leadingIcon = { Icon(leadingIcon, contentDescription = null, tint = tint) },
        trailingIcon = trailingIcon,
        singleLine = true,
        isError = errorText != null,
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = tint,
            unfocusedIndicatorColor = Color.Gray.copy(alpha = 0.5f),
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            cursorColor = tint,
            focusedLabelColor = tint,
            unfocusedLabelColor = Color.White.copy(alpha = 0.7f),
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White.copy(alpha = 0.9f)
        ),
        visualTransformation = visualTransformation,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun TabSwitcher(isLogin: Boolean, onTabSelected: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(colorResource(R.color.darkBlue).copy(alpha = 0.6f), RoundedCornerShape(30.dp))
            .padding(4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        TabButton(
            text = "Login",
            selected = isLogin,
            onClick = { onTabSelected(true) },
            modifier = Modifier.weight(1f)
        )
        TabButton(
            text = "Register",
            selected = !isLogin,
            onClick = { onTabSelected(false) },
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun TabButton(text: String, selected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val backgroundColor = if (selected) colorResource(R.color.orange) else Color.Transparent
    val contentColor = if (selected) Color.White else Color.White.copy(alpha = 0.7f)

    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxHeight()
            .padding(horizontal = 4.dp),
        shape = RoundedCornerShape(30.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = backgroundColor,
            contentColor = contentColor
        ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
    ) {
        Text(
            text = text,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun AdminCheckbox(isAdmin: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            "Login as Admin",
            color = Color.White,
            fontSize = 14.sp,
            modifier = Modifier.weight(1f)
        )
        Checkbox(
            checked = isAdmin,
            onCheckedChange = onCheckedChange,
            colors = CheckboxDefaults.colors(checkedColor = colorResource(R.color.orange))
        )
    }
}

@Composable
private fun TermsCheckbox() {
    var checked by remember { mutableStateOf(false) }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "I agree with the Terms & Conditions",
            color = Color.White,
            fontSize = 14.sp,
            modifier = Modifier.weight(1f)
        )
        Checkbox(
            checked = checked,
            onCheckedChange = { checked = it },
            colors = CheckboxDefaults.colors(checkedColor = colorResource(R.color.orange))
        )
    }
}


@Composable
private fun SocialLoginButton(icon: Int, backgroundColor: Color, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(backgroundColor)
            .border(1.dp, Color.Gray, CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = icon),
            contentDescription = "Social Login",
            modifier = Modifier.size(24.dp)
        )
    }
}
