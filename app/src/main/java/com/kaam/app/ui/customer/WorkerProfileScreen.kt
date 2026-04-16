package com.kaam.app.ui.customer

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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.kaam.app.data.model.VerificationStatus
import com.kaam.app.ui.components.KaamCard
import com.kaam.app.ui.components.KaamCtaButton
import com.kaam.app.ui.components.KaamOutlinedButton
import com.kaam.app.ui.components.LoadingScreen
import com.kaam.app.ui.components.RatingBar
import com.kaam.app.ui.components.VerificationChip

@Composable
fun WorkerProfileScreen(
    viewModel: WorkerProfileViewModel,
    onBookNow: (String) -> Unit,
    onSendInquiry: (String) -> Unit,
) {
    val state by viewModel.uiState.collectAsState()

    if (state.isLoading) {
        LoadingScreen()
        return
    }

    val profile = state.profile ?: return
    val wp = state.workerProfile ?: return

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
    ) {
        // Header: photo, name, badge, area
        item {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                AsyncImage(
                    model = profile.avatarUrl,
                    contentDescription = "${profile.fullName}'s photo",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.size(96.dp).clip(CircleShape),
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = profile.fullName, style = MaterialTheme.typography.headlineSmall)
                    if (wp.verificationStatus == VerificationStatus.VERIFIED) {
                        Spacer(modifier = Modifier.width(8.dp))
                        VerificationChip(VerificationStatus.VERIFIED)
                    }
                }
                Text(
                    text = "${profile.city}${profile.area?.let { ", $it" } ?: ""}",
                    style = MaterialTheme.typography.bodyMedium,
                )

                // Rating
                if (state.averageRating != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RatingBar(rating = state.averageRating!!.toInt())
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "%.1f (${state.reviews.size} reviews)".format(state.averageRating),
                            style = MaterialTheme.typography.bodySmall,
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }

        // Services & rate
        item {
            KaamCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "Services", style = MaterialTheme.typography.titleSmall)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = wp.services.joinToString(", ") { it.name.lowercase().replaceFirstChar { c -> c.uppercase() } },
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(text = "Rate", style = MaterialTheme.typography.titleSmall)
                    Text(
                        text = "Rs ${wp.hourlyRate}/hr",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Bio
        if (!wp.bio.isNullOrBlank()) {
            item {
                KaamCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = "About", style = MaterialTheme.typography.titleSmall)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = wp.bio!!, style = MaterialTheme.typography.bodyMedium)
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        // Action buttons
        item {
            KaamCtaButton(
                text = "Book Now",
                onClick = { onBookNow(profile.id) },
            )
            Spacer(modifier = Modifier.height(8.dp))
            KaamOutlinedButton(
                text = "Send Inquiry",
                onClick = { onSendInquiry(profile.id) },
            )
            Spacer(modifier = Modifier.height(24.dp))
        }

        // Reviews section
        if (state.reviews.isNotEmpty()) {
            item {
                Text(text = "Reviews", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
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
}
