package com.example.mda.ui.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.mda.data.local.dao.MediaDao
import com.example.mda.data.repository.ActorsRepository
import com.example.mda.data.repository.MovieDetailsRepository
import com.example.mda.data.repository.MoviesRepository
import com.example.mda.ui.home.HomeScreen
import com.example.mda.ui.screens.actordetails.ActorDetailsScreen
import com.example.mda.ui.screens.actors.ActorsScreen
import com.example.mda.ui.screens.genreScreen.GenreScreen
import com.example.mda.ui.screens.genre.GenreViewModel
import com.example.mda.ui.screens.home.HomeViewModel
import com.example.mda.ui.screens.home.HomeViewModelFactory
import com.example.mda.ui.screens.moivebygenrescreen.GenreDetailsScreen
import com.example.mda.ui.screens.movieDetail.MovieDetailsScreen
import com.example.mda.ui.screens.search.SearchScreen
import com.example.mda.ui.screens.search.SearchViewModel
import com.example.mda.ui.screens.search.SearchViewModelFactory
import com.example.mda.util.GenreViewModelFactory

// ✅ تعديل شامل: تم تنظيف تعريف الدالة وتصحيح بنية كل الشاشات
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MdaNavHost(
    navController: NavHostController,
    moviesRepository: MoviesRepository,
    actorsRepository: ActorsRepository,
    movieDetailsRepository: MovieDetailsRepository,
    localDao: MediaDao,
    onTopBarStateChange: (TopBarState) -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = "home"
    ) {

        // 🏠 Home
        composable("home") {
            // ✅ صحيح: الشاشة تنشئ الـ ViewModel الخاص بها
            val homeViewModel: HomeViewModel = viewModel(factory = HomeViewModelFactory(moviesRepository))
            HomeScreen(
                viewModel = homeViewModel,
                navController = navController,
                onTopBarStateChange = onTopBarStateChange
            )
        }

        // 🌟 Actors List (People)
        composable("actors") {
            // ✅ صحيح: تم تمرير الـ repository والدالة بشكل صحيح
            ActorsScreen(
                navController = navController,
                actorsRepository = actorsRepository,
                onTopBarStateChange = onTopBarStateChange
            )
        }

        // 🔍 Search
        composable("search") {
            // ✅ صحيح: الشاشة تنشئ الـ ViewModel الخاص بها
            val searchViewModel: SearchViewModel = viewModel(factory = SearchViewModelFactory(
                moviesRepository
                ,localDao

            )
            )
            SearchScreen(
                navController = navController,
                viewModel = searchViewModel,
                onTopBarStateChange = onTopBarStateChange
            )
        }

        // 🎞️ Movies / Genres List
        // تم دمج "movies" و "genres" في مسار واحد لأنهما يعرضان نفس الشاشة
        composable("movies") {
            // ✅ صحيح: الشاشة تنشئ الـ ViewModel الخاص بها
            val genreViewModel: GenreViewModel = viewModel(factory = GenreViewModelFactory(moviesRepository))
            GenreScreen(
                navController = navController,
                viewModel = genreViewModel,
                onTopBarStateChange = onTopBarStateChange
            )
        }

        // 👤 Actor Details
        composable(
            route = "ActorDetails/{personId}",
            arguments = listOf(navArgument("personId") { type = NavType.IntType })
        ) {
            val personId = it.arguments?.getInt("personId") ?: 0
            ActorDetailsScreen(
                personId = personId,
                navController = navController,
                repository = actorsRepository,
                onTopBarStateChange = onTopBarStateChange
            )
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
                genreNameRaw = genreName,
                onTopBarStateChange = onTopBarStateChange
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
                repository = movieDetailsRepository,
                onTopBarStateChange = onTopBarStateChange
            )
        }
    }
}
