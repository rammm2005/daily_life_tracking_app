package com.example.gym_app.activity.tip

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.gym_app.R
import com.example.gym_app.model.Tip
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateUpdateTipScreen(
    navController: NavController,
    tip: Tip? = null,
    onSubmit: suspend (Tip) -> Unit
) {
    val context = LocalContext.current

    var title by remember { mutableStateOf(tip?.title ?: "") }
    var content by remember { mutableStateOf(tip?.content ?: "") }
    var image by remember { mutableStateOf(tip?.Image ?: "") }
    var shortDescription by remember { mutableStateOf(tip?.description_short ?: "") }
    var type by remember { mutableStateOf(tip?.type ?: "") }
    var isLoading by remember { mutableStateOf(false) }

    // Error states
    var titleError by remember { mutableStateOf(false) }
    var imageError by remember { mutableStateOf(false) }
    var shortDescError by remember { mutableStateOf(false) }
    var typeError by remember { mutableStateOf(false) }
    var contentError by remember { mutableStateOf(false) }

    val tipTypes = listOf("General", "Nutrition", "Workout")
    var expanded by remember { mutableStateOf(false) }

    val orangeColor = colorResource(id = R.color.orange)
    val darkBlue = colorResource(id = R.color.darkBlue)

    Scaffold(
        containerColor = colorResource(id = R.color.mainColor),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (tip == null) "Buat Tip Baru" else "Edit Tip",
                        fontSize = 20.sp, color = MaterialTheme.colorScheme.onPrimary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali", tint = MaterialTheme.colorScheme.onPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colorResource(id = R.color.mainColor)
                )
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = darkBlue),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {

                    OutlinedTextField(
                        value = title,
                        onValueChange = {
                            title = it
                            titleError = false
                        },
                        label = { Text("Judul") },
                        isError = titleError,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = orangeColor,
                            unfocusedBorderColor = Color.Gray,
                            focusedLabelColor = orangeColor,
                            unfocusedLabelColor = Color.White,
                            cursorColor = Color.White,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            disabledTextColor = Color.LightGray,
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent
                        )
                    )
                    if (titleError) {
                        Text("Judul tidak boleh kosong", color = Color.Red, fontSize = 12.sp)
                    }
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = image,
                        onValueChange = {
                            image = it
                            imageError = false
                        },
                        label = { Text("URL Gambar") },
                        isError = imageError,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = orangeColor,
                            unfocusedBorderColor = Color.Gray,
                            focusedLabelColor = orangeColor,
                            unfocusedLabelColor = Color.White,
                            cursorColor = Color.White,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            disabledTextColor = Color.LightGray,
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent
                        )
                    )
                    if (imageError) {
                        Text("URL Gambar tidak boleh kosong", color = Color.Red, fontSize = 12.sp)
                    }
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = shortDescription,
                        onValueChange = {
                            shortDescription = it
                            shortDescError = false
                        },
                        label = { Text("Deskripsi Singkat") },
                        isError = shortDescError,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = orangeColor,
                            unfocusedBorderColor = Color.Gray,
                            focusedLabelColor = orangeColor,
                            unfocusedLabelColor = Color.White,
                            cursorColor = Color.White,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            disabledTextColor = Color.LightGray,
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent
                        )
                    )
                    if (shortDescError) {
                        Text("Deskripsi Singkat tidak boleh kosong", color = Color.Red, fontSize = 12.sp)
                    }
                    Spacer(modifier = Modifier.height(12.dp))

                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded }
                    ) {
                        OutlinedTextField(
                            value = type,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Tipe") },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded)
                            },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth(),
                            isError = typeError,
                            shape = RoundedCornerShape(10.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = orangeColor,
                                unfocusedBorderColor = Color.Gray,
                                focusedLabelColor = orangeColor,
                                unfocusedLabelColor = Color.White,
                                cursorColor = Color.White,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                disabledTextColor = Color.LightGray,
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent
                            )
                        )

                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            tipTypes.forEach { selectionOption ->
                                DropdownMenuItem(
                                    text = { Text(selectionOption) },
                                    onClick = {
                                        type = selectionOption
                                        typeError = false
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                    if (typeError) {
                        Text("Tipe harus dipilih", color = Color.Red, fontSize = 12.sp)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = content,
                        onValueChange = {
                            content = it
                            contentError = false
                        },
                        label = { Text("Konten Lengkap") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp),
                        shape = RoundedCornerShape(10.dp),
                        maxLines = 10,
                        singleLine = false,
                        isError = contentError,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = orangeColor,
                            unfocusedBorderColor = Color.Gray,
                            focusedLabelColor = orangeColor,
                            unfocusedLabelColor = Color.White,
                            cursorColor = Color.White,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            disabledTextColor = Color.LightGray,
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent
                        )
                    )
                    if (contentError) {
                        Text("Konten tidak boleh kosong", color = Color.Red, fontSize = 12.sp)
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = {
                            titleError = title.isBlank()
                            imageError = image.isBlank()
                            shortDescError = shortDescription.isBlank()
                            typeError = type.isBlank()
                            contentError = content.isBlank()

                            if (titleError || imageError || shortDescError || typeError || contentError) return@Button

                            isLoading = true
                            CoroutineScope(Dispatchers.IO).launch {
                                onSubmit(
                                    Tip(
                                        _id = tip?._id,
                                        Image = image,
                                        title = title,
                                        content = content,
                                        description_short = shortDescription,
                                        type = type
                                    )
                                )
                                withContext(Dispatchers.Main) {
                                    isLoading = false
                                    navController.popBackStack()
                                }
                            }
                        },
                        modifier = Modifier.align(Alignment.End),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = orangeColor,
                            contentColor = Color.White
                        )
                    ) {
                        Text(if (tip == null) "Simpan" else "Update")
                    }
                }
            }
        }
    }
}