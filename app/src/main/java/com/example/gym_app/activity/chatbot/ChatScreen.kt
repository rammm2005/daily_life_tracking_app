package com.example.gym_app.activity.chatbot

import SessionManager
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.twotone.ChatBubbleOutline
import androidx.compose.material.icons.twotone.CreateNewFolder
import androidx.compose.material.icons.twotone.EditAttributes
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.gym_app.R
import com.example.gym_app.model.Message
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatBotScreen(navController: NavController) {
    val context = LocalContext.current
    val viewModel = remember { ChatViewModel(context) }
    val messages by viewModel.messages.collectAsState()
    val input by viewModel.input.collectAsState()
    val isThinking by viewModel.isThinking.collectAsState()
    val username by viewModel.username.collectAsState()
    val email by viewModel.email.collectAsState()

    val suggestions by viewModel.suggestions.collectAsState()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()

    val chatSessions by viewModel.chatSessions.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadChatSessions()
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Spacer(modifier = Modifier.height(12.dp))

                Image(
                    painter = painterResource(id = R.drawable.robot),
                    contentDescription = "Menu Header",
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .height(40.dp)
                )


                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Riwayat Chat",
                    style = MaterialTheme.typography.labelLarge,
                    color = Color.Gray,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                chatSessions.forEach { session ->
                    NavigationDrawerItem(
                        label = {
                            Column {
                                Text(session.title ?: "Chat tanpa judul")
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = formatIsoDateToIndonesian(session.startedAt),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.Gray
                                )
                            }
                        },
                        selected = false,
                        onClick = {
                            viewModel.loadMessagesBySession(session._id)
                        },
                        icon = {
                            Icon(
                                imageVector = Icons.TwoTone.ChatBubbleOutline,
                                contentDescription = "Chat"
                            )
                        },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                    )
                }
            }
        },
        content = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFEFEFEF))
            ) {
                ChatTopBar(
                    onBackClick = { navController.popBackStack() },
                    onMenuClick = {
                        coroutineScope.launch { drawerState.open() }
                    }
                )

                if (messages.isEmpty() && !isThinking) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f)
                            .padding(16.dp)
                    ) {
                        EmptyChatState()
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .padding(16.dp),
                        contentPadding = PaddingValues(top = 16.dp, bottom = 80.dp),
                        reverseLayout = true
                    ) {
                        items(messages.reversed()) { msg ->
                            ChatBubble(
                                isBot = msg.role == "bot",
                                message = msg.content,
                                avatarUrl = if (msg.role == "bot")
                                    "https://cdn-icons-png.flaticon.com/512/18611/18611364.png"
                                else
                                    generateAvatarUrlFromUsername(username),
                                username = if (msg.role == "bot") "BebanBot" else username
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }

                        if (isThinking) {
                            item {
                                ChatBubble(
                                    isBot = true,
                                    message = "Sedang memproses...",
                                    avatarUrl = "https://cdn-icons-png.flaticon.com/512/18611/18611364.png",
                                    username = "BebanBot"
                                )
                            }
                        }
                    }
                }

                if (suggestions.isNotEmpty()) {
                    SuggestionMessageRow(
                        suggestions = suggestions,
                        onClick = {
                            viewModel.onInputChange(it)
                            viewModel.sendMessage()
                        }
                    )
                }

                Divider()

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = input,
                        onValueChange = { viewModel.onInputChange(it) },
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp),
                        placeholder = { Text("Tulis pesan...", color = Color.Gray) },
                        shape = RoundedCornerShape(28.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent,
                            focusedContainerColor = Color(0xFFF0F0F0),
                            unfocusedContainerColor = Color(0xFFF0F0F0),
                            cursorColor = MaterialTheme.colorScheme.primary
                        ),
                        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Send),
                        keyboardActions = KeyboardActions(onSend = { viewModel.sendMessage() })
                    )
                    IconButton(
                        onClick = { viewModel.sendMessage() },
                        modifier = Modifier.padding(start = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Send,
                            contentDescription = "Kirim",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    )
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatTopBar(
    onBackClick: () -> Unit,
    onMenuClick: () -> Unit
) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = "BebanBot",
                color = colorResource(R.color.smoothMainColor),
                style = MaterialTheme.typography.titleMedium
            )
        },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.Default.ArrowBackIosNew,
                    contentDescription = "Back",
                    tint = colorResource(R.color.smoothMainColor)
                )
            }
        },
        actions = {
            IconButton(onClick = onMenuClick) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "More Options",
                    tint = colorResource(R.color.smoothMainColor)
                )
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = colorResource(R.color.white)
        )
    )
}



@Composable
fun ChatBubble(
    isBot: Boolean,
    message: String,
    avatarUrl: String? = null,
    username: String
) {
    val bubbleColor = if (isBot) Color(0xFFFFFFFF) else MaterialTheme.colorScheme.primary
    val textColor = if (isBot) Color.Black else Color.White
    val alignment = if (isBot) Arrangement.Start else Arrangement.End

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        horizontalArrangement = alignment
    ) {
        if (isBot && avatarUrl != null) {
            Image(
                painter = rememberAsyncImagePainter(model = avatarUrl),
                contentDescription = "Bot Avatar",
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(6.dp))
        }

        Column(
            horizontalAlignment = if (isBot) Alignment.Start else Alignment.End
        ) {
            Text(
                text = username,
                style = MaterialTheme.typography.labelSmall,
                color = Color.Gray
            )
            Surface(
                color = bubbleColor,
                shape = RoundedCornerShape(16.dp),
                tonalElevation = 4.dp
            ) {
                Text(
                    text = message,
                    modifier = Modifier.padding(12.dp),
                    color = textColor
                )
            }
        }

        if (!isBot) {
            val userAvatarUrl = avatarUrl ?: generateAvatarUrlFromUsername(username)

            Spacer(modifier = Modifier.width(6.dp))
            Image(
                painter = rememberAsyncImagePainter(
                    ImageRequest.Builder(LocalContext.current)
                        .data(userAvatarUrl)
                        .crossfade(true)
                        .build()
                ),
                contentDescription = "User Avatar",
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(8.dp))
        }
    }
}

fun generateAvatarUrlFromUsername(username: String): String {
    val encodedName = username.trim().replace(" ", "+")
    return "https://ui-avatars.com/api/?name=$encodedName&background=random&color=fff"
}

@Composable
fun SuggestionMessageRow(
    suggestions: List<String>,
    onClick: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Text(
            text = "Coba pertanyaan ini:",
            style = MaterialTheme.typography.labelMedium,
            color = Color.Gray
        )
        Spacer(modifier = Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .background(
                    color = Color(0xFFF9F9F9),
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Row(
                modifier = Modifier
                    .horizontalScroll(rememberScrollState())
            ) {
                suggestions.forEach { suggestion ->
                    AssistChip(
                        onClick = { onClick(suggestion) },
                        label = {
                            Text(
                                text = suggestion,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        },
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .height(36.dp),
                        border = BorderStroke(1.dp, Color.LightGray)
                    )
                }
            }
        }
    }
}


@Composable
fun EmptyChatState() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_chat_empty),
                contentDescription = "No Chat Icon",
                modifier = Modifier.size(120.dp),
                contentScale = ContentScale.Fit
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Belum ada percakapan",
                style = MaterialTheme.typography.titleMedium,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Silakan mulai bertanya sekarang!",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
        }
    }
}
