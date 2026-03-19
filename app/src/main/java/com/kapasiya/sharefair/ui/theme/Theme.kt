package com.kapasiya.sharefair.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = SkyBlueLight,
    secondary = SkyBlue,
    tertiary = SkyBlueDark,
    background = Color(0xFF0D1B2A), // Dark navy
    surface = Color(0xFF1B263B),
    onPrimary = Color(0xFF00344F),
    onSecondary = Color(0xFF00344F),
    onTertiary = PureWhite,
    onBackground = PureWhite,
    onSurface = PureWhite,
    error = ErrorRed,
)

private val LightColorScheme = lightColorScheme(
    primary = SkyBlue,
    secondary = SkyBlueLight,
    tertiary = SkyBlueDark,
    background = BackgroundBlue,
    surface = PureWhite,
    onPrimary = PureWhite,
    onSecondary = SkyBlueDark,
    onTertiary = PureWhite,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    error = ErrorRed,
)

@Composable
fun ShareFairTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Disabled to use brand colors
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            window.navigationBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
