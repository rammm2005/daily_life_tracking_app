package com.example.gym_app.activity.chatbot

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.gym_app.R
import com.example.gym_app.model.Message
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatBotScreen(
) {
    val context = LocalContext.current
    val viewModel = remember { ChatViewModel(context) }
    val messages by viewModel.messages.collectAsState()
    val input by viewModel.input.collectAsState()
    val isThinking by viewModel.isThinking.collectAsState()
    val username by viewModel.username.collectAsState()

    Column(modifier = Modifier.fillMaxSize().background(Color(0xFFF5F5F5))) {
        TopAppBar(title = { Text("Chat with Assistant") })

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(8.dp),
            reverseLayout = true
        ) {
            if (messages.isEmpty() && !isThinking) {
                item {
                    EmptyChatState()
                }
            } else {
                items(messages.reversed()) { msg: Message ->
                    ChatBubble(
                        isBot = msg.role == "bot",
                        message = msg.content,
                        avatarUrl = if (msg.role == "bot") "https://cdn-icons-png.flaticon.com/512/4712/4712109.png" else null,
                        username = if (msg.role == "bot") "Chatify" else username
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            if (isThinking) {
                item {
                    ChatBubble(
                        isBot = true,
                        message = "Sedang memproses...",
                        avatarUrl = "https://cdn-icons-png.flaticon.com/512/4712/4712109.png",
                        username = "Chatify"
                    )
                }
            }
        }

        val suggestions by viewModel.suggestions.collectAsState()

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
                .padding(8.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = input,
                onValueChange = { viewModel.onInputChange(it) },
                modifier = Modifier.weight(1f),
                placeholder = { Text("Tulis sesuatu...") },
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Send),
                keyboardActions = KeyboardActions(onSend = { viewModel.sendMessage() })
            )
            IconButton(onClick = { viewModel.sendMessage() }) {
                Icon(Icons.Default.Send, contentDescription = "Kirim")
            }
        }
    }
}

@Composable
fun ChatBubble(
    isBot: Boolean,
    message: String,
    avatarUrl: String? = null,
    username: String
) {
    val alignment = if (isBot) Alignment.Start else Alignment.End
    val backgroundColor = if (isBot) Color.White else MaterialTheme.colorScheme.primary
    val contentColor = if (isBot) Color.Black else Color.White

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isBot) Arrangement.Start else Arrangement.End
    ) {
        if (isBot && avatarUrl != null) {
            Image(
                painter = rememberAsyncImagePainter(ImageRequest.Builder(LocalContext.current).data(avatarUrl).build()),
                contentDescription = "Bot Avatar",
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(8.dp))
        }

        Column(horizontalAlignment = alignment) {
            Text(text = username, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
            Surface(
                shape = MaterialTheme.shapes.medium,
                color = backgroundColor,
                tonalElevation = 1.dp
            ) {
                Text(
                    text = message,
                    modifier = Modifier.padding(12.dp),
                    color = contentColor
                )
            }
        }
    }
}


@Composable
fun SuggestionMessageRow(
    suggestions: List<String>,
    onClick: (String) -> Unit
) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Text(
            text = "Coba pertanyaan ini:",
            style = MaterialTheme.typography.labelMedium,
            color = Color.Gray
        )
        Spacer(modifier = Modifier.height(4.dp))
        Row(
            modifier = Modifier
                .horizontalScroll(rememberScrollState())
                .padding(vertical = 4.dp)
        ) {
            suggestions.forEach { suggestion ->
                AssistChip(
                    onClick = { onClick(suggestion) },
                    label = { Text(suggestion) },
                    modifier = Modifier.padding(end = 8.dp)
                )
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


