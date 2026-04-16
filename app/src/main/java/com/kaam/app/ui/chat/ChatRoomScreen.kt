package com.kaam.app.ui.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.kaam.app.ui.components.LoadingScreen

@Composable
fun ChatRoomScreen(
    viewModel: ChatRoomViewModel,
) {
    val state by viewModel.uiState.collectAsState()
    val listState = rememberLazyListState()

    if (state.isLoading) {
        LoadingScreen()
        return
    }

    // Auto-scroll to latest message
    LaunchedEffect(state.messages.size) {
        if (state.messages.isNotEmpty()) {
            listState.animateScrollToItem(state.messages.size - 1)
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Header
        if (state.otherParty != null) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                AsyncImage(
                    model = state.otherParty!!.avatarUrl,
                    contentDescription = "${state.otherParty!!.fullName}'s photo",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.size(40.dp).clip(CircleShape),
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = state.otherParty!!.fullName,
                    style = MaterialTheme.typography.titleMedium,
                )
            }
        }

        // Messages
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(state.messages, key = { it.id ?: it.sentAt ?: it.content }) { message ->
                val isMe = message.senderId == state.currentUserId
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = if (isMe) Alignment.CenterEnd else Alignment.CenterStart,
                ) {
                    Text(
                        text = message.content,
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (isMe) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier
                            .widthIn(max = 280.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                if (isMe) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.surfaceVariant
                            )
                            .padding(12.dp),
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Input
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            OutlinedTextField(
                value = state.messageText,
                onValueChange = { viewModel.setMessageText(it) },
                placeholder = { Text("Type a message...") },
                modifier = Modifier.weight(1f),
                maxLines = 3,
                shape = RoundedCornerShape(24.dp),
            )
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(
                onClick = { viewModel.sendMessage() },
                enabled = state.messageText.isNotBlank(),
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Send",
                    tint = MaterialTheme.colorScheme.primary,
                )
            }
        }
    }
}
