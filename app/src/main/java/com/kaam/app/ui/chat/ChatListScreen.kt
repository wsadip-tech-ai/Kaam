package com.kaam.app.ui.chat

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Badge
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.kaam.app.ui.components.EmptyState
import com.kaam.app.ui.components.LoadingScreen

@Composable
fun ChatListScreen(
    viewModel: ChatListViewModel,
    onConversationClick: (String) -> Unit,
) {
    val state by viewModel.uiState.collectAsState()

    if (state.isLoading) {
        LoadingScreen()
        return
    }

    if (state.conversations.isEmpty()) {
        EmptyState("No conversations yet")
        return
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
    ) {
        item {
            Text(text = "Messages", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(16.dp))
        }
        items(state.conversations, key = { it.conversation.id!! }) { preview ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onConversationClick(preview.conversation.id!!) }
                    .padding(vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                AsyncImage(
                    model = preview.otherParty.avatarUrl,
                    contentDescription = "${preview.otherParty.fullName}'s photo",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.size(48.dp).clip(CircleShape),
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = preview.otherParty.fullName,
                        style = MaterialTheme.typography.titleMedium,
                    )
                    if (preview.lastMessage != null) {
                        Text(
                            text = preview.lastMessage.content,
                            style = MaterialTheme.typography.bodySmall,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }
                if (preview.unreadCount > 0) {
                    Badge { Text(preview.unreadCount.toString()) }
                }
            }
        }
    }
}
