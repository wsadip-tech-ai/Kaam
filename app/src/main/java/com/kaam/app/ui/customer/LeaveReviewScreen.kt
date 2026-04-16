package com.kaam.app.ui.customer

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kaam.app.ui.components.KaamCtaButton
import com.kaam.app.ui.components.RatingBar

@Composable
fun LeaveReviewScreen(
    viewModel: LeaveReviewViewModel,
    onComplete: () -> Unit,
) {
    val state by viewModel.uiState.collectAsState()

    if (state.isComplete) {
        onComplete()
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(48.dp))
        Text(text = "How was the service?", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Your review helps other customers", style = MaterialTheme.typography.bodyMedium)

        Spacer(modifier = Modifier.height(32.dp))
        RatingBar(
            rating = state.rating,
            onRatingChanged = { viewModel.setRating(it) },
        )

        Spacer(modifier = Modifier.height(24.dp))
        OutlinedTextField(
            value = state.comment,
            onValueChange = { viewModel.setComment(it) },
            label = { Text("Comment (optional)") },
            maxLines = 4,
            modifier = Modifier.fillMaxWidth(),
        )

        if (state.error != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = state.error!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
        }

        Spacer(modifier = Modifier.height(32.dp))
        KaamCtaButton(
            text = if (state.isLoading) "Submitting..." else "Submit Review",
            onClick = { viewModel.submit() },
            enabled = state.rating > 0 && !state.isLoading,
        )
    }
}
