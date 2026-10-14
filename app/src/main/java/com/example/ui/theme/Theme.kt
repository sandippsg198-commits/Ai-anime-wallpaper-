package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val AnimeColorScheme = darkColorScheme(
    primary = NeonPink,
    onPrimary = TextPrimary,
    primaryContainer = CosmicPurple,
    onPrimaryContainer = TextPrimary,
    secondary = ElectricCyan,
    onSecondary = AnimeDarkBackground,
    secondaryContainer = AnimeSurfaceVariant,
    onSecondaryContainer = ElectricCyan,
    tertiary = GoldGlow,
    onTertiary = AnimeDarkBackground,
    background = AnimeDarkBackground,
    onBackground = TextPrimary,
    surface = AnimeSurface,
    onSurface = TextPrimary,
    surfaceVariant = AnimeSurfaceVariant,
    onSurfaceVariant = TextSecondary,
    outline = BorderSubtle
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = AnimeColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = AnimeDarkBackground.toArgb()
                window.navigationBarColor = AnimeDarkBackground.toArgb()
                val controller = WindowCompat.getInsetsController(window, view)
                controller.isAppearanceLightStatusBars = false
                controller.isAppearanceLightNavigationBars = false
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
