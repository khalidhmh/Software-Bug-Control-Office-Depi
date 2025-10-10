package com.example.mda.ui.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.mda.data.repository.MoviesRepository
import com.example.mda.ui.DemoScreens.MoviesScreen
import com.example.mda.ui.DemoScreens.ProfileScreen
import com.example.mda.ui.DemoScreens.TvScreen
import com.example.mda.ui.Screens.MovieDetail.MovieDetailsScreen
import com.example.mda.ui.Screens.home.HomeViewModel
import com.example.mda.ui.actordetails.ActorDetailsScreen
import com.example.mda.ui.actors.ActorsScreen
import com.example.mda.ui.genreScreen.GenreScreen
import com.example.mda.ui.home.HomeScreen
import com.example.mda.ui.moivebygenrescreen.GenreDetailsScreen

/**
 * NavHost للتطبيق — هنا تسجل الراوتس (destinations).
 * عندما تكون الشاشات الحقيقية جاهزة، استبدل HomeDemoScreen بـ HomeScreen(viewModel = ...)
 */
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MdaNavHost(
    navController: NavHostController,
    homeViewModel: HomeViewModel,
    repository: MoviesRepository
) {
    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        composable("home") {
            HomeScreen(viewModel = homeViewModel)
        }

        composable(
            route = "ActorDetails/{personId}"
        ) { backStackEntry ->
            val personId = backStackEntry.arguments?.getString("personId")?.toInt() ?: 0
            ActorDetailsScreen(personId = personId , navController = navController)
        }


        composable("genres") {
            GenreScreen(
                navController = navController,
                repository = repository // ⚡️ تمرير Repository
            )
        }

        // 🔵 شاشة تفاصيل الجينرا (الأفلام)
        composable(
            route = "genre_details/{genreId}/{genreName}",
            arguments = listOf(
                navArgument("genreId") { type = NavType.IntType },
                navArgument("genreName") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val genreId = backStackEntry.arguments?.getInt("genreId") ?: 0
            val genreName = backStackEntry.arguments?.getString("genreName") ?: ""
            GenreDetailsScreen(
                navController = navController,
                repository = repository, // ⚡️ تمرير Repository
                genreId = genreId,
                genreNameRaw = genreName
            )
        }
        composable("movies") { GenreScreen(navController,repository) }
        composable("Actors") { ActorsScreen( navController) }
        composable("profile") { ProfileScreen(navController) }
        // لو حبيت تضيف شاشة تفاصيل في المستقبل:
        // composable("detail/{id}") { backStackEntry -> ... }
        composable("movie_detail/{movieId}") { backStackEntry ->
            val movieId = backStackEntry.arguments?.getString("movieId")?.toIntOrNull() ?: 0
            // get repository instance that you have in the host
            MovieDetailsScreen(
                movieId = movieId,
                navController = navController,
                repository = repository // ⚠️ ensure 'repository' variable is available here (pass it down like other screens)
            )
        }
    }
}

