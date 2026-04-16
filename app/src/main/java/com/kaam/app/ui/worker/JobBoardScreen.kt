package com.kaam.app.ui.worker

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kaam.app.data.model.ServiceType
import com.kaam.app.ui.components.EmptyState
import com.kaam.app.ui.components.KaamCard
import com.kaam.app.ui.components.KaamCtaButton
import com.kaam.app.ui.components.KaamPrimaryButton
import com.kaam.app.ui.components.LoadingScreen

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun JobBoardScreen(
    viewModel: JobBoardViewModel,
) {
    val state by viewModel.uiState.collectAsState()

    if (state.isLoading) {
        LoadingScreen()
        return
    }

    // Apply dialog
    if (state.applyingToJobId != null) {
        AlertDialog(
            onDismissRequest = { viewModel.cancelApply() },
            title = { Text("Apply to Job") },
            text = {
                Column {
                    OutlinedTextField(
                        value = state.proposedRate,
                        onValueChange = { viewModel.setProposedRate(it) },
                        label = { Text("Your rate (Rs/hr)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = state.applyMessage,
                        onValueChange = { viewModel.setApplyMessage(it) },
                        label = { Text("Message (optional)") },
                        maxLines = 3,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    if (state.error != null) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = state.error!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { viewModel.submitApplication() }) {
                    Text("Submit")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.cancelApply() }) {
                    Text("Cancel")
                }
            },
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
    ) {
        item {
            Text(text = "Job Board", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(12.dp))

            // Filter chips
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = state.filterService == null,
                    onClick = { viewModel.setFilterService(null) },
                    label = { Text("All") },
                )
                ServiceType.entries.forEach { service ->
                    FilterChip(
                        selected = state.filterService == service,
                        onClick = { viewModel.setFilterService(service) },
                        label = { Text(service.name.lowercase().replaceFirstChar { it.uppercase() }) },
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        if (state.jobs.isEmpty()) {
            item { EmptyState("No open jobs in your area") }
        } else {
            items(state.jobs, key = { it.id!! }) { job ->
                KaamCard(modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row {
                            Text(
                                text = job.service.name.lowercase().replaceFirstChar { it.uppercase() },
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.primary,
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = job.date,
                                style = MaterialTheme.typography.bodySmall,
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = job.description, style = MaterialTheme.typography.bodyMedium)
                        if (job.budgetMax != null) {
                            Text(
                                text = "Budget: up to Rs ${job.budgetMax}",
                                style = MaterialTheme.typography.labelMedium,
                            )
                        }
                        Text(
                            text = "${job.city}${job.area?.let { ", $it" } ?: ""}",
                            style = MaterialTheme.typography.bodySmall,
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        KaamPrimaryButton(
                            text = "Apply",
                            onClick = { viewModel.startApply(job.id!!) },
                        )
                    }
                }
            }
        }
    }
}
