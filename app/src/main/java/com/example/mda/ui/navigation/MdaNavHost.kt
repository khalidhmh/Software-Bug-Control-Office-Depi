package com.example.mda.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.mda.ui.DemoScreens.MoviesScreen
import com.example.mda.ui.DemoScreens.ProfileScreen
import com.example.mda.ui.DemoScreens.TvScreen
import com.example.mda.ui.Screens.home.HomeViewModel
import com.example.mda.ui.home.HomeScreen

/**
 * NavHost للتطبيق — هنا تسجل الراوتس (destinations).
 * عندما تكون الشاشات الحقيقية جاهزة، استبدل HomeDemoScreen بـ HomeScreen(viewModel = ...)
 */
@Composable
fun MdaNavHost(
    navController: NavHostController,
    homeViewModel: HomeViewModel
) {
    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        composable("home") {
            HomeScreen(viewModel = homeViewModel)
        }

        composable("movies") { MoviesScreen(navController) }
        composable("tv") { TvScreen(navController) }
        composable("profile") { ProfileScreen(navController) }
        // لو حبيت تضيف شاشة تفاصيل في المستقبل:
        // composable("detail/{id}") { backStackEntry -> ... }
    }
}
