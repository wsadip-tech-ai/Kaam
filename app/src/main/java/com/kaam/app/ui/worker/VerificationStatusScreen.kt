package com.kaam.app.ui.worker

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kaam.app.data.model.VerificationStatus
import com.kaam.app.ui.components.KaamPrimaryButton
import com.kaam.app.ui.components.LoadingScreen
import com.kaam.app.ui.components.VerificationChip

@Composable
fun VerificationStatusScreen(
    viewModel: VerificationStatusViewModel,
    onVerified: () -> Unit,
    onReUpload: () -> Unit,
) {
    val workerProfile by viewModel.workerProfile.collectAsState()

    if (workerProfile == null) {
        LoadingScreen()
        return
    }

    val wp = workerProfile!!

    if (wp.verificationStatus == VerificationStatus.VERIFIED) {
        onVerified()
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(80.dp))
        VerificationChip(wp.verificationStatus)
        Spacer(modifier = Modifier.height(24.dp))

        when (wp.verificationStatus) {
            VerificationStatus.PENDING -> {
                Text(
                    text = "Your profile is under review",
                    style = MaterialTheme.typography.headlineSmall,
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Our team will verify your documents. This usually takes 1-2 business days. You'll get a notification once verified.",
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
            VerificationStatus.REJECTED -> {
                Text(
                    text = "Verification unsuccessful",
                    style = MaterialTheme.typography.headlineSmall,
                )
                Spacer(modifier = Modifier.height(8.dp))
                if (wp.rejectionReason != null) {
                    Text(
                        text = "Reason: ${wp.rejectionReason}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error,
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
                Text(
                    text = "Please re-upload your documents and try again.",
                    style = MaterialTheme.typography.bodyMedium,
                )
                Spacer(modifier = Modifier.height(24.dp))
                KaamPrimaryButton(
                    text = "Re-upload Documents",
                    onClick = onReUpload,
                )
            }
            else -> {}
        }
    }
}
