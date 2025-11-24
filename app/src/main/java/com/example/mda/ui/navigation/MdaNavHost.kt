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
import com.example.mda.data.repository.MovieDetailsRepository
import com.example.mda.data.repository.MoviesRepository
import com.example.mda.ui.screens.actordetails.ActorDetailsScreen
import com.example.mda.ui.screens.actors.ActorsScreen
import com.example.mda.ui.screens.genreScreen.GenreViewModel
import com.example.mda.ui.screens.home.HomeViewModel
import com.example.mda.ui.screens.home.HomeViewModelFactory
import com.example.mda.ui.screens.moivebygenrescreen.GenreDetailsScreen
import com.example.mda.ui.screens.movieDetail.MovieDetailsScreen
import com.example.mda.ui.screens.search.SearchScreen
import com.example.mda.ui.screens.search.SearchViewModel
import com.example.mda.util.GenreViewModelFactory
import com.example.mda.data.repository.ActorsRepository
import com.example.mda.data.repository.AuthRepository
import com.example.mda.ui.home.HomeScreen
import com.example.mda.ui.screens.actors.ActorViewModel
import com.example.mda.ui.screens.genreScreen.GenreScreen
import com.example.mda.ui.screens.profile.ProfileScreen
import com.example.mda.ui.screens.favorites.FavoritesViewModel
import com.example.mda.ui.screens.auth.AuthViewModel
import com.example.mda.ui.screens.auth.LoginScreen
import com.example.mda.ui.screens.auth.SignupScreen
import com.example.mda.ui.screens.auth.AccountScreen
import com.example.mda.ui.screens.onboarding.OnboardingScreen
import com.example.mda.ui.screens.splash.SplashScreen

// ✅ تعديل: أضفت import لـ ActorRepository (كان ناقص)

// ✅ تعديل شامل: تم تنظيف تعريف الدالة وتصحيح بنية كل الشاشات
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MdaNavHost(
    navController: NavHostController,
    moviesRepository: MoviesRepository,
    actorsRepository: ActorsRepository,
    movieDetailsRepository: MovieDetailsRepository,
    localDao: MediaDao,
    onTopBarStateChange: (TopBarState) -> Unit,
    GenreViewModel: GenreViewModel,
    SearchViewModel: SearchViewModel,
    actorViewModel: ActorViewModel,
    favoritesViewModel: FavoritesViewModel,
    authViewModel: AuthViewModel?,
    authRepository: AuthRepository
) {
    NavHost(
        navController = navController,
        startDestination = "splash"
    ){
        composable("splash") {
            SplashScreen(navController = navController)
        }

        // 🏠 Home
        composable("home") {
            // ✅ صحيح: الشاشة تنشئ الـ ViewModel الخاص بها
            val homeViewModel: HomeViewModel = viewModel(factory = HomeViewModelFactory(moviesRepository, authRepository))
            HomeScreen(
                viewModel = homeViewModel,
                navController = navController,
                onTopBarStateChange = onTopBarStateChange,
                favoritesViewModel = favoritesViewModel
            )
        }
        // 👇 نضيفها هنا داخل NavHost في MdaNavHost.kt
        composable("onboarding") {
            OnboardingScreen(navController = navController)
        }

        // 🌟 Actors List (People)
        composable("actors") {
            // ✅ صحيح: تم تمرير الـ repository والدالة بشكل صحيح
            ActorsScreen(
                navController = navController,
                actorsRepository = actorsRepository,
                onTopBarStateChange = onTopBarStateChange,
                viewModel = actorViewModel
            )
        }

        // 🔍 Search
        composable("search") {
            // ✅ صحيح: الشاشة تنشئ الـ ViewModel الخاص بها
            SearchScreen(
                navController = navController,
                viewModel = SearchViewModel,
                onTopBarStateChange = onTopBarStateChange,
                favoritesViewModel = favoritesViewModel
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
                onTopBarStateChange = onTopBarStateChange,
                favoritesViewModel = favoritesViewModel
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
                onTopBarStateChange = onTopBarStateChange,
                favoritesViewModel = favoritesViewModel
            )
        }

        // 🎞️ Movies (Genre reuse)
        composable("movies") {
            GenreScreen(navController = navController, GenreViewModel,onTopBarStateChange)
        }

        // 🌟 Actors List (People)
        composable("actors") {
            // ✅ تعديل: استخدمنا ActorsScreen الجديدة اللي فيها Offline Mode + كاش
            ActorsScreen(
                navController = navController,
                actorsRepository = actorsRepository,
                viewModel = actorViewModel,
                onTopBarStateChange = onTopBarStateChange,
            )
        }

        // 🔍 Search
        composable("search") {
            SearchScreen(
                navController = navController,
                viewModel = SearchViewModel,
                onTopBarStateChange = onTopBarStateChange,
                favoritesViewModel = favoritesViewModel
            )
        }

        // 🎥 Movie/TV Details
        composable(
            route = "detail/{mediaType}/{id}",
            arguments = listOf(
                navArgument("mediaType") { type = NavType.StringType },
                navArgument("id") { type = NavType.IntType }
            )
        ) {backStackEntry ->
            val mediaType = backStackEntry.arguments?.getString("mediaType") ?: "movie"
            val id = backStackEntry.arguments?.getInt("id") ?: 0
            val isTvShow = mediaType == "tv"
            MovieDetailsScreen(
                id = id,
                isTvShow = isTvShow,
                navController = navController,
                repository = movieDetailsRepository,
                onTopBarStateChange = onTopBarStateChange,
                favoritesViewModel = favoritesViewModel
            )
        }

        // 👤 Profile Screen
        composable("profile") {
            ProfileScreen(
                navController = navController,
                favoritesViewModel = favoritesViewModel,
                authViewModel = authViewModel,
                onTopBarStateChange = onTopBarStateChange
            )
        }

        // 🔐 Authentication Screens
        composable("login") {
            if (authViewModel != null) {
                LoginScreen(
                    navController = navController,
                    viewModel = authViewModel
                )
            }
        }

        composable("signup") {
            SignupScreen(
                navController = navController
            )
        }

        composable("account") {
            if (authViewModel != null) {
                AccountScreen(
                    navController = navController,
                    viewModel = authViewModel,
                    onTopBarStateChange = onTopBarStateChange
                )
            }
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
