package com.example.mda.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.example.mda.ui.theme.Shapes

private val LightColors = lightColorScheme(
    primary = PrimaryLight,
    onPrimary = Color.White,
    background = BackgroundLight,
    onBackground = TextPrimaryLight,
    surface = SurfaceLight,
    onSurface = TextPrimaryLight
    // تقدر تكمل باقي الألوان لو محتاج
)

private val DarkColors = darkColorScheme(
    primary = PrimaryBlue, // اللون الأساسي للأزرار
    onPrimary = Color.White, // لون الكلام اللي على الأزرار
    background = AppBackground, // لون الخلفية
    onBackground = TextPrimary, // لون الكلام اللي على الخلفية
    surface = SurfaceDark, // لون الكروت
    onSurface = TextPrimary // لون الكلام اللي على الكروت
    // تقدر تكمل باقي الألوان لو محتاج
)

@Composable
fun MovieAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // التطبيق تصميمه الأساسي Dark، ممكن تفرض الـ Dark Theme
    val colors = DarkColors // or: if (darkTheme) DarkColors else LightColors

    MaterialTheme(
        colorScheme = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}