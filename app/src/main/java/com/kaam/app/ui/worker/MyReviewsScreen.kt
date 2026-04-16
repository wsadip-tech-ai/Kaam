package com.kaam.app.ui.worker

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kaam.app.ui.components.EmptyState
import com.kaam.app.ui.components.KaamCard
import com.kaam.app.ui.components.LoadingScreen
import com.kaam.app.ui.components.RatingBar

@Composable
fun MyReviewsScreen(
    viewModel: MyReviewsViewModel,
) {
    val state by viewModel.uiState.collectAsState()

    if (state.isLoading) {
        LoadingScreen()
        return
    }

    if (state.reviews.isEmpty()) {
        EmptyState("No reviews yet")
        return
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
    ) {
        // Average rating header
        item {
            Text(text = "My Reviews", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(16.dp))
            if (state.averageRating != null) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "%.1f".format(state.averageRating),
                        style = MaterialTheme.typography.displayLarge,
                        color = MaterialTheme.colorScheme.primary,
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        RatingBar(rating = state.averageRating!!.toInt())
                        Text(
                            text = "${state.reviews.size} reviews",
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }

        items(state.reviews, key = { it.id!! }) { review ->
            KaamCard(modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    RatingBar(rating = review.rating)
                    if (!review.comment.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = review.comment!!, style = MaterialTheme.typography.bodyMedium)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = review.createdAt?.take(10) ?: "",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}
