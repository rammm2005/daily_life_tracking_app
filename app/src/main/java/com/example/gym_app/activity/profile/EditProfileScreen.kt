package com.example.gym_app.activity.profile

import SessionManager
import android.content.Context
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.gym_app.R
import com.example.gym_app.network.UserData
import com.example.gym_app.repository.UserRepository
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    navController: NavController,
    context: Context = LocalContext.current,
    userRepository: UserRepository = UserRepository()
) {
    val coroutineScope = rememberCoroutineScope()
    val sessionManager = remember { SessionManager(context) }


    var isLoading by remember { mutableStateOf(false) }

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("") }
    var height by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        val savedEmail = sessionManager.userEmail.firstOrNull()
        if (savedEmail != null) {
            email = savedEmail

            val userData = userRepository.getUserSpesifictByEmail(savedEmail)
            if (userData != null) {
                name = userData.name.orEmpty()
                age = userData.age?.toString().orEmpty()
                gender = userData.gender.orEmpty()
                height = userData.height_cm?.toString().orEmpty()
                weight = userData.weight_kg?.toString().orEmpty()
            } else {
                Log.e("EditProfileScreen", "Failed to fetch user data")
            }
        }
    }

    Scaffold(
        containerColor = colorResource(R.color.smoothMainColor),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Edit Profile",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
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
                .padding(horizontal = 20.dp, vertical = 8.dp)
                .fillMaxSize()
                .background(colorResource(R.color.smoothMainColor)),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            EditTextField(
                value = name,
                onValueChange = { name = it },
                label = "Name",
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = Color.White) }
            )

            EditTextField(
                value = email,
                onValueChange = {},
                label = "Email",
                leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = Color.White) },
                enabled = false
            )

            EditTextField(
                value = age,
                onValueChange = { age = it },
                label = "Age",
                isNumber = true,
                leadingIcon = { Icon(Icons.Default.Cake, contentDescription = null, tint = Color.White) }
            )

            GenderDropdown(
                selectedGender = gender,
                onGenderSelected = { gender = it }
            )


            EditTextField(
                value = height,
                onValueChange = { height = it },
                label = "Height (cm)",
                isNumber = true,
                leadingIcon = { Icon(Icons.Default.Height, contentDescription = null, tint = Color.White) }
            )

            EditTextField(
                value = weight,
                onValueChange = { weight = it },
                label = "Weight (kg)",
                isNumber = true,
                leadingIcon = { Icon(Icons.Default.MonitorWeight, contentDescription = null, tint = Color.White) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    isLoading = true
                    coroutineScope.launch {
                        val request = UserData(
                            name = name.ifBlank { null },
                            age = age.toIntOrNull(),
                            gender = gender.ifBlank { null },
                            height_cm = height.toIntOrNull(),
                            weight_kg = weight.toIntOrNull()
                        )

                        val result = userRepository.updateUserProfile(email, request)

                        if (result?.success == true) {
                            snackbarHostState.showSnackbar("Profile updated successfully ✅")
                            Log.d("EditProfileScreen", "Profile updated: ${result.user}")
                            navController.popBackStack()
                        } else {
                            Log.e("EditProfileScreen", "Failed to update profile")
                            snackbarHostState.showSnackbar("Failed to update profile ❌")
                        }
                        isLoading = true
                    }
                },
                enabled = !isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = colorResource(R.color.orange))
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier
                            .size(20.dp)
                            .padding(end = 8.dp),
                        strokeWidth = 2.dp
                    )
                    Text("Saving...", color = Color.White, fontWeight = FontWeight.Bold)
                } else {
                    Text("Save", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GenderDropdown(
    selectedGender: String,
    onGenderSelected: (String) -> Unit
) {
    val genderOptions = listOf("Male", "Female", "Other")
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        OutlinedTextField(
            value = selectedGender,
            onValueChange = {},
            readOnly = true,
            label = { Text("Gender") },
            leadingIcon = {
                Icon(Icons.Default.Wc, contentDescription = null, tint = Color.White)
            },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth(),
            textStyle = androidx.compose.ui.text.TextStyle(color = Color.White),
            shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                disabledTextColor = Color.Gray,
                errorTextColor = Color.Red,
                focusedContainerColor = Color(0xFF1C1C1E),
                unfocusedContainerColor = Color(0xFF1C1C1E),
                disabledContainerColor = Color.DarkGray,
                errorContainerColor = Color(0xFF1C1C1E),
                cursorColor = Color.White,
                errorCursorColor = Color.Red,
                focusedBorderColor = Color.White,
                unfocusedBorderColor = Color.Gray,
                disabledBorderColor = Color.DarkGray,
                errorBorderColor = Color.Red,
                focusedLabelColor = Color.White,
                unfocusedLabelColor = Color.Gray,
                disabledLabelColor = Color.DarkGray,
                errorLabelColor = Color.Red,
                focusedPlaceholderColor = Color.LightGray,
                unfocusedPlaceholderColor = Color.Gray
            ),
            singleLine = true
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            genderOptions.forEach { genderOption ->
                DropdownMenuItem(
                    text = { Text(genderOption, color = Color.Black) },
                    onClick = {
                        onGenderSelected(genderOption)
                        expanded = false
                    }
                )
            }
        }
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    leadingIcon: @Composable (() -> Unit)? = null,
    isPassword: Boolean = false,
    isNumber: Boolean = false,
    enabled: Boolean = true
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        leadingIcon = leadingIcon,
        enabled = enabled,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        textStyle = androidx.compose.ui.text.TextStyle(color = Color.White),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            disabledTextColor = Color.Gray,
            errorTextColor = Color.Red,
            focusedContainerColor = Color(0xFF1C1C1E),
            unfocusedContainerColor = Color(0xFF1C1C1E),
            disabledContainerColor = Color.DarkGray,
            errorContainerColor = Color(0xFF1C1C1E),
            cursorColor = Color.White,
            errorCursorColor = Color.Red,
            focusedBorderColor = Color.White,
            unfocusedBorderColor = Color.Gray,
            disabledBorderColor = Color.DarkGray,
            errorBorderColor = Color.Red,
            focusedLabelColor = Color.White,
            unfocusedLabelColor = Color.Gray,
            disabledLabelColor = Color.DarkGray,
            errorLabelColor = Color.Red,
            focusedPlaceholderColor = Color.LightGray,
            unfocusedPlaceholderColor = Color.Gray
        ),
        singleLine = true,
        visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
        keyboardOptions = when {
            isNumber -> KeyboardOptions(keyboardType = KeyboardType.Number)
            else -> KeyboardOptions.Default
        }
    )
}
