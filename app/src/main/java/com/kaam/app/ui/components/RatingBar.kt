package com.kaam.app.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kaam.app.ui.theme.KaamTextSecondary
import com.kaam.app.ui.theme.KaamWarning

@Composable
fun RatingBar(
    rating: Int,
    modifier: Modifier = Modifier,
    onRatingChanged: ((Int) -> Unit)? = null,
) {
    Row(modifier = modifier) {
        (1..5).forEach { star ->
            val icon = if (star <= rating) Icons.Filled.Star else Icons.Outlined.Star
            val tint = if (star <= rating) KaamWarning else KaamTextSecondary
            Icon(
                imageVector = icon,
                contentDescription = "Star $star",
                tint = tint,
                modifier = Modifier
                    .size(24.dp)
                    .then(
                        if (onRatingChanged != null) Modifier.clickable { onRatingChanged(star) }
                        else Modifier
                    ),
            )
        }
    }
}
