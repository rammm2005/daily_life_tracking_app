package com.example.gym_app.activity.meal

import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.gym_app.R
import com.example.gym_app.model.Meal
import com.halilibo.richtext.commonmark.Markdown
import com.halilibo.richtext.ui.material3.RichText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateUpdateMealScreen(
    navController: NavController,
    meal: Meal? = null,
    onSubmit: suspend (Meal, Uri?) -> Unit
) {
    val context = LocalContext.current

    var title by remember { mutableStateOf(meal?.title ?: "") }
    var description by remember { mutableStateOf(TextFieldValue(meal?.description ?: "")) }
    var ingredients by remember { mutableStateOf(TextFieldValue(meal?.ingredients?.joinToString("\n") ?: "")) }
    var calories by remember { mutableStateOf(meal?.calories?.toString() ?: "") }
    var category by remember { mutableStateOf(meal?.category ?: "") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val categoryOptions = listOf("Sarapan", "Makan Siang", "Makan Malam", "Snack / Cheat Day", "Pre-Workout", "Post-Workout", "Makanan Diet", "Makanan Kesehatan", "Makanan Bulking")
    var expanded by remember { mutableStateOf(false) }

    var titleError by remember { mutableStateOf(false) }
    var caloriesError by remember { mutableStateOf(false) }
    var categoryError by remember { mutableStateOf(false) }
    var imageError by remember { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageUri = uri
        imageError = false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(if (meal == null) "Tambah Meal" else "Edit Meal", fontSize = 20.sp)
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colorResource(id = R.color.mainColor),
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(colorResource(id = R.color.mainColor))
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = {
                    title = it
                    titleError = false
                },
                label = { Text("Judul", color = Color.White) },
                isError = titleError,
                modifier = Modifier.fillMaxWidth(),
                textStyle = TextStyle(color = Color.White)
            )
            if (titleError) {
                Text("Judul tidak boleh kosong", color = Color.Red, fontSize = 12.sp)
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = calories,
                onValueChange = {
                    calories = it
                    caloriesError = false
                },
                label = { Text("Kalori", color = Color.White) },
                isError = caloriesError,
                modifier = Modifier.fillMaxWidth(),
                textStyle = TextStyle(color = Color.White)
            )
            if (caloriesError) {
                Text("Kalori harus diisi dan berupa angka", color = Color.Red, fontSize = 12.sp)
            }

            Spacer(modifier = Modifier.height(12.dp))

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = category,
                    onValueChange = { category = it },
                    readOnly = true,
                    label = { Text("Kategori", color = Color.White) },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                    },
                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Color.White,
                        unfocusedBorderColor = Color.LightGray,
                        focusedLabelColor = Color.White,
                        unfocusedLabelColor = Color.LightGray
                    ),
                    isError = categoryError,
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth(),
                    textStyle = TextStyle(color = Color.White)
                )

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    categoryOptions.forEach { selectionOption ->
                        DropdownMenuItem(
                            text = { Text(selectionOption) },
                            onClick = {
                                category = selectionOption
                                categoryError = false
                                expanded = false
                            }
                        )
                    }
                }
            }
            if (categoryError) {
                Text(
                    text = "Kategori tidak boleh kosong",
                    color = Color.Red,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(start = 8.dp, top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Deskripsi (Markdown: **bold**, *italic*, - list, 1. list)", color = Color.White) },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 100.dp, max = 250.dp),
                singleLine = false,
                textStyle = TextStyle(color = Color.White)
            )
            if (description.text.isNotBlank()) {
                Text("Preview Deskripsi:", color = Color.White, style = MaterialTheme.typography.titleSmall, modifier = Modifier.padding(top = 8.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    RichText(modifier = Modifier.padding(8.dp)) {
                        Markdown(content = description.text)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = ingredients,
                onValueChange = { ingredients = it },
                label = { Text("Bahan-bahan (Markdown: **bold**, *italic*, - list, 1. list)", color = Color.White) },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 100.dp, max = 250.dp),
                singleLine = false,
                textStyle = TextStyle(color = Color.White)
            )
            if (ingredients.text.isNotBlank()) {
                Text("Preview Bahan-bahan:", color = Color.White, style = MaterialTheme.typography.titleSmall, modifier = Modifier.padding(top = 8.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    RichText(modifier = Modifier.padding(8.dp)) {
                        Markdown(content = ingredients.text)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = { launcher.launch("image/*") },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorResource(id = R.color.orange),
                    contentColor = Color.White
                )
            ) {
                Text(text = if (imageUri == null && meal?.image.isNullOrEmpty()) "Pilih Gambar" else "Ganti Gambar")
            }

            imageUri?.let {
                Image(
                    painter = rememberAsyncImagePainter(ImageRequest.Builder(context).data(it).build()),
                    contentDescription = "Preview Gambar",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .padding(top = 8.dp)
                )
            } ?: meal?.image?.let {
                Image(
                    painter = rememberAsyncImagePainter(it),
                    contentDescription = "Gambar Meal",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .padding(top = 8.dp)
                )
            }

            if (imageError) {
                Text("Gambar harus dipilih", color = Color.Red, fontSize = 12.sp)
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    titleError = title.isBlank()
                    caloriesError = calories.toIntOrNull() == null
                    categoryError = category.isBlank()
                    imageError = imageUri == null && meal?.image.isNullOrEmpty()

                    if (titleError || caloriesError || categoryError || imageError) {
                        Log.d("Validation", "Error: title=$titleError, calories=$caloriesError, category=$categoryError, image=$imageError")
                        return@Button
                    }

                    CoroutineScope(Dispatchers.IO).launch {
                        onSubmit(
                            Meal(
                                _id = meal?._id,
                                title = title,
                                description = description.text,
                                ingredients = ingredients.text.split("\n").filter { it.isNotBlank() },
                                calories = calories.toInt(),
                                category = category,
                                image = meal?.image ?: "",
                            ),
                            imageUri
                        )
                        withContext(Dispatchers.Main) {
                            navController.popBackStack()
                        }
                    }
                },
                modifier = Modifier.align(Alignment.End),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorResource(id = R.color.orange),
                    contentColor = Color.White
                )
            ) {
                Text(if (meal == null) "Simpan" else "Update")
            }
        }
    }
}