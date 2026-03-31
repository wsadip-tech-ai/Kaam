package com.kaam.app.ui.auth

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kaam.app.ui.components.KaamPrimaryButton

@Composable
fun BasicInfoScreen(
    isLoading: Boolean,
    error: String?,
    onSubmit: (name: String, city: String, area: String?) -> Unit,
    modifier: Modifier = Modifier,
) {
    var fullName by rememberSaveable { mutableStateOf("") }
    var city by rememberSaveable { mutableStateOf("Kathmandu") }
    var area by rememberSaveable { mutableStateOf("") }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 48.dp),
        ) {
            Text(
                text = "Tell us about yourself",
                style = MaterialTheme.typography.headlineMedium,
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "This helps workers and customers find you.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Spacer(modifier = Modifier.height(32.dp))

            OutlinedTextField(
                value = fullName,
                onValueChange = { fullName = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Full name") },
                placeholder = { Text("e.g. Sita Sharma") },
                singleLine = true,
                isError = error != null && fullName.isBlank(),
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = city,
                onValueChange = { city = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("City") },
                singleLine = true,
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = area,
                onValueChange = { area = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Area (optional)") },
                placeholder = { Text("e.g. Thamel") },
                singleLine = true,
            )

            if (error != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            KaamPrimaryButton(
                text = if (isLoading) "Saving…" else "Continue",
                onClick = {
                    onSubmit(
                        fullName.trim(),
                        city.trim().ifBlank { "Kathmandu" },
                        area.trim().ifBlank { null },
                    )
                },
                enabled = fullName.isNotBlank() && city.isNotBlank() && !isLoading,
            )
        }
    }
}
