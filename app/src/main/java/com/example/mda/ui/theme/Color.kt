// Unified color palette for Dark and Light themes
// All colors are centralized here to ensure consistent theming throughout the app

package com.example.mda.ui.theme

import android.R.style.Theme
import android.content.res.Resources
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
val AppVerticalGradient = Brush.verticalGradient(
    colorStops = arrayOf(
        0.0f to Color(0xFF0C2B4E),  // 0%
        0.35f to Color(0xFF1A3D64), // 35%
        1.0f to Color(0xFF1D546C)   // 100%
    )
)


// ===================== DARK THEME COLORS =====================
val DarkBackground = Color(0xFF0D0F1C)      // Main dark background
val DarkSurface = Color(0xFF1A1C2A)         // Card and component surfaces
val DarkSurfaceVariant = Color(0xFF1A2233)  // Alternative surface for cards
val DarkContainer = Color(0xFF101528)       // Container backgrounds (BottomBar, etc.)
val DarkCardBackground = Color(0xFF1E1E3A)  // Background for specific cards
val DarkInfoCard = Color(0xFF2C2C4A)        // Info card backgrounds
val DarkMovieCard = Color(0xFF1E1E2E)       // Movie card backgrounds

val PrimaryBlue = Color(0xFF60D2FF)         // Primary blue color
val AccentCyan = Color(0xFF53DEED)          // Accent cyan for emphasis
val RatingYellow = Color(0xFFFFC107)        // Star ratings

val TextPrimaryDark = Color(0xFFFFFFFF)     // Primary text on dark theme
val TextSecondaryDark = Color(0xFFC5D1D9)   // Secondary text on dark theme
val TextAccentDark = Color(0xFFAAAAFF)      // Accent text (purple-ish)
val TextSubtleDark = Color(0xFFB0B0C0)      // Subtle text for secondary info
val TextDimDark = Color(0xFFE0E0E0)         // Dimmed text

val IconGrayDark = Color(0xFF757575)        // Gray icons

// ===================== LIGHT THEME COLORS =====================
val LightBackground = Color(0xFFF6F7FB)     // Main light background
val LightSurface = Color(0xFFFFFFFF)        // Card and component surfaces
val LightSurfaceVariant = Color(0xFFF5F5F5) // Alternative surface for cards
val LightContainer = Color(0xFFEAEFF3)      // Container backgrounds
val LightCardBackground = Color(0xFFF8F8FA) // Background for specific cards
val LightInfoCard = Color(0xFFE8EAF6)       // Info card backgrounds
val LightMovieCard = Color(0xFFF5F5F5)      // Movie card backgrounds

val PrimaryLightBlue = Color(0xFF1976D2)    // Primary blue for light theme
val AccentLightCyan = Color(0xFF26C6DA)     // Accent cyan for light theme
val RatingYellowLight = Color(0xFFFFC107)   // Star ratings (same as dark)

val TextPrimaryLight = Color(0xFF212121)    // Primary text on light theme
val TextSecondaryLight = Color(0xFF757575)  // Secondary text on light theme
val TextAccentLight = Color(0xFF5E35B1)     // Accent text (purple)
val TextSubtleLight = Color(0xFF616161)     // Subtle text for secondary info
val TextDimLight = Color(0xFF424242)        // Dimmed text

val IconGrayLight = Color(0xFF9E9E9E)// Gray icons

// ===================== GRADIENTS =====================
@Composable
fun AppBackgroundGradient(darkTheme: Boolean = isSystemInDarkTheme()): Brush {
    return if (darkTheme) {
        // 🌙 تدرّج الثيم الداكن
        Brush.verticalGradient(
            colorStops = arrayOf(
                0.0f to Color(0xFF0C2B4E),
                0.35f to Color(0xFF1A3D64),
                1.0f to Color(0xFF1D546C)
            )
        )
    } else {
        // ☀️ تدرّج الثيم النهاري
        Brush.verticalGradient(
            colorStops = arrayOf(
                0.0f to Color(0xFFEAEFEF),
                0.35f to Color(0xFFB8CFCE),
                1.0f to Color(0xFF2973B2)
            )
        )
    }
}


//=======================================================

@Composable
fun AppTopBarColors(darkTheme: Boolean = isSystemInDarkTheme()): Pair<Color, Color> {
    return if (darkTheme) {
        // 🌙 داكن
        Color(0xFF0C2B4E) to Color(0xFFFFFFFF)
    } else {
        // ☀️ فاتح
        Color(0xFFEAEFEF) to Color(0xFF212121)
    }
}