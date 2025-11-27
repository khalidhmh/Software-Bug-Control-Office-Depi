package com.example.mda.ui.kids

import android.util.LayoutDirection
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.mda.ui.navigation.AnimatedNavigationBar
import com.example.mda.ui.navigation.ButtonData
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.datastore.dataStore
import com.example.mda.data.SettingsDataStore
import com.example.mda.data.repository.MoviesRepository
import com.example.mda.ui.screens.favorites.FavoritesViewModel
import com.example.mda.ui.theme.AppBackgroundGradient
import com.example.mda.ui.theme.AppTopBarColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KidsRoot(
    parentNavController: NavHostController,
    moviesRepository: MoviesRepository,
    favoritesViewModel: FavoritesViewModel,
    localRepository: com.example.mda.data.local.LocalRepository,
) {
    val kidsNavController = rememberNavController()
    val backStackEntry = kidsNavController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry.value?.destination?.route
    val showBars = currentRoute != KidsScreens.Splash.route
    val isSection = currentRoute?.startsWith("kids_section") == true
    val currentArgs = backStackEntry.value?.arguments
    val sectionArg = currentArgs?.getString("category")
    val context = LocalContext.current
    val settingsDataStore = remember { SettingsDataStore(context) }
    val themeMode by settingsDataStore.themeModeFlow.collectAsState(initial = 0)
    val darkTheme = when (themeMode) {
        2 -> true
        1 -> false
        else -> isSystemInDarkTheme()
    }
    val sectionTitle = when (sectionArg) {
        "cartoons" -> "New Cartoons"
        "family" -> "Family Movies"
        "anime" -> "Anime Movies"
        else -> "Section"
    }
    val topTitle = if (isSection) "Kids - $sectionTitle" else "Kids Mode"

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Scaffold(
                contentWindowInsets = WindowInsets(0, 0, 0, 0),
                containerColor = Color.Transparent,
                topBar = {
                if (showBars) {
                    TopAppBar(
                        title = {
                            Text(
                                topTitle,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = Color.Transparent,
                            titleContentColor = MaterialTheme.colorScheme.onBackground,
                            navigationIconContentColor = MaterialTheme.colorScheme.onBackground,
                            actionIconContentColor = MaterialTheme.colorScheme.onBackground
                        ),
                        navigationIcon = {
                            val canNavigateBack = kidsNavController.previousBackStackEntry != null
                            if (isSection && canNavigateBack) {
                                IconButton(onClick = { kidsNavController.popBackStack() }) {
                                    Icon(
                                        Icons.Default.ArrowBack,
                                        contentDescription = "Back",
                                        tint = MaterialTheme.colorScheme.onBackground
                                    )
                                }
                            }
                        },
                        actions = {
                            IconButton(onClick = {
                                parentNavController.popBackStack()
                            }) {
                                Icon(
                                    Icons.Default.ExitToApp,
                                    contentDescription = "Exit Kids Mode",
                                    tint = MaterialTheme.colorScheme.onBackground
                                )
                            }
                        }
                    )
                }
            },
            bottomBar = {
                if (showBars && currentRoute != KidsScreens.Splash.route) {
                    val (topBarBg) = AppTopBarColors(darkTheme)
                    AnimatedNavigationBar(
                        navController = kidsNavController,
                        buttons = listOf(
                            ButtonData(KidsScreens.Home.route, "Home", Icons.Default.Home),
                            ButtonData(KidsScreens.Search.route, "Search", Icons.Default.Search),
                            ButtonData(KidsScreens.Favorites.route, "Favorites", Icons.Default.Favorite),
                        ),
                        barColor = topBarBg,
                        circleColor = MaterialTheme.colorScheme.background,
                        selectedColor = MaterialTheme.colorScheme.primary,
                        unselectedColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }

        ) { innerPadding ->
            Box(
                modifier = Modifier.padding(
                    top = innerPadding.calculateTopPadding())
                .navigationBarsPadding())

            {
                KidsNavGraph(
                    parentNavController = parentNavController,
                    navController = kidsNavController,
                    moviesRepository = moviesRepository,
                    favoritesViewModel = favoritesViewModel,
                    localRepository = localRepository
                )
            }
        }
    }
}

@Composable
fun KidsNavGraph(
    parentNavController: NavHostController,
    navController: NavHostController,
    moviesRepository: MoviesRepository,
    favoritesViewModel: FavoritesViewModel,
    localRepository: com.example.mda.data.local.LocalRepository,
) {
    NavHost(navController = navController, startDestination = KidsScreens.Splash.route) {
        composable(KidsScreens.Splash.route) {
            KidsSplashScreen(
                onFinished = {
                    navController.navigate(KidsScreens.Home.route) {
                        popUpTo(KidsScreens.Splash.route) { inclusive = true }
                    }
                }
            )
        }
        composable(KidsScreens.Home.route) {
            KidsHomeScreen(
                moviesRepository = moviesRepository,
                favoritesViewModel = favoritesViewModel,
                onItemClick = { media ->
                    val type = media.resolvedMediaType
                    parentNavController.navigate("detail/$type/${media.id}")
                },
                onOpenSection = { category ->
                    navController.navigate("kids_section/$category")
                }
            )
        }
        composable(KidsScreens.Section.route) { backStackEntry ->
            val category = backStackEntry.arguments?.getString("category") ?: ""
            KidsSectionScreen(
                category = category,
                moviesRepository = moviesRepository,
                onItemClick = { media ->
                    val type = media.resolvedMediaType
                    parentNavController.navigate("detail/$type/${media.id}")
                }
            )
        }
        composable(KidsScreens.Search.route) {
            KidsSearchScreen(
                moviesRepository = moviesRepository,
                favoritesViewModel = favoritesViewModel,
                onItemClick = { media ->
                    val type = media.resolvedMediaType
                    parentNavController.navigate("detail/$type/${media.id}")
                }
            )
        }
        composable(KidsScreens.Favorites.route) {
            KidsFavoritesScreen(
                localRepository = localRepository,
                onItemClick = { media ->
                    val type = media.resolvedMediaType
                    parentNavController.navigate("detail/$type/${media.id}")
                }
            )
        }
    }
}