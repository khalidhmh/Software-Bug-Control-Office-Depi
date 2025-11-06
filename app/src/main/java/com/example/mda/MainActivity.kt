package com.example.mda

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.core.content.edit
import androidx.lifecycle.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.mda.data.local.LocalRepository
import com.example.mda.data.local.database.AppDatabase
import com.example.mda.data.remote.RetrofitInstance
import com.example.mda.data.repository.*
import com.example.mda.ui.navigation.*
import com.example.mda.ui.screens.actors.ActorViewModel
import com.example.mda.ui.screens.genreScreen.GenreViewModel
import com.example.mda.ui.screens.home.HomeViewModel
import com.example.mda.ui.screens.home.HomeViewModelFactory
import com.example.mda.ui.screens.search.SearchViewModel
import com.example.mda.ui.theme.MovieAppTheme
import androidx.room.Room
import com.example.mda.util.GenreViewModelFactory
import com.example.mda.ui.navigation.TopBarState // ✅ استيراد الكلاس الجديد
import com.example.mda.data.repository.FavoritesRepository
import com.example.mda.ui.screens.favorites.FavoritesViewModel
import com.example.mda.ui.screens.favorites.FavoritesViewModelFactory


class MainActivity : ComponentActivity() {

    private lateinit var database: AppDatabase
    private lateinit var localRepository: LocalRepository
    private lateinit var moviesRepository: MoviesRepository
    private lateinit var movieDetailsRepository: MovieDetailsRepository
    private lateinit var actorRepository: ActorsRepository
    private lateinit var favoritesRepository: FavoritesRepository

    private lateinit var searchViewModel: SearchViewModel
    private lateinit var actorViewModel: ActorViewModel
    private lateinit var favoritesViewModel: FavoritesViewModel
    private lateinit var authViewModel: com.example.mda.ui.screens.auth.AuthViewModel


    @OptIn(ExperimentalMaterial3Api::class)
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // ======= Database & Repo setup =======
        database = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "mda_db"
        ).fallbackToDestructiveMigration().build()

        localRepository = LocalRepository(database.mediaDao())
        moviesRepository = MoviesRepository(RetrofitInstance.api, localRepository)
        movieDetailsRepository = MovieDetailsRepository(RetrofitInstance.api, database.mediaDao())
        actorRepository = ActorsRepository(RetrofitInstance.api, database.actorDao())
        actorViewModel = ActorViewModel(actorRepository)
        favoritesRepository = FavoritesRepository(localRepository)

        // ======= Auth setup =======
        val sessionManager = com.example.mda.data.datastore.SessionManager(applicationContext)
        val authRepository = com.example.mda.data.repository.AuthRepository(RetrofitInstance.api, sessionManager)
        authViewModel = com.example.mda.ui.screens.auth.AuthViewModel(authRepository)

        val searchViewModelFactory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val savedStateHandle = SavedStateHandle()
                @Suppress("UNCHECKED_CAST")
                return SearchViewModel(
                    repository = moviesRepository,
                    historyDao = database.searchHistoryDao(),
                    savedStateHandle = savedStateHandle
                ) as T
            }
        }

// ======= Theme Preferences =======
        val prefs = getSharedPreferences("theme_prefs", MODE_PRIVATE)
        val savedTheme = prefs.getBoolean("dark_mode", true)

        setContent {
            var darkTheme by remember { mutableStateOf(savedTheme) }

            MovieAppTheme(darkTheme = darkTheme) {

                val mediaDao = remember { database.mediaDao() }

                val homeViewModel: HomeViewModel = viewModel(
                    factory = HomeViewModelFactory(moviesRepository)
                )
                val genreViewModel: GenreViewModel = viewModel(
                    factory = GenreViewModelFactory(moviesRepository)
                )
                val searchVM: SearchViewModel = viewModel(factory = searchViewModelFactory)
                searchViewModel = searchVM

                val favoritesVM: FavoritesViewModel = viewModel(
                    factory = FavoritesViewModelFactory(favoritesRepository)
                )
                favoritesViewModel = favoritesVM

                val navController = rememberNavController()

                var topBarState by remember { mutableStateOf(TopBarState()) }
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                Scaffold(
                    contentWindowInsets = WindowInsets(0),
                    topBar = {
                        val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
                        val hideTopBarRoutes = listOf(
                            "ActorDetails/{personId}",
                        )

                        if (currentRoute !in hideTopBarRoutes) {
                            TopAppBar(
                                title = {
                                    val titleToShow = if (topBarState.title.isNotEmpty()) {
                                        topBarState.title
                                    } else {
                                        when (currentRoute) {
                                            "home" -> "Home"
                                            "movies" -> "Movies"
                                            "actors" -> "People"
                                            "search" -> "Search"
                                            else -> ""
                                        }
                                    }
                                    Text(titleToShow)
                                },
                                navigationIcon = {
                                    topBarState.navigationIcon?.invoke()
                                },
                                actions = {
                                    topBarState.actions(this)
                                    IconButton(onClick = {
                                        darkTheme = !darkTheme
                                        prefs.edit { putBoolean("dark_mode", darkTheme) }
                                    }) {
                                        Icon(
                                            imageVector = if (darkTheme)
                                                Icons.Default.LightMode
                                            else Icons.Default.DarkMode,
                                            contentDescription = "Toggle Theme"
                                        )
                                    }
                                }
                            )
                        }
                    },

                    // ================== تم التعديل هنا ==================
                    // أزلنا الـ Box الإضافي لتبسيط التركيب
                    bottomBar = {
                        val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
                        val hideBottomBarRoutes = listOf(
                            "ActorDetails/{personId}",
                            "detail/{mediaType}/{id}",
                        )

                        if (currentRoute !in hideBottomBarRoutes) {
                            val buttons = listOf(
                                ButtonData("home", "Home", Icons.Default.Home),
                                ButtonData("movies", "Movies", Icons.Default.Movie),
                                ButtonData("actors", "People", Icons.Default.People),
                                ButtonData("search", "Search", Icons.Default.Search),
                                ButtonData("profile", "Profile", Icons.Default.Person)
                            )

                            AnimatedNavigationBar(
                                navController = navController,
                                buttons = buttons,
                                barColor = MaterialTheme.colorScheme.surface,
                                circleColor = MaterialTheme.colorScheme.background,
                                selectedColor = MaterialTheme.colorScheme.primary,
                                unselectedColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.55f)
                            )
                        }
                    }

                    // =========================================================
                ) { innerPadding ->
                    val adjustedPadding = PaddingValues(
                        top = innerPadding.calculateTopPadding(),
                        bottom = 0.dp, // تجاهل الـ bottom padding
                        start = innerPadding.calculateStartPadding(LayoutDirection.Ltr),
                        end = innerPadding.calculateEndPadding(LayoutDirection.Ltr)
                    )

                    Box(modifier = Modifier.padding(adjustedPadding)) {
                        MdaNavHost(
                            navController = navController,
                            moviesRepository = moviesRepository,
                            actorsRepository = actorRepository,
                            movieDetailsRepository = movieDetailsRepository,

                            localDao = mediaDao,
                            onTopBarStateChange = { newState ->
                                topBarState = newState
                            },
                            GenreViewModel = genreViewModel,
                            SearchViewModel = searchViewModel,
                            actorViewModel = actorViewModel,
                            favoritesViewModel = favoritesViewModel,
                            authViewModel = authViewModel
                        )
                    }
                }

            }
        }
    }
}
