package com.kaam.app.ui.worker

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kaam.app.ui.components.BookingStatusChip
import com.kaam.app.ui.components.KaamCard
import com.kaam.app.ui.components.LoadingScreen

@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel,
    onBookingClick: (String) -> Unit,
) {
    val state by viewModel.uiState.collectAsState()

    if (state.isLoading) {
        LoadingScreen()
        return
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
    ) {
        // Availability toggle
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "Dashboard", style = MaterialTheme.typography.headlineMedium)
                    Text(
                        text = if (state.workerProfile?.isAvailable == true) "Available for work" else "Not available",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
                Switch(
                    checked = state.workerProfile?.isAvailable == true,
                    onCheckedChange = { viewModel.toggleAvailability() },
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
        }

        // Pending bookings
        if (state.pendingBookings.isNotEmpty()) {
            item {
                Text(text = "Pending Requests", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
            }
            items(state.pendingBookings, key = { it.id!! }) { booking ->
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
                        Text(text = booking.address, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }

        // Active bookings
        if (state.activeBookings.isNotEmpty()) {
            item {
                Spacer(modifier = Modifier.height(16.dp))
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
                        Text(text = booking.address, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
}
