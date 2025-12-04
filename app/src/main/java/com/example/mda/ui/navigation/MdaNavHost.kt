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
import com.example.mda.data.local.LocalRepository
import com.example.mda.data.repository.*
import com.example.mda.ui.Settings.AboutScreen
import com.example.mda.ui.Settings.HelpScreen
import com.example.mda.ui.Settings.LanguageSettingsScreen
import com.example.mda.ui.home.HomeScreen
import com.example.mda.ui.screens.actordetails.ActorDetailsScreen
import com.example.mda.ui.screens.actors.ActorsScreen
import com.example.mda.ui.screens.actors.ActorViewModel
import com.example.mda.ui.screens.auth.*
import com.example.mda.ui.screens.favorites.FavoritesViewModel
import com.example.mda.ui.screens.genreScreen.GenreScreen
import com.example.mda.ui.screens.genreScreen.GenreViewModel
import com.example.mda.ui.screens.home.HomeViewModel
import com.example.mda.ui.screens.genreDetails.GenreDetailsScreen
import com.example.mda.ui.screens.movieDetail.MovieDetailsScreen
import com.example.mda.ui.screens.profile.ProfileScreen
import com.example.mda.ui.screens.profile.favourites.FavoritesScreen
import com.example.mda.ui.screens.profile.history.HistoryScreen
import com.example.mda.ui.screens.profile.history.HistoryViewModel
import com.example.mda.ui.screens.profile.history.MoviesHistoryScreen
import com.example.mda.ui.screens.profile.history.MoviesHistoryViewModel
import com.example.mda.ui.screens.search.SearchScreen
import com.example.mda.ui.screens.search.SearchViewModel
import com.example.mda.ui.screens.settings.SettingsScreen
import com.example.mda.ui.screens.onboarding.OnboardingScreen
import com.example.mda.ui.screens.splash.SplashScreen
import com.example.mda.ui.kids.KidsRoot
import com.example.mda.ui.kids.KidsSplashScreen
import com.example.mda.ui.screens.home.homeScreen.PopularNowScreen
import com.example.mda.ui.screens.settings.DeveloperToolsScreen
import com.example.mda.ui.screens.settings.PrivacyPolicyScreen

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MdaNavHost(
    navController: NavHostController,
    moviesRepository: MoviesRepository,
    actorsRepository: ActorsRepository,
    movieDetailsRepository: MovieDetailsRepository,
    localDao: MediaDao,
    localRepository: LocalRepository,
    onTopBarStateChange: (TopBarState) -> Unit,
    genreViewModel: GenreViewModel,
    searchViewModel: SearchViewModel,
    actorViewModel: ActorViewModel,
    favoritesViewModel: FavoritesViewModel,
    authViewModel: AuthViewModel,
    authRepository: AuthRepository,
    historyViewModel: HistoryViewModel,
    moviesHistoryViewModel: MoviesHistoryViewModel,
    darkTheme: Boolean,
    homeViewModel: HomeViewModel // ✅ نستقبل الـ VM الجاهز هنا
) {
    NavHost(
        navController = navController,
        startDestination = "splash"
    ) {
        // Splash
        composable("splash") {
            SplashScreen(navController = navController)
        }

        // Password Settings & Kids PIN
        composable("password_settings") {
            com.example.mda.ui.screens.settings.password.PasswordSettingsScreen(
                navController = navController,
                onTopBarStateChange = onTopBarStateChange
            )
        }
        composable("set_kids_pin") {
            com.example.mda.ui.screens.settings.password.SetKidsPinScreen(
                navController = navController,
                onTopBarStateChange = onTopBarStateChange
            )
        }
        composable("security_questions_setup") {
            com.example.mda.ui.screens.settings.password.SecurityQuestionsSetupScreen(
                navController = navController,
                onTopBarStateChange = onTopBarStateChange
            )
        }
        composable("security_questions_verify") {
            com.example.mda.ui.screens.settings.password.SecurityQuestionsVerifyScreen(
                navController = navController,
                onTopBarStateChange = onTopBarStateChange
            )
        }
        composable("change_kids_pin") {
            com.example.mda.ui.screens.settings.password.ChangeKidsPinScreen(
                navController = navController,
                onTopBarStateChange = onTopBarStateChange
            )
        }

        // Onboarding
        composable("onboarding") {
            OnboardingScreen(navController = navController)
        }

        // Home
        composable("home") {
            HomeScreen(
                viewModel = homeViewModel,   
                navController = navController,
                onTopBarStateChange = onTopBarStateChange,
                favoritesViewModel = favoritesViewModel,
                authViewModel = authViewModel
            )
        }

        // Actors List
        composable("actors") {
            ActorsScreen(
                navController = navController,
                actorsRepository = actorsRepository,
                viewModel = actorViewModel, 
                onTopBarStateChange = onTopBarStateChange
            )
        }

        // Search
        composable("search") {
            SearchScreen(
                navController = navController,
                viewModel = searchViewModel,
                onTopBarStateChange = onTopBarStateChange,
                favoritesViewModel = favoritesViewModel,
                authViewModel = authViewModel
            )
        }

        // Movies / Genres
        composable("movies") {
            GenreScreen(
                navController = navController,
                viewModel = genreViewModel,
                onTopBarStateChange = onTopBarStateChange
            )
        }

        // Actor Details
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
                favoritesViewModel = favoritesViewModel,
                historyViewModel = historyViewModel,
                authViewModel = authViewModel
            )
        }

        // Genre Details
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
                favoritesViewModel = favoritesViewModel,
                authViewModel = authViewModel
            )
        }

        // Movie / TV Details
        composable(
            route = "detail/{mediaType}/{id}",
            arguments = listOf(
                navArgument("mediaType") { type = NavType.StringType },
                navArgument("id") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val mediaType = backStackEntry.arguments?.getString("mediaType") ?: "movie"
            val id = backStackEntry.arguments?.getInt("id") ?: 0
            val isTvShow = mediaType == "tv"
            MovieDetailsScreen(
                id = id,
                isTvShow = isTvShow,
                navController = navController,
                repository = movieDetailsRepository,
                onTopBarStateChange = onTopBarStateChange,
                favoritesViewModel = favoritesViewModel,
                moviehistoryViewModel = moviesHistoryViewModel,
                authViewModel = authViewModel
            )
        }

        // Profile
        composable("profile") {
            ProfileScreen(
                navController = navController,
                favoritesViewModel = favoritesViewModel,
                authViewModel = authViewModel,
                moviesHistoryViewModel = moviesHistoryViewModel,
                historyviewModel = historyViewModel,
                onTopBarStateChange = onTopBarStateChange
            )
        }

        // Favorites
        composable("Favprofile") {
            FavoritesScreen(
                navController = navController,
                favoritesViewModel = favoritesViewModel,
                onTopBarStateChange = onTopBarStateChange,
                authViewModel = authViewModel
            )
        }

        // History
        composable("HistoryScreen") {
            HistoryScreen(
                navController = navController,
                viewModel = historyViewModel,
                onTopBarStateChange = onTopBarStateChange
            )
        }

        // Movies History
        composable("MovieHistoryScreen") {
            MoviesHistoryScreen(
                navController = navController,
                moviesHistoryViewModel = moviesHistoryViewModel,
                onTopBarStateChange = onTopBarStateChange
            )
        }

        // Authentication
        composable("login") {
            LoginScreen(
                navController = navController,
                viewModel = authViewModel,
                darkTheme = darkTheme,
            )
        }

        composable("signup") {
            SignupScreen(
                navController = navController,
                darkTheme = darkTheme,
            )
        }

        composable("account") {
            AccountScreen(
                navController = navController,
                viewModel = authViewModel,
            )
        }

        // Kids Mode
        composable("kids") {
            KidsRoot(
                parentNavController = navController,
                moviesRepository = moviesRepository,
                favoritesViewModel = favoritesViewModel,
                localRepository = localRepository
            )
        }
        // Kids Splash Screen
        composable("kids_splash") {
            KidsSplashScreen(
                onFinished = {
                    navController.navigate("kids") {
                        popUpTo("kids_splash") { inclusive = true }
                    }
                }
            )
        }
        composable("developer_tools") {
            DeveloperToolsScreen(navController, onTopBarStateChange)
        }

        // Settings
        composable("settings") {
            SettingsScreen(
                navController = navController,
                onTopBarStateChange = onTopBarStateChange,
                authViewModel = authViewModel,
                FavoritesViewModel = favoritesViewModel
            )
        }
        // Language Settings
        composable("language_settings") {
            LanguageSettingsScreen(
                navController = navController,
                onTopBarStateChange = onTopBarStateChange
            )
        }
        composable("about_app") {
            AboutScreen(
                navController = navController,
                onTopBarStateChange = onTopBarStateChange
            )
        }
        composable("help_faq") {
            HelpScreen(navController = navController,
                onTopBarStateChange = onTopBarStateChange
            )
        }
        composable("privacy_policy") {
            PrivacyPolicyScreen(navController = navController,
                onTopBarStateChange = onTopBarStateChange
            )
        }

        // Popular Movies
        composable("popular_movies") {
            // 🔥 تحسين هام: نستخدم نفس الـ ViewModel الممرر من الأعلى بدلاً من إنشاء واحد جديد
            // هذا يمنع إعادة تحميل البيانات عند فتح هذه الشاشة

            PopularNowScreen(
                navController = navController,
                homeViewModel = homeViewModel, // ✅ استخدام النسخة المشتركة
                favoritesViewModel = favoritesViewModel ,
                authViewModel = authViewModel,
                onTopBarStateChange = onTopBarStateChange
            )
        }
    }
}