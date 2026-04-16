package com.kaam.app.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.kaam.app.data.model.VerificationStatus
import com.kaam.app.data.model.WorkerWithProfile

@Composable
fun WorkerCard(
    worker: WorkerWithProfile,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    KaamCard(onClick = onClick, modifier = modifier.fillMaxWidth()) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = worker.profile.avatarUrl,
                contentDescription = "${worker.profile.fullName}'s photo",
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(56.dp).clip(CircleShape),
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = worker.profile.fullName,
                        style = MaterialTheme.typography.titleMedium,
                    )
                    if (worker.workerProfile.verificationStatus == VerificationStatus.VERIFIED) {
                        Spacer(modifier = Modifier.width(6.dp))
                        VerificationChip(VerificationStatus.VERIFIED)
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = worker.workerProfile.services.joinToString(", ") { it.name.lowercase().replaceFirstChar { c -> c.uppercase() } },
                    style = MaterialTheme.typography.bodyMedium,
                )
                Spacer(modifier = Modifier.height(2.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (worker.averageRating != null) {
                        Text(
                            text = "%.1f".format(worker.averageRating),
                            style = MaterialTheme.typography.bodySmall,
                        )
                        Text(
                            text = " (${worker.reviewCount})",
                            style = MaterialTheme.typography.bodySmall,
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                    }
                    Text(
                        text = "Rs ${worker.workerProfile.hourlyRate}/hr",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
            }
        }
    }
}
