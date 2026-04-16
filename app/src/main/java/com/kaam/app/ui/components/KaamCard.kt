package com.kaam.app.ui.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun KaamCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    val shape = RoundedCornerShape(16.dp)
    val colors = CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.surface,
    )
    val elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)

    if (onClick != null) {
        Card(
            onClick = onClick,
            shape = shape,
            colors = colors,
            elevation = elevation,
            modifier = modifier,
        ) { content() }
    } else {
        Card(
            shape = shape,
            colors = colors,
            elevation = elevation,
            modifier = modifier,
        ) { content() }
    }
}
