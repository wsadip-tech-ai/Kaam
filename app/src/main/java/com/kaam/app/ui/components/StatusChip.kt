package com.kaam.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kaam.app.data.model.BookingStatus
import com.kaam.app.data.model.VerificationStatus
import com.kaam.app.ui.theme.KaamActiveTint
import com.kaam.app.ui.theme.KaamCta
import com.kaam.app.ui.theme.KaamError
import com.kaam.app.ui.theme.KaamPendingTint
import com.kaam.app.ui.theme.KaamPrimary
import com.kaam.app.ui.theme.KaamRejectedTint
import com.kaam.app.ui.theme.KaamVerifiedTint
import com.kaam.app.ui.theme.KaamWarning

@Composable
fun VerificationChip(status: VerificationStatus, modifier: Modifier = Modifier) {
    val (text, bgColor, textColor) = when (status) {
        VerificationStatus.VERIFIED -> Triple("Verified", KaamVerifiedTint, KaamCta)
        VerificationStatus.PENDING -> Triple("Pending", KaamPendingTint, KaamWarning)
        VerificationStatus.REJECTED -> Triple("Rejected", KaamRejectedTint, KaamError)
    }
    StatusChipBase(text = text, backgroundColor = bgColor, textColor = textColor, modifier = modifier)
}

@Composable
fun BookingStatusChip(status: BookingStatus, modifier: Modifier = Modifier) {
    val (text, bgColor, textColor) = when (status) {
        BookingStatus.PENDING -> Triple("Pending", KaamPendingTint, KaamWarning)
        BookingStatus.ACCEPTED -> Triple("Accepted", KaamActiveTint, KaamPrimary)
        BookingStatus.DECLINED -> Triple("Declined", KaamRejectedTint, KaamError)
        BookingStatus.COMPLETED -> Triple("Completed", KaamVerifiedTint, KaamCta)
        BookingStatus.CANCELLED -> Triple("Cancelled", KaamRejectedTint, KaamError)
    }
    StatusChipBase(text = text, backgroundColor = bgColor, textColor = textColor, modifier = modifier)
}

@Composable
private fun StatusChipBase(
    text: String,
    backgroundColor: Color,
    textColor: Color,
    modifier: Modifier = Modifier,
) {
    Text(
        text = text,
        color = textColor,
        fontSize = 12.sp,
        fontWeight = FontWeight.SemiBold,
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(backgroundColor)
            .padding(horizontal = 14.dp, vertical = 6.dp),
    )
}
