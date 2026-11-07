package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = ClinispireTeal,
    onPrimary = ClinispireDeepNavy,
    primaryContainer = ClinispireTealDark,
    onPrimaryContainer = ClinispireTealLight,
    secondary = ClinispirePrimaryBlue,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF1D4ED8),
    onSecondaryContainer = Color(0xFFDBEAFE),
    tertiary = ClinispireTeal,
    background = ClinispireDarkBg,
    onBackground = Color(0xFFF1F5F9),
    surface = ClinispireDarkSurface,
    onSurface = Color(0xFFF1F5F9),
    surfaceVariant = ClinispireDarkCard,
    onSurfaceVariant = Color(0xFF94A3B8),
    outline = ClinispireBorderDark,
    error = ClinispireDanger
)

private val LightColorScheme = lightColorScheme(
    primary = ClinispirePrimaryBlue,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFDBEAFE),
    onPrimaryContainer = ClinispireDeepNavy,
    secondary = ClinispireTeal,
    onSecondary = Color.White,
    secondaryContainer = ClinispireTealLight,
    onSecondaryContainer = ClinispireDeepNavy,
    tertiary = ClinispireDeepNavy,
    background = ClinispireLightBg,
    onBackground = ClinispireTextDark,
    surface = ClinispireWhite,
    onSurface = ClinispireTextDark,
    surfaceVariant = Color(0xFFEEF2F6),
    onSurfaceVariant = ClinispireTextMuted,
    outline = ClinispireBorderLight,
    error = ClinispireDanger
)

@Composable
fun ClinispireTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
