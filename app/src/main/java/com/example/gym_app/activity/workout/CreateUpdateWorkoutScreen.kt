package com.example.gym_app.activity.workout

import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.gym_app.R
import com.example.gym_app.model.Lession
import com.example.gym_app.model.Workout
import kotlinx.coroutines.launch
import org.bson.types.ObjectId

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateUpdateWorkoutScreen(
    navController: NavController,
    workout: Workout? = null,
    onSubmit: suspend (Workout, Uri?) -> Unit
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    var title by remember { mutableStateOf(workout?.title ?: "") }
    var description by remember { mutableStateOf(workout?.description ?: "") }
    var selectedCategory by remember { mutableStateOf(workout?.category ?: "") }
    var kcal by remember { mutableStateOf(workout?.kcal?.toString() ?: "") }
    var difficulty by remember { mutableStateOf(workout?.difficulty ?: "") }
    var videoUrl by remember { mutableStateOf(workout?.video_url ?: "") }
    var durationAll by remember { mutableStateOf(workout?.durationAll ?: "") }
    var lessons by remember { mutableStateOf(workout?.lessons ?: listOf(Lession("", "", "", "", ""))) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    var showValidation by remember { mutableStateOf(false) }

    val categories = listOf("Strength Training", "Cardio", "Flexibility", "Running", "Stretching", "HIIT", "Yoga", "Pilates", "CrossFit", "Bodyweight", "Powerlifting", "Functional Training")
    val difficulties = listOf("Beginner", "Intermediate", "Advanced", "Expert")

    val imagePickerLauncher = rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri: Uri? ->
        selectedImageUri = uri
    }

    val isFormValid = title.isNotBlank()
            && description.isNotBlank()
            && durationAll.isNotBlank()
            && kcal.isNotBlank() && kcal.toIntOrNull() != null
            && selectedCategory.isNotBlank()
            && difficulty.isNotBlank()
            && lessons.any {
        it.title.isNotBlank() &&
                it.description.isNotBlank() &&
                it.videoUrl.isNotBlank() &&
                it.duration.isNotBlank()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (workout != null) "Edit Workout" else "Create Workout",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    TextButton(
                        onClick = {
                            if (isFormValid) {
                                scope.launch {
                                    isLoading = true
                                    try {
                                        val newWorkout = Workout(
                                            _id = (workout?._id ?: ObjectId()).toString(),
                                            title = title,
                                            description = description,
                                            category = selectedCategory.takeIf { it.isNotBlank() },
                                            kcal = kcal.toIntOrNull(),
                                            difficulty = difficulty.takeIf { it.isNotBlank() },
                                            video_url = videoUrl.takeIf { it.isNotBlank() },
                                            durationAll = durationAll.takeIf { it.isNotBlank() },
                                            lessons = lessons.filter { it.title.isNotBlank() }
                                        )
                                        val isLocalImage = selectedImageUri?.scheme == "content" || selectedImageUri?.scheme == "file"
                                        val finalImageUri = if (isLocalImage) selectedImageUri else null

                                        Log.d("WorkoutForm", "SelectedImageUri: $selectedImageUri, Scheme: ${selectedImageUri?.scheme}")
                                        Log.d("WorkoutForm", "Image in workout object: ${workout?.picPath}")
                                        Log.d("WorkoutForm", "Final Image URI sent to backend: $finalImageUri")

                                        onSubmit(newWorkout, finalImageUri)
                                        navController.popBackStack()
                                    } finally {
                                        isLoading = false
                                    }
                                }
                            } else {
                                showValidation = true
                            }
                        }
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp,
                                color = Color.White
                            )
                        } else {
                            Text(
                                text = "SAVE",
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colorResource(id = R.color.mainColor)
                )
            )
        },
        containerColor = colorResource(id = R.color.smoothMainColor)
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(colorResource(id = R.color.smoothMainColor))
                .padding(paddingValues)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    WorkoutBasicInfoCard(
                        title = title,
                        description = description,
                        onTitleChange = { title = it },
                        onDescriptionChange = { description = it },
                        showValidation = showValidation
                    )
                }

                item {
                    WorkoutDetailsCard(
                        selectedCategory = selectedCategory,
                        categories = categories,
                        kcal = kcal,
                        difficulty = difficulty,
                        difficulties = difficulties,
                        durationAll = durationAll,
                        onCategoryChange = { selectedCategory = it },
                        onKcalChange = { kcal = it },
                        onDifficultyChange = { difficulty = it },
                        onDurationChange = { durationAll = it },
                        showValidation = showValidation
                    )
                }

                item {
                    MediaSection(
                        videoUrl = videoUrl,
                        selectedImageUri = selectedImageUri,
                        existingImagePath = workout?.picPath,
                        onVideoUrlChange = { videoUrl = it },
                        onImagePickerClick = { imagePickerLauncher.launch("image/*") }
                    )
                }

                item {
                    LessonsSection(
                        lessons = lessons,
                        onLessonsChange = { lessons = it },
                        showValidation = showValidation
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}

@Composable
fun WorkoutBasicInfoCard(
    title: String,
    description: String,
    onTitleChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    showValidation: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = colorResource(id = R.color.mainColor)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.FitnessCenter,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Basic Information",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Column {
                OutlinedTextField(
                    value = title,
                    onValueChange = onTitleChange,
                    label = { Text("Workout Title *", color = Color.White.copy(alpha = 0.8f)) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    isError = showValidation && title.isBlank(),
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Title,
                            contentDescription = null,
                            tint = if (showValidation && title.isBlank()) Color.Red else Color.White
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = colorResource(id = R.color.orange),
                        unfocusedBorderColor = Color.White.copy(alpha = 0.5f),
                        errorBorderColor = Color.Red,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        cursorColor = colorResource(id = R.color.orange)
                    )
                )

                if (showValidation && title.isBlank()) {
                    Text(
                        text = "Workout title is required",
                        color = Color.Red,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                    )
                }
            }

            Column {
                OutlinedTextField(
                    value = description,
                    onValueChange = onDescriptionChange,
                    label = { Text("Description *", color = Color.White.copy(alpha = 0.8f)) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    minLines = 3,
                    maxLines = 5,
                    isError = showValidation && description.isBlank(),
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Description,
                            contentDescription = null,
                            tint = if (showValidation && description.isBlank()) Color.Red else Color.White
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = colorResource(id = R.color.orange),
                        unfocusedBorderColor = Color.White.copy(alpha = 0.5f),
                        errorBorderColor = Color.Red,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        cursorColor = colorResource(id = R.color.orange)
                    )
                )

                if (showValidation && description.isBlank()) {
                    Text(
                        text = "Description is required",
                        color = Color.Red,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutDetailsCard(
    selectedCategory: String,
    categories: List<String>,
    kcal: String,
    difficulty: String,
    difficulties: List<String>,
    durationAll: String,
    onCategoryChange: (String) -> Unit,
    onKcalChange: (String) -> Unit,
    onDifficultyChange: (String) -> Unit,
    onDurationChange: (String) -> Unit,
    showValidation: Boolean
) {
    var categoryExpanded by remember { mutableStateOf(false) }
    var difficultyExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = colorResource(id = R.color.mainColor)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Workout Details",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Column {
                ExposedDropdownMenuBox(
                    expanded = categoryExpanded,
                    onExpandedChange = { categoryExpanded = !categoryExpanded }
                ) {
                    OutlinedTextField(
                        value = selectedCategory,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Category *", color = Color.White.copy(alpha = 0.8f)) },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(
                                expanded = categoryExpanded,
                                modifier = Modifier,
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        shape = RoundedCornerShape(12.dp),
                        isError = showValidation && selectedCategory.isBlank(),
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Category,
                                contentDescription = null,
                                tint = if (showValidation && selectedCategory.isBlank()) Color.Red else Color.White
                            )
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = colorResource(id = R.color.orange),
                            unfocusedBorderColor = Color.White.copy(alpha = 0.5f),
                            errorBorderColor = Color.Red,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedTrailingIconColor = Color.White,
                            unfocusedTrailingIconColor = Color.White.copy(alpha = 0.7f)
                        )
                    )

                    ExposedDropdownMenu(
                        expanded = categoryExpanded,
                        onDismissRequest = { categoryExpanded = false },
                        modifier = Modifier.background(Color.White)
                    ) {
                        categories.forEach { category ->
                            DropdownMenuItem(
                                text = { Text(category, color = Color.Black) },
                                onClick = {
                                    onCategoryChange(category)
                                    categoryExpanded = false
                                }
                            )
                        }
                    }
                }

                if (showValidation && selectedCategory.isBlank()) {
                    Text(
                        text = "Please select a category",
                        color = Color.Red,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                    )
                }
            }

            Column {
                ExposedDropdownMenuBox(
                    expanded = difficultyExpanded,
                    onExpandedChange = { difficultyExpanded = !difficultyExpanded }
                ) {
                    OutlinedTextField(
                        value = difficulty,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Difficulty Level *", color = Color.White.copy(alpha = 0.8f)) },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(
                                expanded = difficultyExpanded
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        shape = RoundedCornerShape(12.dp),
                        isError = showValidation && difficulty.isBlank(),
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.TrendingUp,
                                contentDescription = null,
                                tint = if (showValidation && difficulty.isBlank()) Color.Red else Color.White
                            )
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = colorResource(id = R.color.orange),
                            unfocusedBorderColor = Color.White.copy(alpha = 0.5f),
                            errorBorderColor = Color.Red,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedTrailingIconColor = Color.White,
                            unfocusedTrailingIconColor = Color.White.copy(alpha = 0.7f)
                        )
                    )

                    ExposedDropdownMenu(
                        expanded = difficultyExpanded,
                        onDismissRequest = { difficultyExpanded = false },
                        modifier = Modifier.background(Color.White)
                    ) {
                        difficulties.forEach { diff ->
                            DropdownMenuItem(
                                text = { Text(diff, color = Color.Black) },
                                onClick = {
                                    onDifficultyChange(diff)
                                    difficultyExpanded = false
                                }
                            )
                        }
                    }
                }

                if (showValidation && difficulty.isBlank()) {
                    Text(
                        text = "Please select difficulty level",
                        color = Color.Red,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    OutlinedTextField(
                        value = kcal,
                        onValueChange = onKcalChange,
                        label = { Text("Calories *", color = Color.White.copy(alpha = 0.8f)) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        isError = showValidation && (kcal.isBlank() || kcal.toIntOrNull() == null),
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.LocalFireDepartment,
                                contentDescription = null,
                                tint = if (showValidation && (kcal.isBlank() || kcal.toIntOrNull() == null)) Color.Red else Color.White
                            )
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = colorResource(id = R.color.orange),
                            unfocusedBorderColor = Color.White.copy(alpha = 0.5f),
                            errorBorderColor = Color.Red,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            cursorColor = colorResource(id = R.color.orange)
                        )
                    )

                    if (showValidation && kcal.isBlank()) {
                        Text(
                            text = "Required",
                            color = Color.Red,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(start = 8.dp, top = 2.dp)
                        )
                    } else if (showValidation && kcal.isNotBlank() && kcal.toIntOrNull() == null) {
                        Text(
                            text = "Invalid number",
                            color = Color.Red,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(start = 8.dp, top = 2.dp)
                        )
                    }
                }

                Column(modifier = Modifier.weight(1f)) {
                    OutlinedTextField(
                        value = durationAll,
                        onValueChange = onDurationChange,
                        label = { Text("Duration *", color = Color.White.copy(alpha = 0.8f)) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        isError = showValidation && durationAll.isBlank(),
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Timer,
                                contentDescription = null,
                                tint = if (showValidation && durationAll.isBlank()) Color.Red else Color.White
                            )
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = colorResource(id = R.color.orange),
                            unfocusedBorderColor = Color.White.copy(alpha = 0.5f),
                            errorBorderColor = Color.Red,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            cursorColor = colorResource(id = R.color.orange)
                        )
                    )

                    if (showValidation && durationAll.isBlank()) {
                        Text(
                            text = "Required",
                            color = Color.Red,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(start = 8.dp, top = 2.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MediaSection(
    videoUrl: String,
    selectedImageUri: Uri?,
    existingImagePath: String?,
    onVideoUrlChange: (String) -> Unit,
    onImagePickerClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = colorResource(id = R.color.mainColor)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Image,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Media",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            OutlinedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                onClick = onImagePickerClick,
                colors = CardDefaults.outlinedCardColors(
                    containerColor = Color.White.copy(alpha = 0.1f)
                ),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = androidx.compose.foundation.BorderStroke(
                        2.dp,
                        colorResource(id = R.color.orange)
                    ).brush
                )
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    when {
                        selectedImageUri != null -> {
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(selectedImageUri)
                                    .build(),
                                contentDescription = "Selected image",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                        existingImagePath != null -> {
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(existingImagePath)
                                    .build(),
                                contentDescription = "Existing image",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                        else -> {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AddPhotoAlternate,
                                    contentDescription = null,
                                    modifier = Modifier.size(32.dp),
                                    tint = Color.White
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Tap to select image",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.White.copy(alpha = 0.8f)
                                )
                            }
                        }
                    }
                }
            }

            OutlinedTextField(
                value = videoUrl,
                onValueChange = onVideoUrlChange,
                label = { Text("Video URL (Optional)", color = Color.White.copy(alpha = 0.8f)) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.VideoLibrary,
                        contentDescription = null,
                        tint = Color.White
                    )
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = colorResource(id = R.color.orange),
                    unfocusedBorderColor = Color.White.copy(alpha = 0.5f),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    cursorColor = colorResource(id = R.color.orange)
                )
            )
        }
    }
}

