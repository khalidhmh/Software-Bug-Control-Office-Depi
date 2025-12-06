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
import com.example.mda.localization.LocalizationKeys
import com.example.mda.localization.localizedString


@Composable
fun BottomNavigationBar(navController: NavController) {
    val items = listOf(
        BottomNavItem(route = "home", icon = Icons.Default.Home, label = localizedString(LocalizationKeys.NAV_HOME)),
        BottomNavItem(route = "movies", icon = Icons.Default.Movie, label = localizedString(LocalizationKeys.NAV_MOVIES)),
        BottomNavItem(route = "tv", icon = Icons.Default.Tv, label = localizedString(LocalizationKeys.NAV_TV)),
        BottomNavItem(route = "profile", icon = Icons.Default.Person, label = localizedString(LocalizationKeys.NAV_PROFILE))
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 3.dp,
        modifier = Modifier.height(56.dp)
    ) {
        items.forEach { item ->
            val selected = currentRoute == item.route
            NavigationBarItem(
                selected = selected,
                onClick = {
                    if (currentRoute != item.route) {
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
                        tint = if (selected) Color.Cyan else Color.Gray
                    )
                },
                label = { Text(item.label, color = if (selected) Color.Cyan else Color.Gray) }
            )
        }
    }
}
