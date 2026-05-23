package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = ElectricBlue,
    onPrimary = CyberDarkBg,
    primaryContainer = ElectricBlueSecondary,
    onPrimaryContainer = Color.White,
    secondary = Color(0xFF38BDF8),
    onSecondary = CyberDarkBg,
    background = CyberDarkBg,
    onBackground = TextWhite,
    surface = CyberSurface,
    onSurface = TextWhite,
    surfaceVariant = CyberCardColor,
    onSurfaceVariant = TextSubtle,
    tertiary = CyberSuccessGreen,
    onTertiary = CyberDarkBg,
    error = CyberErrorRed,
    onError = Color.White,
    outline = BorderColor
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF0284C7),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE0F2FE),
    onPrimaryContainer = Color(0xFF0369A1),
    secondary = Color(0xFF0EA5E9),
    onSecondary = Color.White,
    background = Color(0xFFF8FAFC),
    onBackground = Color(0xFF0F172A),
    surface = Color.White,
    onSurface = Color(0xFF0F172A),
    surfaceVariant = Color(0xFFF1F5F9),
    onSurfaceVariant = Color(0xFF475569),
    tertiary = Color(0xFF059669),
    onTertiary = Color.White,
    error = Color(0xFFDC2626),
    onError = Color.White,
    outline = Color(0xFFCBD5E1)
)

@Composable
fun MyApplicationTheme(
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
