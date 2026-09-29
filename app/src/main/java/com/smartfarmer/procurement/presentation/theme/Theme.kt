package com.smartfarmer.procurement.presentation.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

val GreenPrimary = Color(0xFF2E7D32)
val GreenPrimaryDark = Color(0xFF1B5E20)
val GreenSecondary = Color(0xFF388E3C)
val GreenTertiary = Color(0xFF81C784)
val AccentGold = Color(0xFFF9A825)
val EarthBrown = Color(0xFF5D4037)
val CardSurfaceLight = Color(0xFFFFFFFF)
val BackgroundLight = Color(0xFFF6F8F6)
val TextPrimary = Color(0xFF1B2624)
val TextSecondary = Color(0xFF4A5551)
val StatusBlue = Color(0xFF1976D2)
val StatusOrange = Color(0xFFE65100)
val StatusGreen = Color(0xFF2E7D32)
val StatusRed = Color(0xFFC62828)

private val LightColorScheme = lightColorScheme(
    primary = GreenPrimary,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFC8E6C9),
    onPrimaryContainer = GreenPrimaryDark,
    secondary = AccentGold,
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFFFFF9C4),
    background = BackgroundLight,
    onBackground = TextPrimary,
    surface = CardSurfaceLight,
    onSurface = TextPrimary,
    error = StatusRed,
    onError = Color.White
)

private val DarkColorScheme = darkColorScheme(
    primary = GreenTertiary,
    onPrimary = Color.Black,
    primaryContainer = GreenPrimaryDark,
    onPrimaryContainer = Color.White,
    secondary = AccentGold,
    onSecondary = Color.Black,
    background = Color(0xFF121A15),
    onBackground = Color(0xFFE8F5E9),
    surface = Color(0xFF1B2620),
    onSurface = Color(0xFFE8F5E9)
)

@Composable
fun SmartFarmerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = GreenPrimaryDark.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography(),
        content = content
    )
}
