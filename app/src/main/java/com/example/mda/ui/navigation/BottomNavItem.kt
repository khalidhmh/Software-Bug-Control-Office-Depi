package com.example.mda.ui.navigation

import androidx.compose.ui.graphics.vector.ImageVector

/**
 * تمثيل عنصر في الـ Bottom Bar.
 * route: اسم الراوت في NavHost (مثلاً "home", "movies", "tv", "profile")
 * icon: الأيقونة المعروضة
 * label: النص القصير تحت الأيقونة
 */
data class BottomNavItem(
    val route: String,
    val icon: ImageVector,
    val label: String
)
