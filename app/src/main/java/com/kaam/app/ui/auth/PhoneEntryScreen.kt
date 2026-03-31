package com.kaam.app.ui.auth

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.kaam.app.ui.components.KaamPrimaryButton

@Composable
fun PhoneEntryScreen(
    isLoading: Boolean,
    error: String?,
    onSendOtp: (phone: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var phone by rememberSaveable { mutableStateOf("") }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 48.dp),
        ) {
            Text(
                text = "Enter your number",
                style = MaterialTheme.typography.headlineMedium,
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "We'll send a one-time code to verify your identity.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Spacer(modifier = Modifier.height(32.dp))

            OutlinedTextField(
                value = phone,
                onValueChange = { input ->
                    // Strip non-digit characters and enforce 10-digit max
                    val digits = input.filter { it.isDigit() }
                    if (digits.length <= 10) phone = digits
                },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Phone number") },
                prefix = { Text("+977 ") },
                placeholder = { Text("98XXXXXXXX") },
                singleLine = true,
                isError = error != null,
                supportingText = if (error != null) {
                    { Text(text = error, color = MaterialTheme.colorScheme.error) }
                } else null,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Phone,
                    imeAction = ImeAction.Done,
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        if (phone.length == 10 && !isLoading) onSendOtp(phone)
                    },
                ),
            )

            Spacer(modifier = Modifier.height(24.dp))

            KaamPrimaryButton(
                text = if (isLoading) "Sending…" else "Send OTP",
                onClick = { onSendOtp(phone) },
                enabled = phone.length == 10 && !isLoading,
            )
        }
    }
}
