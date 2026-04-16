package com.kaam.app.ui.profile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.kaam.app.ui.components.ImagePicker
import com.kaam.app.ui.components.KaamCtaButton
import com.kaam.app.ui.components.KaamOutlinedButton
import com.kaam.app.ui.components.LoadingScreen

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onLoggedOut: () -> Unit,
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    if (state.isLoggedOut) {
        onLoggedOut()
        return
    }

    if (state.isLoading) {
        LoadingScreen()
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
    ) {
        Text(text = "Settings", style = MaterialTheme.typography.headlineMedium)

        // Avatar
        Spacer(modifier = Modifier.height(24.dp))
        Text(text = "Profile Photo", style = MaterialTheme.typography.titleSmall)
        Spacer(modifier = Modifier.height(8.dp))
        ImagePicker(
            currentImageUri = state.avatarUri,
            onImageSelected = { viewModel.setAvatarUri(it) },
            size = 96.dp,
        )

        // Name
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = state.fullName,
            onValueChange = { viewModel.setFullName(it) },
            label = { Text("Full Name") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )

        // City
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = state.city,
            onValueChange = { viewModel.setCity(it) },
            label = { Text("City") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )

        // Area
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = state.area,
            onValueChange = { viewModel.setArea(it) },
            label = { Text("Area (optional)") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )

        // Worker-specific fields
        if (state.workerProfile != null) {
            Spacer(modifier = Modifier.height(24.dp))
            Text(text = "Worker Settings", style = MaterialTheme.typography.titleMedium)

            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = state.bio,
                onValueChange = { viewModel.setBio(it) },
                label = { Text("Bio") },
                maxLines = 3,
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "Hourly rate: Rs ${state.hourlyRate}", style = MaterialTheme.typography.titleSmall)
            Slider(
                value = state.hourlyRate.toFloat(),
                onValueChange = { viewModel.setHourlyRate(it.toInt()) },
                valueRange = 200f..2000f,
                steps = 17,
            )

            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Available for work",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.weight(1f),
                )
                Switch(
                    checked = state.isAvailable,
                    onCheckedChange = { viewModel.toggleAvailability() },
                )
            }
        }

        // Error
        if (state.error != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = state.error!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
        }

        // Save
        Spacer(modifier = Modifier.height(32.dp))
        KaamCtaButton(
            text = if (state.isSaving) "Saving..." else "Save Changes",
            onClick = { viewModel.save(context) },
            enabled = state.fullName.isNotBlank() && state.city.isNotBlank() && !state.isSaving,
        )

        // Logout
        Spacer(modifier = Modifier.height(16.dp))
        KaamOutlinedButton(
            text = "Logout",
            onClick = { viewModel.logout() },
        )
        Spacer(modifier = Modifier.height(24.dp))
    }
}
