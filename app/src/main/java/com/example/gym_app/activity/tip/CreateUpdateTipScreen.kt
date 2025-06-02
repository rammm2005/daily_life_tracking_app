package com.example.gym_app.activity.tip

import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.gym_app.R
import com.example.gym_app.model.Tip
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.core.net.toUri

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateUpdateTipScreen(
    navController: NavController,
    tip: Tip? = null,
    onSubmit: suspend (Tip, List<Uri>) -> Unit
) {
    val context = LocalContext.current

    var title by remember { mutableStateOf(tip?.title ?: "") }
    var content by remember { mutableStateOf(tip?.content ?: "") }
    var imageUris by remember { mutableStateOf<List<Uri>>(emptyList()) }
    var shortDescription by remember { mutableStateOf(tip?.description_short ?: "") }
    var type by remember { mutableStateOf(tip?.type ?: "") }
    var isLoading by remember { mutableStateOf(false) }


    var titleError by remember { mutableStateOf(false) }
    var imageError by remember { mutableStateOf(false) }
    var shortDescError by remember { mutableStateOf(false) }
    var typeError by remember { mutableStateOf(false) }
    var contentError by remember { mutableStateOf(false) }

    val tipTypes = listOf("General", "Nutrition", "Workout")
    var expanded by remember { mutableStateOf(false) }

    val orangeColor = colorResource(id = R.color.orange)
    val darkBlue = colorResource(id = R.color.darkBlue)

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris: List<Uri> ->
        if (uris.size <= 5) {
            imageUris = uris
            imageError = false
        }
    }

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
        val scrollState = rememberScrollState()
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(scrollState),
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


                    Button(
                        onClick = {
                            launcher.launch("image/*")
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = orangeColor)
                    ) {
                        Text(text = if (imageUris.isEmpty() && tip?.images.isNullOrEmpty()) "Pilih Gambar" else "Ganti Gambar", color = Color.White)                    }
                    Spacer(modifier = Modifier.height(8.dp))


                    if (imageUris.isNotEmpty()) {
                        imageUris.forEach { uri ->
                            Image(
                                painter = rememberAsyncImagePainter(uri),
                                contentDescription = "Preview Gambar",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp)
                                    .padding(8.dp)
                            )
                        }
                    } else if (!tip?.images.isNullOrEmpty()) {
                        tip!!.images.forEach { imageUrl ->
                            Image(
                                painter = rememberAsyncImagePainter(imageUrl),
                                contentDescription = "Preview Gambar",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp)
                                    .padding(8.dp)
                            )
                        }
                    }


                    if (imageError) {
                        Text("Gambar harus dipilih", color = Color.Red, fontSize = 12.sp)
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
                            imageError = (imageUris.isEmpty() && tip?.images.isNullOrEmpty())
                            shortDescError = shortDescription.isBlank()
                            typeError = type.isBlank()
                            contentError = content.isBlank()

                            if (titleError || imageError || shortDescError || typeError || contentError) return@Button
//                            if (titleError || shortDescError || typeError || contentError) return@Button

                            isLoading = true
                            Log.d("TipButton", "Mulai proses submit")
                            CoroutineScope(Dispatchers.IO).launch {
                                val imageList = if (imageUris.isNotEmpty()) {
                                    imageUris
                                } else {
                                    tip?.images?.map { it.toUri() } ?: emptyList()
                                }
                                onSubmit(
                                    Tip(
                                        _id = tip?._id,
                                        images = imageList.map {it.toString()},
                                        title = title,
                                        content = content,
                                        description_short = shortDescription,
                                        type = type
                                    ),
                                    imageList
                                )
                                withContext(Dispatchers.Main) {
                                    Log.d("TipButton", "Selesai submit, kembali ke halaman sebelumnya")
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
                        ),
                        enabled = !isLoading
                    ) {
                        Text(if (tip == null) "Simpan" else "Update")
                    }
                }
            }
        }
    }
}
