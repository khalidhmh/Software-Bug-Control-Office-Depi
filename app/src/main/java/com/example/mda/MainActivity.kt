package com.example.mda

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.mda.data.local.LocalRepository
import com.example.mda.data.local.database.AppDatabase
import com.example.mda.data.remote.RetrofitInstance
import com.example.mda.data.repository.ActorsRepository
import com.example.mda.data.repository.MovieDetailsRepository
import com.example.mda.data.repository.MoviesRepository
import com.example.mda.ui.navigation.AnimatedNavigationBar
import com.example.mda.ui.navigation.ButtonData
import com.example.mda.ui.navigation.MdaNavHost
import com.example.mda.ui.screens.home.HomeViewModel
import com.example.mda.ui.screens.home.HomeViewModelFactory
import com.example.mda.ui.screens.genre.GenreViewModel
import com.example.mda.ui.screens.search.SearchViewModel
import com.example.mda.ui.theme.MovieAppTheme
import androidx.core.content.edit
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.room.Room
import com.example.mda.util.GenreViewModelFactory
import com.example.mda.ui.navigation.TopBarState // ✅ استيراد الكلاس الجديد


class MainActivity : ComponentActivity() {

    // ======= Database & Repositories =======
    private lateinit var database: AppDatabase
    private lateinit var localRepository: LocalRepository
    private lateinit var moviesRepository: MoviesRepository
    private lateinit var movieDetailsRepository: MovieDetailsRepository
    private lateinit var actorRepository: ActorsRepository
    lateinit var searchViewModel: SearchViewModel


    @OptIn(ExperimentalMaterial3Api::class)
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // ======= Initialize Database & Repositories =======
        database = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "mda_db"
        )
            .fallbackToDestructiveMigrationOnDowngrade()
            .build()

        localRepository = LocalRepository(database.mediaDao())
        moviesRepository = MoviesRepository(RetrofitInstance.api, localRepository)
        movieDetailsRepository = MovieDetailsRepository(RetrofitInstance.api, database.mediaDao())
        actorRepository = ActorsRepository(RetrofitInstance.api, database.actorDao())
        searchViewModel = SearchViewModel(moviesRepository, localDao = database.mediaDao())

// ======= Theme Preferences =======
        val prefs = getSharedPreferences("theme_prefs", MODE_PRIVATE)
        val savedTheme = prefs.getBoolean("dark_mode", true)

        setContent {
            var darkTheme by remember { mutableStateOf(savedTheme) }

            MovieAppTheme(darkTheme = darkTheme) {
                val mediaDao = remember { database.mediaDao() }

                // ======= Initialize ViewModels =======
                val homeViewModel: HomeViewModel = viewModel(
                    factory = HomeViewModelFactory(moviesRepository)
                )

                val genreViewModel: GenreViewModel = viewModel(
                    factory = GenreViewModelFactory(moviesRepository)
                )

                val navController = rememberNavController()

                var topBarState by remember { mutableStateOf(TopBarState()) }
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                Scaffold(
                    topBar = {
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
                            actions = {
                                topBarState.actions(this)
                                IconButton(onClick = {
                                    darkTheme = !darkTheme
                                    prefs.edit { putBoolean("dark_mode", darkTheme) }
                                }) {
                                    Icon(
                                        imageVector = if (darkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
                                        contentDescription = "Toggle Theme"
                                    )
                                }
                            }
                        )

                    },
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
                            unselectedColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                ) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        MdaNavHost(
                            navController = navController,
                            moviesRepository = moviesRepository,
                            actorsRepository = actorRepository,
                            movieDetailsRepository = movieDetailsRepository,
                            localDao = mediaDao,
                            onTopBarStateChange = { newState ->
                                topBarState = newState
                            }
                        )
                    }
                }
            }
        }
    }
}