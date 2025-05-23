package com.example.chainsproject.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import org.json.JSONArray
import org.json.JSONObject

@Composable
fun RichTextContent(
    content: String,
    modifier: Modifier = Modifier
) {
    val richText = remember(content) {
        try {
            JSONArray(content)
        } catch (e: Exception) {
            JSONArray()
        }
    }

    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        for (i in 0 until richText.length()) {
            val item = richText.getJSONObject(i)
            when (item.getString("type")) {
                "text" -> {
                    Text(
                        text = item.getString("content"),
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }
                "image" -> {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(item.getString("url"))
                            .crossfade(true)
                            .build(),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    )
                }
                "heading" -> {
                    Text(
                        text = item.getString("content"),
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
                "list" -> {
                    val items = item.getJSONArray("items")
                    for (j in 0 until items.length()) {
                        Row(
                            modifier = Modifier.padding(vertical = 4.dp)
                        ) {
                            Text(
                                text = "• ",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = items.getString(j),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
        }
    }
} 