// =============================================================================
// Theme.kt
// =============================================================================
// This file defines the Material Design 3 theme for the Kilo Companion app.
// It provides light and dark color schemes and applies them to the app.
// 
// Key concepts:
// - ColorScheme: Defines colors for all Material components
// - Typography: Text styles (defined in Type.kt)
// - Shapes: Corner radius for components
// =============================================================================

package com.kilo.companion.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// =============================================================================
// Light Color Scheme
// =============================================================================
// Defines colors used in light mode
private val LightColorScheme = lightColorScheme(
    primary = KiloPrimary,
    onPrimary = Color.White,
    primaryContainer = KiloPrimaryLight,
    onPrimaryContainer = Color(0xFF21005D),
    
    secondary = KiloSecondary,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF6FFFE6),
    onSecondaryContainer = Color(0xFF00201A),
    
    tertiary = Pink40,
    onTertiary = Color.White,
    tertiaryContainer = Pink80,
    onTertiaryContainer = Color(0xFF31111D),
    
    error = ErrorRed,
    onError = Color.White,
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002),
    
    background = BackgroundLight,
    onBackground = TextPrimaryLight,
    surface = SurfaceLight,
    onSurface = TextPrimaryLight,
    surfaceVariant = Color(0xFFE7E0EC),
    onSurfaceVariant = TextSecondaryLight,
    
    outline = Color(0xFF79747E)
)

// =============================================================================
// Dark Color Scheme
// =============================================================================
// Defines colors used in dark mode
private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    onPrimary = Color(0xFF381E72),
    primaryContainer = Color(0xFF4F378B),
    onPrimaryContainer = Purple80,
    
    secondary = KiloSecondary,
    onSecondary = Color(0xFF00382D),
    secondaryContainer = Color(0xFF005144),
    onSecondaryContainer = Color(0xFF6FFFE6),
    
    tertiary = Pink80,
    onTertiary = Color(0xFF492532),
    tertiaryContainer = Color(0xFF633B48),
    onTertiaryContainer = Pink80,
    
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),
    
    background = BackgroundDark,
    onBackground = TextPrimaryDark,
    surface = SurfaceDark,
    onSurface = TextPrimaryDark,
    surfaceVariant = Color(0xFF49454F),
    onSurfaceVariant = TextSecondaryDark,
    
    outline = Color(0xFF938F99)
)

// =============================================================================
// Kilo Companion Theme
// =============================================================================
/**
 * Main theme composable for the Kilo Companion app.
 * 
 * This function wraps content with MaterialTheme, providing consistent
 * colors, typography, and shapes throughout the app.
 * 
 * @param darkTheme Whether to use dark color scheme. Defaults to system setting.
 * @param dynamicColor Whether to use dynamic colors (Android 12+). Defaults to true.
 * @param content The composable content to theme
 */
@Composable
fun KiloCompanionTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        // Use dynamic colors on Android 12+ if enabled
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        // Otherwise use our custom color schemes
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            // Set status bar color to match theme
            window.statusBarColor = colorScheme.primary.toArgb()
            // Configure status bar icons to be visible
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
