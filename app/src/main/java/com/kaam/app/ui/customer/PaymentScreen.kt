package com.kaam.app.ui.customer

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.kaam.app.ui.components.KaamCard
import com.kaam.app.ui.components.KaamCtaButton
import com.kaam.app.ui.components.LoadingScreen

@Composable
fun PaymentScreen(
    viewModel: PaymentViewModel,
    onPaid: () -> Unit,
) {
    val state by viewModel.uiState.collectAsState()

    if (state.isLoading) {
        LoadingScreen()
        return
    }

    if (state.isPaid) {
        onPaid()
        return
    }

    val wp = state.workerProfile
    val booking = state.booking

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(text = "Payment", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Pay the worker directly using the details below",
            style = MaterialTheme.typography.bodyMedium,
        )

        if (booking != null) {
            Spacer(modifier = Modifier.height(16.dp))
            KaamCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "Booking Details", style = MaterialTheme.typography.titleSmall)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${booking.service.name.lowercase().replaceFirstChar { it.uppercase() }} • ${booking.date}",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    Text(text = booking.timeSlot, style = MaterialTheme.typography.bodySmall)
                }
            }
        }

        if (wp != null) {
            // Payment QR
            if (!wp.paymentQrUrl.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(24.dp))
                Text(text = "Scan QR to Pay", style = MaterialTheme.typography.titleSmall)
                Spacer(modifier = Modifier.height(8.dp))
                AsyncImage(
                    model = wp.paymentQrUrl,
                    contentDescription = "Payment QR code",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .size(200.dp)
                        .clip(RoundedCornerShape(12.dp)),
                )
            }

            // Payment phone
            if (!wp.paymentPhone.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = "Or send to mobile:", style = MaterialTheme.typography.titleSmall)
                Text(
                    text = wp.paymentPhone!!,
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
        }

        // Error
        if (state.error != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = state.error!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
        }

        Spacer(modifier = Modifier.height(32.dp))
        KaamCtaButton(
            text = "I've Paid",
            onClick = { viewModel.markAsPaid() },
        )
        Spacer(modifier = Modifier.height(24.dp))
    }
}
