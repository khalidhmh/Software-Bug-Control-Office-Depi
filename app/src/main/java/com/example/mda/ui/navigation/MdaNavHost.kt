package com.example.mda.ui.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.mda.data.local.dao.MediaDao
import com.example.mda.data.repository.MovieDetailsRepository
import com.example.mda.data.repository.MoviesRepository
import com.example.mda.ui.home.HomeScreen
import com.example.mda.ui.screens.actordetails.ActorDetailsScreen
import com.example.mda.ui.screens.actors.ActorsScreen
import com.example.mda.ui.screens.genre.GenreScreen
import com.example.mda.ui.screens.genre.GenreViewModel
import com.example.mda.ui.screens.home.HomeViewModel
import com.example.mda.ui.screens.moivebygenrescreen.GenreDetailsScreen
import com.example.mda.ui.screens.movieDetail.MovieDetailsScreen
import com.example.mda.ui.screens.search.SearchScreen
import com.example.mda.ui.screens.search.SearchViewModel
import com.example.mda.data.repository.ActorsRepository
import com.example.mda.ui.screens.actors.ActorViewModel

// ✅ تعديل: أضفت import لـ ActorRepository (كان ناقص)

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MdaNavHost(
    navController: NavHostController,
    homeViewModel: HomeViewModel,
    moviesRepository: MoviesRepository,
    localDao: MediaDao,
    actorRepository: ActorsRepository, // ✅ موجود ومستخدم تحت
    movieDetailsRepository: MovieDetailsRepository,
    GenreViewModel: GenreViewModel,
    SearchViewModel: SearchViewModel,
    actorViewModel: ActorViewModel
) {
    NavHost(
        navController = navController,
        startDestination = "home"
    ) {

        // 🏠 Home
        composable("home") {
            HomeScreen(viewModel = homeViewModel, navController = navController)
        }

        // 👤 Actor Details
        composable(
            route = "ActorDetails/{personId}",
            arguments = listOf(navArgument("personId") { type = NavType.IntType })
        ) {
            val personId = it.arguments?.getInt("personId") ?: 0
            if (personId != 0) {
                ActorDetailsScreen(
                    personId = personId,
                    navController = navController,
                    repository = actorRepository
                )
            }
        }

        // 🎭 Genres List
        composable("genres") {
            GenreScreen(navController = navController, GenreViewModel)
        }

        // 🎬 Genre Details
        composable(
            route = "genre_details/{genreId}/{genreName}",
            arguments = listOf(
                navArgument("genreId") { type = NavType.IntType },
                navArgument("genreName") { type = NavType.StringType }
            )
        ) {
            val genreId = it.arguments?.getInt("genreId") ?: 0
            val genreName = it.arguments?.getString("genreName") ?: ""
            GenreDetailsScreen(
                navController = navController,
                repository = moviesRepository,
                genreId = genreId,
                genreNameRaw = genreName
            )
        }

        // 🎞️ Movies (Genre reuse)
        composable("movies") {
            GenreScreen(navController = navController, GenreViewModel)
        }

        // 🌟 Actors List (People)
        composable("actors") {
            // ✅ تعديل: استخدمنا ActorsScreen الجديدة اللي فيها Offline Mode + كاش
            ActorsScreen(
                navController = navController,
                repository = actorRepository,
                viewModel=actorViewModel,
            )
        }

        // 🔍 Search
        composable("search") {
            SearchScreen(
                navController = navController,
                SearchViewModel
            )
        }

        // 🎥 Movie/TV Details
        composable(
            route = "detail/{mediaType}/{id}",
            arguments = listOf(
                navArgument("mediaType") { type = NavType.StringType },
                navArgument("id") { type = NavType.IntType }
            )
        ) {
            val type = it.arguments?.getString("mediaType") ?: "movie"
            val id = it.arguments?.getInt("id") ?: 0
            MovieDetailsScreen(
                id = id,
                isTvShow = (type == "tv"),
                navController = navController,
                repository = movieDetailsRepository
            )
        }
    }
}
fun getTitleForRoute(route: String?): String = when (route) {
    "home" -> "Home"
    "movies" -> "Movies"
    "actors" -> "Actors"
    "search" -> "Search"
    else -> ""
}
