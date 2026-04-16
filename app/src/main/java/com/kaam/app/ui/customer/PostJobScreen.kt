package com.kaam.app.ui.customer

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kaam.app.data.model.ServiceType
import com.kaam.app.ui.components.KaamCtaButton

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PostJobScreen(
    viewModel: PostJobViewModel,
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
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
    ) {
        Text(text = "Post a Job Request", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Describe what you need, workers will apply", style = MaterialTheme.typography.bodyMedium)

        // Service type
        Spacer(modifier = Modifier.height(24.dp))
        Text(text = "Service type", style = MaterialTheme.typography.titleSmall)
        Spacer(modifier = Modifier.height(8.dp))
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            ServiceType.entries.forEach { service ->
                FilterChip(
                    selected = state.service == service,
                    onClick = { viewModel.setService(service) },
                    label = { Text(service.name.lowercase().replaceFirstChar { it.uppercase() }) },
                )
            }
        }

        // Description
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = state.description,
            onValueChange = { viewModel.setDescription(it) },
            label = { Text("What do you need done?") },
            maxLines = 4,
            modifier = Modifier.fillMaxWidth(),
        )

        // Date
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = state.date,
            onValueChange = { viewModel.setDate(it) },
            label = { Text("Preferred date (YYYY-MM-DD)") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )

        // Budget
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = state.budgetMax,
            onValueChange = { viewModel.setBudgetMax(it) },
            label = { Text("Max budget in Rs (optional)") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )

        // Error
        if (state.error != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = state.error!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
        }

        // Submit
        Spacer(modifier = Modifier.height(32.dp))
        KaamCtaButton(
            text = if (state.isLoading) "Posting..." else "Post Job Request",
            onClick = { viewModel.submit() },
            enabled = state.description.isNotBlank() && state.date.isNotBlank() && !state.isLoading,
        )
        Spacer(modifier = Modifier.height(24.dp))
    }
}
