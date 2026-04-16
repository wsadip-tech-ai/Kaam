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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.kaam.app.data.model.ServiceType
import com.kaam.app.ui.components.ImagePicker
import com.kaam.app.ui.components.KaamCtaButton

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ProfileSetupScreen(
    viewModel: ProfileSetupViewModel,
    onComplete: () -> Unit,
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current

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
        Text(text = "Set up your work profile", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Tell customers what you offer", style = MaterialTheme.typography.bodyMedium)

        // Services
        Spacer(modifier = Modifier.height(24.dp))
        Text(text = "Services you offer", style = MaterialTheme.typography.titleSmall)
        Spacer(modifier = Modifier.height(8.dp))
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            ServiceType.entries.forEach { service ->
                FilterChip(
                    selected = service in state.services,
                    onClick = { viewModel.toggleService(service) },
                    label = { Text(service.name.lowercase().replaceFirstChar { it.uppercase() }) },
                )
            }
        }

        // Hourly rate
        Spacer(modifier = Modifier.height(24.dp))
        Text(text = "Hourly rate: Rs ${state.hourlyRate}", style = MaterialTheme.typography.titleSmall)
        Slider(
            value = state.hourlyRate.toFloat(),
            onValueChange = { viewModel.setHourlyRate(it.toInt()) },
            valueRange = 200f..2000f,
            steps = 17,
        )

        // Bio
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = state.bio,
            onValueChange = { viewModel.setBio(it) },
            label = { Text("Short bio (optional)") },
            maxLines = 3,
            modifier = Modifier.fillMaxWidth(),
        )

        // Citizenship doc
        Spacer(modifier = Modifier.height(24.dp))
        Text(text = "Citizenship document", style = MaterialTheme.typography.titleSmall)
        Spacer(modifier = Modifier.height(8.dp))
        ImagePicker(
            currentImageUri = state.citizenshipDocUri,
            onImageSelected = { viewModel.setCitizenshipDoc(it) },
            size = 120.dp,
        )

        // Payment QR
        Spacer(modifier = Modifier.height(24.dp))
        Text(text = "Payment QR code (optional)", style = MaterialTheme.typography.titleSmall)
        Spacer(modifier = Modifier.height(8.dp))
        Row {
            ImagePicker(
                currentImageUri = state.paymentQrUri,
                onImageSelected = { viewModel.setPaymentQr(it) },
            )
        }

        // Payment phone
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = state.paymentPhone,
            onValueChange = { viewModel.setPaymentPhone(it) },
            label = { Text("Payment mobile number (optional)") },
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
            text = if (state.isLoading) "Submitting..." else "Submit for Verification",
            onClick = { viewModel.submit(context) },
            enabled = state.services.isNotEmpty() && state.citizenshipDocUri != null && !state.isLoading,
        )
        Spacer(modifier = Modifier.height(24.dp))
    }
}
