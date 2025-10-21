package com.example.mda

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.core.content.edit
import androidx.lifecycle.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import com.example.mda.data.local.LocalRepository
import com.example.mda.data.local.database.AppDatabase
import com.example.mda.data.remote.RetrofitInstance
import com.example.mda.data.repository.*
import com.example.mda.ui.navigation.*
import com.example.mda.ui.screens.actors.ActorViewModel
import com.example.mda.ui.screens.actors.ViewType
import com.example.mda.ui.screens.genre.GenreViewModel
import com.example.mda.ui.screens.home.HomeViewModel
import com.example.mda.ui.screens.home.HomeViewModelFactory
import com.example.mda.ui.screens.search.SearchViewModel
import com.example.mda.ui.theme.MovieAppTheme
import com.example.mda.util.GenreViewModelFactory

class MainActivity : ComponentActivity() {

    private lateinit var database: AppDatabase
    private lateinit var localRepository: LocalRepository
    private lateinit var moviesRepository: MoviesRepository
    private lateinit var movieDetailsRepository: MovieDetailsRepository
    private lateinit var actorRepository: ActorsRepository

    private lateinit var searchViewModel: SearchViewModel
    private lateinit var actorViewModel: ActorViewModel

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

        val prefs = getSharedPreferences("theme_prefs", MODE_PRIVATE)
        val savedTheme = prefs.getBoolean("dark_mode", true)

        setContent {
            var darkTheme by remember { mutableStateOf(savedTheme) }

            MovieAppTheme(darkTheme = darkTheme) {

                val navController = rememberNavController()
                val mediaDao = remember { database.mediaDao() }

                val homeViewModel: HomeViewModel = viewModel(
                    factory = HomeViewModelFactory(moviesRepository)
                )
                val genreViewModel: GenreViewModel = viewModel(
                    factory = GenreViewModelFactory(moviesRepository)
                )
                val searchVM: SearchViewModel = viewModel(factory = searchViewModelFactory)
                searchViewModel = searchVM

                Scaffold(
                    contentWindowInsets = WindowInsets(0),
                    topBar = {
                        val currentRoute =
                            navController.currentBackStackEntryAsState().value?.destination?.route
                        TopAppBar(
                            modifier = Modifier
                                .height(100.dp)
                                .windowInsetsPadding(WindowInsets(0)), // ← السطر المهم
                            title = {},
                            actions = {
                                when (currentRoute) {
                                    "home" -> {
                                        IconButton(onClick = {
                                            navController.navigate("search")
                                        }) {
                                            Icon(
                                                Icons.Default.Search,
                                                contentDescription = "Search"
                                            )
                                        }
                                    }

                                    "actors" -> {
                                        val viewType by actorViewModel.viewType.collectAsState()
                                        IconButton(onClick = { actorViewModel.toggleViewType() }) {
                                            Icon(
                                                imageVector = if (viewType == ViewType.GRID)
                                                    Icons.Default.List
                                                else Icons.Default.GridView,
                                                contentDescription = "Toggle View"
                                            )
                                        }
                                    }

                                    "movies" -> {
                                        IconButton(onClick = { /* refresh */ }) {
                                            Icon(
                                                Icons.Default.Refresh,
                                                contentDescription = "Refresh"
                                            )
                                        }
                                    }

                                    else -> {}
                                }

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
                            },
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = Color.Unspecified
                            )
                        )
                    },
                    // ================== تم التعديل هنا ==================
                    // أزلنا الـ Box الإضافي لتبسيط التركيب
                    bottomBar = {
                        val buttons = listOf(
                            ButtonData("home", "Home", Icons.Default.Home),
                            ButtonData("movies", "Movies", Icons.Default.Movie),
                            ButtonData("actors", "People", Icons.Default.People),
                            ButtonData("search", "Search", Icons.Default.Search)
                        )

                        AnimatedNavigationBar(
                            navController = navController,
                            buttons = buttons,
                            barColor = MaterialTheme.colorScheme.surface,
                            circleColor = MaterialTheme.colorScheme.background,
                            selectedColor = MaterialTheme.colorScheme.primary,
                            unselectedColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.55f)
                        )
                    },
                    // =========================================================
                ) { innerPadding ->
                    Box(modifier = Modifier.padding(bottom = 0.dp)){
                        MdaNavHost(
                            navController = navController,
                            homeViewModel = homeViewModel,
                            moviesRepository = moviesRepository,
                            localDao = mediaDao,
                            actorRepository = actorRepository,
                            movieDetailsRepository = movieDetailsRepository,
                            GenreViewModel = genreViewModel,
                            SearchViewModel = searchViewModel,
                            actorViewModel = actorViewModel
                        )
                    }
                }
            }
        }
    }
}