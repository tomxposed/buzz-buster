package com.tom.buzzbuster.ui.theme

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
    primary = Crimson,
    onPrimary = TextOnCrimson,
    primaryContainer = CrimsonDark,
    onPrimaryContainer = CrimsonLight,
    secondary = CrimsonLight,
    onSecondary = Color.Black,
    secondaryContainer = CardDarkElevated,
    onSecondaryContainer = TextPrimary,
    tertiary = CrimsonLight,
    onTertiary = Color.Black,
    background = SurfaceDark,
    onBackground = TextPrimary,
    surface = SurfaceDark,
    onSurface = TextPrimary,
    surfaceVariant = CardDark,
    onSurfaceVariant = TextSecondary,
    surfaceContainerHigh = CardDarkElevated,
    outline = DividerDark,
    outlineVariant = DividerDark,
    error = ErrorRed,
    onError = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = Crimson,
    onPrimary = TextOnCrimson,
    primaryContainer = CrimsonLight,
    onPrimaryContainer = CrimsonDark,
    secondary = CrimsonDark,
    onSecondary = Color.White,
    secondaryContainer = CardLightElevated,
    onSecondaryContainer = TextPrimaryLight,
    tertiary = CrimsonDark,
    onTertiary = Color.White,
    background = SurfaceLight,
    onBackground = TextPrimaryLight,
    surface = SurfaceLight,
    onSurface = TextPrimaryLight,
    surfaceVariant = CardLight,
    onSurfaceVariant = TextSecondaryLight,
    surfaceContainerHigh = CardLightElevated,
    outline = Color(0xFFE0E0E0),
    outlineVariant = Color(0xFFE0E0E0),
    error = ErrorRed,
    onError = Color.White
)

@Composable
fun BuzzBusterTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = Color.Transparent.toArgb()
            window.navigationBarColor = Color.Transparent.toArgb()
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = !darkTheme
                isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}