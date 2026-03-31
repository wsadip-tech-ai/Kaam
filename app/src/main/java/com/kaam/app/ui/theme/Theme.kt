package com.kaam.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val KaamColorScheme = lightColorScheme(
    primary = KaamPrimary,
    onPrimary = KaamOnPrimary,
    secondary = KaamSecondary,
    onSecondary = KaamOnPrimary,
    tertiary = KaamCta,
    onTertiary = KaamOnCta,
    error = KaamError,
    onError = KaamOnError,
    background = KaamBackground,
    onBackground = KaamTextPrimary,
    surface = KaamSurface,
    onSurface = KaamTextPrimary,
    surfaceVariant = KaamSurfaceTint,
    onSurfaceVariant = KaamTextSecondary,
)

@Composable
fun KaamTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = KaamColorScheme,
        typography = KaamTypography,
        content = content,
    )
}
