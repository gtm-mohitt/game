package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = EmeraldAccent,
    onPrimary = Navy950,
    primaryContainer = EmeraldDark,
    onPrimaryContainer = White,
    secondary = CyanAccent,
    onSecondary = Navy950,
    tertiary = GoldYellow,
    onTertiary = Navy950,
    background = Navy950,
    onBackground = TextPrimary,
    surface = CardDark,
    onSurface = TextPrimary,
    surfaceVariant = Navy800,
    onSurfaceVariant = TextSecondary,
    outline = CardBorder,
    error = LiveRed,
    onError = White
)

private val LightColorScheme = DarkColorScheme // Default to sports dark navy theme for consistent stadium aesthetic

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
