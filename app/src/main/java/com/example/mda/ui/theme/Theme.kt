package com.example.mda.ui.theme

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryBlue,
    onPrimary = Color.Black,
    secondary = AccentCyan,
    onSecondary = Color.Black,
    tertiary = RatingYellow,
    onTertiary = Color.Black,
    background = DarkBackground,
    onBackground = TextPrimaryDark,
    surface = DarkSurface,
    onSurface = TextPrimaryDark,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = TextSecondaryDark,
    surfaceTint = PrimaryBlue,
    inverseSurface = LightSurface,
    inverseOnSurface = TextPrimaryLight,
    error = Color(0xFFCF6679),
    onError = Color.Black,
    outline = IconGrayDark,
    outlineVariant = DarkContainer
)

private val LightColorScheme = lightColorScheme(
    primary = PrimaryLightBlue,
    onPrimary = Color.White,
    secondary = AccentLightCyan,
    onSecondary = Color.White,
    tertiary = RatingYellowLight,
    onTertiary = Color.Black,
    background = LightBackground,
    onBackground = TextPrimaryLight,
    surface = LightSurface,
    onSurface = TextPrimaryLight,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = TextSecondaryLight,
    surfaceTint = PrimaryLightBlue,
    inverseSurface = DarkSurface,
    inverseOnSurface = TextPrimaryDark,
    error = Color(0xFFB00020),
    onError = Color.White,
    outline = IconGrayLight,
    outlineVariant = LightContainer
)

@Composable
fun MovieAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Disabled to use our custom theme colors
    content: @Composable () -> Unit
) {
    val backgroundGradient = AppBackgroundGradient(darkTheme)
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme

            // 1. هذا السطر مهم جداً: يسمح للتطبيق بالرسم خلف شريط الحالة وشريط التنقل
            WindowCompat.setDecorFitsSystemWindows(window, false)

            // 2. تغيير لون شريط التنقل (الأسهم) إلى شفاف
            window.navigationBarColor = Color.Transparent.toArgb()

            // 3. تغيير لون شريط الحالة (الساعة والبطارية) إلى شفاف أيضاً (للتناسق)
            window.statusBarColor = Color.Transparent.toArgb()

            // 4. التحكم في لون الأيقونات (جعلها فاتحة في الوضع الليلي وداكنة في الوضع النهاري)
            val controller = WindowCompat.getInsetsController(window, view)
            controller.isAppearanceLightStatusBars = !darkTheme
            controller.isAppearanceLightNavigationBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}