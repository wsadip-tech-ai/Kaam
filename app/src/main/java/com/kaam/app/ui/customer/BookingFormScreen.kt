package com.kaam.app.ui.customer

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kaam.app.ui.components.KaamCtaButton

private val TIME_SLOTS = listOf(
    "7:00 AM - 9:00 AM",
    "9:00 AM - 11:00 AM",
    "11:00 AM - 1:00 PM",
    "1:00 PM - 3:00 PM",
    "3:00 PM - 5:00 PM",
    "5:00 PM - 7:00 PM",
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingFormScreen(
    viewModel: BookingFormViewModel,
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
        Text(text = "Book a Service", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = state.service.name.lowercase().replaceFirstChar { it.uppercase() },
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
        )

        // Date
        Spacer(modifier = Modifier.height(24.dp))
        OutlinedTextField(
            value = state.date,
            onValueChange = { viewModel.setDate(it) },
            label = { Text("Date (YYYY-MM-DD)") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )

        // Time slot
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "Time Slot", style = MaterialTheme.typography.titleSmall)
        Spacer(modifier = Modifier.height(8.dp))
        TIME_SLOTS.forEach { slot ->
            androidx.compose.material3.FilterChip(
                selected = state.timeSlot == slot,
                onClick = { viewModel.setTimeSlot(slot) },
                label = { Text(slot) },
                modifier = Modifier.padding(bottom = 4.dp),
            )
        }

        // Address
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = state.address,
            onValueChange = { viewModel.setAddress(it) },
            label = { Text("Address") },
            maxLines = 2,
            modifier = Modifier.fillMaxWidth(),
        )

        // Notes
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = state.notes,
            onValueChange = { viewModel.setNotes(it) },
            label = { Text("Notes (optional)") },
            maxLines = 3,
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
            text = if (state.isLoading) "Booking..." else "Confirm Booking",
            onClick = { viewModel.submit() },
            enabled = state.date.isNotBlank() && state.timeSlot.isNotBlank() && state.address.isNotBlank() && !state.isLoading,
        )
        Spacer(modifier = Modifier.height(24.dp))
    }
}
