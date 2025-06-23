package com.example.gym_app.component

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp

@Composable
fun ParsedText(text: String, color: Color = Color.White.copy(alpha = 0.9f)) {
    val withNewlines = text.replace("\\", "\n")

    val cleanedText = withNewlines.replace(Regex("\\[.*?\\]"), "")

    val lines = cleanedText.split("\n")

    Column {
        for (line in lines) {
            val trimmedLine = line.trim()
            when {
                trimmedLine.matches(Regex("^\\d+\\.\\s.*")) -> {
                    Text(
                        text = trimmedLine,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = color,
                            lineHeight = 24.sp
                        )
                    )
                }

                trimmedLine.contains("**") -> {
                    val parts = trimmedLine.split("**")
                    val annotatedText = buildAnnotatedString {
                        for (i in parts.indices) {
                            if (i % 2 == 0) {
                                append(parts[i])
                            } else {
                                withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                                    append(parts[i])
                                }
                            }
                        }
                    }
                    Text(
                        text = annotatedText,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = color,
                            lineHeight = 24.sp
                        )
                    )
                }

                else -> {
                    if (trimmedLine.isNotBlank()) {
                        Text(
                            text = trimmedLine,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                color = color,
                                lineHeight = 24.sp
                            )
                        )
                    }
                }
            }
        }
    }
}
