package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val NinjaDarkColorScheme = darkColorScheme(
    primary = ElectricGold,
    onPrimary = ShadowBlack,
    primaryContainer = MidnightBlueLight,
    onPrimaryContainer = ElectricGoldBright,
    secondary = CyanAccent,
    onSecondary = ShadowBlack,
    secondaryContainer = MidnightBlueLight,
    onSecondaryContainer = CyanAccent,
    tertiary = CrimsonAlert,
    onTertiary = TextPrimary,
    background = ShadowBlack,
    onBackground = TextPrimary,
    surface = MidnightBlue,
    onSurface = TextPrimary,
    surfaceVariant = ShadowSurfaceVariant,
    onSurfaceVariant = TextSecondary,
    error = CrimsonAlert,
    onError = TextPrimary
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = NinjaDarkColorScheme,
        typography = Typography,
        content = content
    )
}
