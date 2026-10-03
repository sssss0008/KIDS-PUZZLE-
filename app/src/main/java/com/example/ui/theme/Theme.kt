package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val KidDarkColorScheme = darkColorScheme(
    primary = KidPurpleLight,
    onPrimary = Color.Black,
    secondary = KidCoralLight,
    onSecondary = Color.Black,
    tertiary = KidYellowSoft,
    background = Color(0xFF1E1F29),
    surface = Color(0xFF2B2D42),
    onBackground = Color.White,
    onSurface = Color.White
)

private val KidLightColorScheme = lightColorScheme(
    primary = KidPurple,
    onPrimary = Color.White,
    primaryContainer = KidPurpleLight.copy(alpha = 0.25f),
    onPrimaryContainer = KidPurpleDark,
    secondary = KidCoral,
    onSecondary = Color.White,
    secondaryContainer = KidCoralLight.copy(alpha = 0.25f),
    onSecondaryContainer = KidCoralDark,
    tertiary = KidGreen,
    onTertiary = Color.White,
    background = KidBackgroundLight,
    surface = KidSurfaceLight,
    onBackground = KidTextDark,
    onSurface = KidTextDark
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Keep consistent fun kid palette
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) KidDarkColorScheme else KidLightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
