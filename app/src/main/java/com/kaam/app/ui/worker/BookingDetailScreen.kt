package com.kaam.app.ui.worker

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
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
import com.kaam.app.data.model.BookingStatus
import com.kaam.app.ui.components.BookingStatusChip
import com.kaam.app.ui.components.KaamCard
import com.kaam.app.ui.components.KaamCtaButton
import com.kaam.app.ui.components.KaamOutlinedButton
import com.kaam.app.ui.components.KaamPrimaryButton
import com.kaam.app.ui.components.LoadingScreen

@Composable
fun BookingDetailScreen(
    viewModel: BookingDetailViewModel,
    onBack: () -> Unit,
) {
    val state by viewModel.uiState.collectAsState()

    if (state.isLoading) {
        LoadingScreen()
        return
    }

    val booking = state.booking
    if (booking == null) {
        Text("Booking not found", modifier = Modifier.padding(24.dp))
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
    ) {
        // Status header
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = "Booking Details", style = MaterialTheme.typography.headlineMedium, modifier = Modifier.weight(1f))
            BookingStatusChip(booking.status)
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Customer info
        if (state.customerProfile != null) {
            val customer = state.customerProfile!!
            KaamCard(modifier = Modifier.fillMaxWidth()) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    AsyncImage(
                        model = customer.avatarUrl,
                        contentDescription = "${customer.fullName}'s photo",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.size(48.dp).clip(CircleShape),
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(text = customer.fullName, style = MaterialTheme.typography.titleMedium)
                        Text(
                            text = "${customer.city}${customer.area?.let { ", $it" } ?: ""}",
                            style = MaterialTheme.typography.bodySmall,
                        )
                    }
                }
            }
        }

        // Booking details
        Spacer(modifier = Modifier.height(16.dp))
        KaamCard(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                DetailRow("Service", booking.service.name.lowercase().replaceFirstChar { it.uppercase() })
                DetailRow("Date", booking.date)
                DetailRow("Time", booking.timeSlot)
                DetailRow("Address", booking.address)
                if (!booking.notes.isNullOrBlank()) {
                    DetailRow("Notes", booking.notes!!)
                }
            }
        }

        // Error
        if (state.error != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = state.error!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
        }

        // Action buttons based on status
        Spacer(modifier = Modifier.height(24.dp))
        when (booking.status) {
            BookingStatus.PENDING -> {
                KaamCtaButton(
                    text = "Accept",
                    onClick = { viewModel.acceptBooking() },
                )
                Spacer(modifier = Modifier.height(8.dp))
                KaamOutlinedButton(
                    text = "Decline",
                    onClick = { viewModel.declineBooking() },
                )
            }
            BookingStatus.ACCEPTED -> {
                KaamPrimaryButton(
                    text = "Mark as Complete",
                    onClick = { viewModel.markComplete() },
                )
            }
            else -> {}
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(
            text = "$label: ",
            style = MaterialTheme.typography.titleSmall,
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}