@Composable
fun LessonsSection(
    lessons: List<Lession>,
    onLessonsChange: (List<Lession>) -> Unit,
    showValidation: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = colorResource(id = R.color.mainColor)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayCircle,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Lessons",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                FilledTonalButton(
                    onClick = {
                        onLessonsChange(lessons + Lession("", "", "", "", ""))
                    },
                    modifier = Modifier.height(36.dp),
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = colorResource(id = R.color.orange),
                        contentColor = Color.White
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add Lesson", fontWeight = FontWeight.Medium)
                }
            }

            lessons.forEachIndexed { index, lesson ->
                LessonCard(
                    lesson = lesson,
                    lessonNumber = index + 1,
                    onLessonChange = { updatedLesson ->
                        val updatedLessons = lessons.toMutableList()
                        updatedLessons[index] = updatedLesson
                        onLessonsChange(updatedLessons)
                    },
                    onRemoveLesson = {
                        if (lessons.size > 1) {
                            val updatedLessons = lessons.toMutableList()
                            updatedLessons.removeAt(index)
                            onLessonsChange(updatedLessons)
                        }
                    },
                    canRemove = lessons.size > 1,
                    showValidation = showValidation
                )
            }
        }
    }
}

@Composable
fun LessonCard(
    lesson: Lession,
    lessonNumber: Int,
    onLessonChange: (Lession) -> Unit,
    onRemoveLesson: () -> Unit,
    canRemove: Boolean,
    showValidation: Boolean
) {
    OutlinedCard(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.outlinedCardColors(
            containerColor = Color.White.copy(alpha = 0.1f)
        ),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.foundation.BorderStroke(
                1.dp,
                Color.White.copy(alpha = 0.3f)
            ).brush
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = colorResource(id = R.color.orange),
                        modifier = Modifier.size(24.dp)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Text(
                                text = lessonNumber.toString(),
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = "Lesson $lessonNumber",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Medium,
                        color = Color.White
                    )
                }

                if (canRemove) {
                    IconButton(
                        onClick = onRemoveLesson,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Remove Lesson",
                            tint = colorResource(id = R.color.orange),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Column {
                OutlinedTextField(
                    value = lesson.title,
                    onValueChange = { onLessonChange(lesson.copy(title = it)) },
                    label = { Text("Lesson Title *", color = Color.White.copy(alpha = 0.8f)) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    isError = showValidation && lesson.title.isBlank(),
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Title,
                            contentDescription = null,
                            tint = if (showValidation && lesson.title.isBlank()) Color.Red else Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = colorResource(id = R.color.orange),
                        unfocusedBorderColor = Color.White.copy(alpha = 0.5f),
                        errorBorderColor = Color.Red,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        cursorColor = colorResource(id = R.color.orange)
                    )
                )

                if (showValidation && lesson.title.isBlank()) {
                    Text(
                        text = "Lesson title is required",
                        color = Color.Red,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(start = 8.dp, top = 2.dp)
                    )
                }
            }

            Column {
                OutlinedTextField(
                    value = lesson.description,
                    onValueChange = { onLessonChange(lesson.copy(description = it)) },
                    label = { Text("Lesson Description *", color = Color.White.copy(alpha = 0.8f)) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    minLines = 2,
                    maxLines = 4,
                    isError = showValidation && lesson.description.isBlank(),
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Description,
                            contentDescription = null,
                            tint = if (showValidation && lesson.description.isBlank()) Color.Red else Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = colorResource(id = R.color.orange),
                        unfocusedBorderColor = Color.White.copy(alpha = 0.5f),
                        errorBorderColor = Color.Red,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        cursorColor = colorResource(id = R.color.orange)
                    )
                )

                if (showValidation && lesson.description.isBlank()) {
                    Text(
                        text = "Lesson description is required",
                        color = Color.Red,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(start = 8.dp, top = 2.dp)
                    )
                }
            }

            Column {
                OutlinedTextField(
                    value = lesson.duration,
                    onValueChange = { onLessonChange(lesson.copy(duration = it)) },
                    label = { Text("Duration *", color = Color.White.copy(alpha = 0.8f)) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    isError = showValidation && lesson.duration.isBlank(),
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Timer,
                            contentDescription = null,
                            tint = if (showValidation && lesson.duration.isBlank()) Color.Red else Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = colorResource(id = R.color.orange),
                        unfocusedBorderColor = Color.White.copy(alpha = 0.5f),
                        errorBorderColor = Color.Red,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        cursorColor = colorResource(id = R.color.orange)
                    )
                )

                if (showValidation && lesson.duration.isBlank()) {
                    Text(
                        text = "Duration is required",
                        color = Color.Red,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(start = 8.dp, top = 2.dp)
                    )
                }
            }

            Column {
                OutlinedTextField(
                    value = lesson.videoUrl,
                    onValueChange = { onLessonChange(lesson.copy(videoUrl = it)) },
                    label = { Text("Video URL *", color = Color.White.copy(alpha = 0.8f)) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    isError = showValidation && lesson.videoUrl.isBlank(),
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.VideoLibrary,
                            contentDescription = null,
                            tint = if (showValidation && lesson.videoUrl.isBlank()) Color.Red else Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = colorResource(id = R.color.orange),
                        unfocusedBorderColor = Color.White.copy(alpha = 0.5f),
                        errorBorderColor = Color.Red,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        cursorColor = colorResource(id = R.color.orange)
                    )
                )

                if (showValidation && lesson.videoUrl.isBlank()) {
                    Text(
                        text = "Video URL is required",
                        color = Color.Red,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(start = 8.dp, top = 2.dp)
                    )
                }
            }
        }
    }
}