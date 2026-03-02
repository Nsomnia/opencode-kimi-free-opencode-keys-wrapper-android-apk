// =============================================================================
// Color.kt
// =============================================================================
// This file defines the color palette for the Kilo Companion app.
// Using Material Design 3 color system with a custom theme.
// 
// Material 3 uses these color roles:
// - Primary: Main brand color, used for key components
// - Secondary: Accent color, used for less prominent components
// - Tertiary: Balanced complement to primary and secondary
// - Error: Used for error states and destructive actions
// - Background/Surface: Background colors for different elevation levels
// =============================================================================

package com.kilo.companion.ui.theme

import androidx.compose.ui.graphics.Color

// =============================================================================
// Light Theme Colors
// =============================================================================

// Primary colors - Deep purple, representing AI/tech
val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

// Primary colors - Darker variants for light theme
val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)

// =============================================================================
// Custom App Colors
// =============================================================================
// These are specific to Kilo Companion for consistent branding

// Primary brand color - Vibrant purple
val KiloPrimary = Color(0xFF6B4EFF)
val KiloPrimaryDark = Color(0xFF4A32CC)
val KiloPrimaryLight = Color(0xFF9B7EFF)

// Secondary accent - Teal for contrast
val KiloSecondary = Color(0xFF00BFA5)
val KiloSecondaryDark = Color(0xFF008E76)

// Background colors
val BackgroundLight = Color(0xFFFFFBFE)
val BackgroundDark = Color(0xFF1C1B1F)
val SurfaceLight = Color(0xFFFFFBFE)
val SurfaceDark = Color(0xFF1C1B1F)

// Status colors
val SuccessGreen = Color(0xFF4CAF50)
val ErrorRed = Color(0xFFE53935)
val WarningOrange = Color(0xFFFF9800)
val InfoBlue = Color(0xFF2196F3)

// Text colors
val TextPrimaryLight = Color(0xFF1C1B1F)
val TextSecondaryLight = Color(0xFF49454F)
val TextPrimaryDark = Color(0xFFE6E1E5)
val TextSecondaryDark = Color(0xFFCAC4D0)

// File type indicator colors
val JsonFileColor = Color(0xFFFFA000)  // Amber for JSON
val ConfigFileColor = Color(0xFF7C4DFF) // Deep purple for config
val AuthFileColor = Color(0xFF00BCD4)   // Cyan for auth files
val TextFileColor = Color(0xFF607D8B)   // Blue grey for text
