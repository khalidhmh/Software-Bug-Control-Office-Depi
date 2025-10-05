package com.example.mda.ui.navigation

import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

/**
 * Bottom navigation bar عام وجاهز للاستخدام مع NavController.
 * لا يغير الشكل — يمنحك سلوك Nav كامل (save/restore state).
 */
@Composable
fun BottomNavigationBar(navController: NavController) {
    val items = listOf(
        BottomNavItem(route = "home", icon = Icons.Default.Home, label = "Home"),
        BottomNavItem(route = "movies", icon = Icons.Default.Movie, label = "Movies"),
        BottomNavItem(route = "tv", icon = Icons.Default.Tv, label = "TV"),
        BottomNavItem(route = "profile", icon = Icons.Default.Person, label = "Profile")
    )

    // currentBackStackEntryAsState() يستخدم لمراقبة الراوت الحالي
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface, // بدل من لون مختلف
        tonalElevation = 3.dp, // ظل خفيف
        modifier = Modifier.height(56.dp) // بدل من 80dp أو أكتر
    ) {
        items.forEach { item ->
            val selected = currentRoute == item.route
            NavigationBarItem(
                selected = selected,
                onClick = {
                    if (currentRoute != item.route) {
                        // هذه الإعدادات تضمن سلوك جيد عند التنقل (singleTop, restore state)
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label,
                        // اللون يتغير حسب الـ selected (تقدر تعدل الألوان حسب الثيم)
                        tint = if (selected) Color.Cyan else Color.Gray
                    )
                },
                label = { Text(item.label, color = if (selected) Color.Cyan else Color.Gray) }
            )
        }
    }
}
