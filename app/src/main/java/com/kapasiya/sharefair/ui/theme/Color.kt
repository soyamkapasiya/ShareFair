package com.kapasiya.sharefair.ui.theme

import androidx.compose.ui.graphics.Color

// Sky Blue & Standard Palette
val SkyBlue = Color(0xFF00B0FF)      // Primary Sky Blue
val SkyBlueDark = Color(0xFF0081CB)  // Darker Sky Blue
val SkyBlueLight = Color(0xFF80D8FF) // Lighter Sky Blue

val PureWhite = Color(0xFFFFFFFF)
val OffWhite = Color(0xFFF8F9FA)     // Very light grey/white for surfaces
val BackgroundBlue = Color(0xFFF0F7FF) // Extremely light blue for app background

val TextPrimary = Color(0xFF1A1C1E)   // Standard Dark Grey/Black for text
val TextSecondary = Color(0xFF44474E) // Standard Muted Grey for text

val SuccessGreen = Color(0xFF2E7D32)
val ErrorRed = Color(0xFFBA1A1A)
val GlassyOverlay = Color(0x33FFFFFF)

// Keep legacy names as aliases for backward compatibility during migration
val DarkPurple = SkyBlueDark
val MediumPurple = SkyBlue
val SandBeige = SkyBlueLight
val SoftSalmon = Color(0xFFFFB4AB) // Standard coral for error/accent if needed
val WarmWhite = PureWhite
val WarmCream = BackgroundBlue
val SoftPeach = OffWhite
