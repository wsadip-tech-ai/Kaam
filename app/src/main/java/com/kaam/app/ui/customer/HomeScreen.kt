package com.kaam.app.ui.customer

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.ChildCare
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kaam.app.ui.components.BookingStatusChip
import com.kaam.app.ui.components.KaamCard

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onServiceClick: (String) -> Unit,
    onPostJob: () -> Unit,
    onBookingClick: (String) -> Unit,
) {
    val state by viewModel.uiState.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
    ) {
        item {
            Text(text = "What do you need?", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                ServiceCard(
                    icon = { Icon(Icons.Default.CleaningServices, contentDescription = null, modifier = Modifier.size(32.dp)) },
                    label = "Cleaning",
                    onClick = { onServiceClick("cleaning") },
                    modifier = Modifier.weight(1f),
                )
                ServiceCard(
                    icon = { Icon(Icons.Default.ChildCare, contentDescription = null, modifier = Modifier.size(32.dp)) },
                    label = "Nanny",
                    onClick = { onServiceClick("nanny") },
                    modifier = Modifier.weight(1f),
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
            KaamCard(onClick = onPostJob, modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp).fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(text = "Post a Job Request", style = MaterialTheme.typography.titleSmall)
                    Text(text = "Describe what you need, workers will apply", style = MaterialTheme.typography.bodySmall)
                }
            }
        }

        // Active bookings
        if (state.activeBookings.isNotEmpty()) {
            item {
                Spacer(modifier = Modifier.height(24.dp))
                Text(text = "Active Bookings", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
            }
            items(state.activeBookings, key = { it.id!! }) { booking ->
                KaamCard(
                    onClick = { onBookingClick(booking.id!!) },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = booking.service.name.lowercase().replaceFirstChar { it.uppercase() },
                                style = MaterialTheme.typography.titleSmall,
                                modifier = Modifier.weight(1f),
                            )
                            BookingStatusChip(booking.status)
                        }
                        Text(text = "${booking.date} • ${booking.timeSlot}", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    }
}

@Composable
private fun ServiceCard(
    icon: @Composable () -> Unit,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        tonalElevation = 2.dp,
        modifier = modifier.clickable(onClick = onClick),
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            icon()
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = label, style = MaterialTheme.typography.titleSmall)
        }
    }
}
