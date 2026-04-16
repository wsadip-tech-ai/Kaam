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
import com.kaam.app.data.model.JobApplicationStatus
import com.kaam.app.ui.components.EmptyState
import com.kaam.app.ui.components.KaamCard
import com.kaam.app.ui.components.KaamCtaButton
import com.kaam.app.ui.components.KaamOutlinedButton
import com.kaam.app.ui.components.LoadingScreen

@Composable
fun JobApplicantsScreen(
    viewModel: JobApplicantsViewModel,
) {
    val state by viewModel.uiState.collectAsState()

    if (state.isLoading) {
        LoadingScreen()
        return
    }

    if (state.applicants.isEmpty()) {
        EmptyState("No applicants yet")
        return
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
    ) {
        item {
            Text(text = "Applicants", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(16.dp))
        }
        items(state.applicants, key = { it.application.id!! }) { applicant ->
            KaamCard(modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        AsyncImage(
                            model = applicant.profile.avatarUrl,
                            contentDescription = "${applicant.profile.fullName}'s photo",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.size(48.dp).clip(CircleShape),
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = applicant.profile.fullName,
                                style = MaterialTheme.typography.titleMedium,
                            )
                            Text(
                                text = "Rs ${applicant.application.proposedRate}/hr",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.primary,
                            )
                        }
                    }

                    if (!applicant.application.message.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = applicant.application.message!!,
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }

                    if (applicant.application.status == JobApplicationStatus.PENDING) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Row {
                            KaamCtaButton(
                                text = "Accept",
                                onClick = { viewModel.acceptApplication(applicant.application.id!!) },
                                modifier = Modifier.weight(1f),
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            KaamOutlinedButton(
                                text = "Decline",
                                onClick = { viewModel.rejectApplication(applicant.application.id!!) },
                                modifier = Modifier.weight(1f),
                            )
                        }
                    } else {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = applicant.application.status.name.lowercase().replaceFirstChar { it.uppercase() },
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
        }
    }
}
