package com.example.mda

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.core.content.edit
import androidx.lifecycle.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import com.example.mda.data.datastore.IntroDataStore
import com.example.mda.data.datastore.introDataStore
import com.example.mda.data.local.LocalRepository
import com.example.mda.data.local.database.AppDatabase
import com.example.mda.data.remote.RetrofitInstance
import com.example.mda.data.repository.*
import com.example.mda.ui.navigation.*
import com.example.mda.ui.screens.actors.ActorViewModel
import com.example.mda.ui.screens.favorites.FavoritesViewModel
import com.example.mda.ui.screens.favorites.FavoritesViewModelFactory
import com.example.mda.ui.screens.genreScreen.GenreViewModel
import com.example.mda.ui.screens.home.HomeViewModel
import com.example.mda.ui.screens.home.HomeViewModelFactory
import com.example.mda.ui.screens.onboarding.OnboardingScreen
import com.example.mda.ui.screens.search.SearchViewModel
import com.example.mda.ui.theme.AppBackgroundGradient
import com.example.mda.ui.theme.AppTopBarColors
import com.example.mda.ui.theme.MovieAppTheme
import com.example.mda.util.GenreViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
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

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // ======= Database & Repository Setup =======
        database = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "mda_db"
        ).fallbackToDestructiveMigration().build()

        localRepository = LocalRepository(database.mediaDao(), searchHistoryDao =database.searchHistoryDao() )
        moviesRepository = MoviesRepository(RetrofitInstance.api, localRepository)
        movieDetailsRepository = MovieDetailsRepository(RetrofitInstance.api, database.mediaDao())
        actorRepository = ActorsRepository(RetrofitInstance.api, database.actorDao())
        actorViewModel = ActorViewModel(actorRepository)
        favoritesRepository = FavoritesRepository(localRepository)

        // ======= Auth setup =======
        val sessionManager =
            com.example.mda.data.datastore.SessionManager(applicationContext)
        val authRepository =
            com.example.mda.data.repository.AuthRepository(RetrofitInstance.api, sessionManager)
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

            val context = this
            val introDataStore = remember { IntroDataStore(context) }

// نستخدم rememberCoroutineScope علشان نحط override مؤقت
            val scope = rememberCoroutineScope()
            var forceShowIntro by remember { mutableStateOf(false) }

// ✅ بنخلي هو اللي يطبع كل حاجة في اللوج
            val isIntroShownFlow = introDataStore.isIntroShown
            val isIntroShown by isIntroShownFlow.collectAsState(initial = null)

// اطبع القيمة الفعلية
            LaunchedEffect(isIntroShown) {
                println("🔥🔥 IntroDataStore value = $isIntroShown")
            }

            val navController = rememberNavController()

            MovieAppTheme(darkTheme = darkTheme) {

                // 🌈 خلفية التدرّج العامة لكل الشاشات
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(AppBackgroundGradient(darkTheme))
                ) {
                    when (isIntroShown) {

                        null -> {
                            // ⏳ شاشة انتظار أثناء التحميل من DataStore
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        }

                        false -> {
                            // ✳️ أول مرة — شاشة الإنترو
                            OnboardingScreen(navController)
                        }

                        true -> {
                            // ✳️ التطبيق الرئيسي بعد شاشة الإنترو
                            val mediaDao = remember { database.mediaDao() }

                            val homeViewModel: HomeViewModel =
                                viewModel(factory = HomeViewModelFactory(moviesRepository,authRepository))
                            val genreViewModel: GenreViewModel =
                                viewModel(factory = GenreViewModelFactory(moviesRepository))
                            val searchVM: SearchViewModel =
                                viewModel(factory = searchViewModelFactory)
                            searchViewModel = searchVM

                            val favoritesVM: FavoritesViewModel =
                                viewModel(factory = FavoritesViewModelFactory(favoritesRepository))
                            favoritesViewModel = favoritesVM

                            var topBarState by remember { mutableStateOf(TopBarState()) }

                            Scaffold(
                                contentWindowInsets = WindowInsets(0),
                                containerColor = Color.Transparent, // ✅ حتى يظهر التدرّج
                                topBar = {
                                    val  barColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f)
                                    val barOverlayColor = barColor.copy(alpha = 0.6f)
                                    val currentRoute =
                                        navController.currentBackStackEntryAsState().value?.destination?.route
                                    val hideTopBarRoutes =
                                        listOf("ActorDetails/{personId}", "onboarding")

                                    if (currentRoute !in hideTopBarRoutes) {
                                        val (topBarBg, topBarText) = AppTopBarColors(darkTheme = darkTheme)
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
                                                Text(titleToShow, color = topBarText)

                                            },
                                            colors = TopAppBarDefaults.topAppBarColors(
                                                containerColor = Color.Transparent // ✅ هنا نخلي اللون اللي في الـ Pair
                                            ),
//                                            modifier = Modifier.background(AppTopBarColors(darkTheme = darkTheme)),
                                            navigationIcon = { topBarState.navigationIcon?.invoke() },
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
                                                        contentDescription = "Toggle Theme",
                                                        tint = topBarText
                                                    )
                                                }
                                            }
                                        )
                                    }
                                },
                                bottomBar = {
                                    val currentRoute =
                                        navController.currentBackStackEntryAsState().value?.destination?.route
                                    val hideBottomBarRoutes = listOf(
                                        "ActorDetails/{personId}",
                                        "detail/{mediaType}/{id}",
                                        "onboarding"
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
                                            barColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
                                            circleColor = MaterialTheme.colorScheme.background,
                                            selectedColor = MaterialTheme.colorScheme.primary,
                                            unselectedColor = MaterialTheme.colorScheme.onSurface.copy(
                                                alpha = 0.55f
                                            )
                                        )
                                    }
                                }
                            ) { innerPadding ->
                                val adjustedPadding = PaddingValues(
                                    top = innerPadding.calculateTopPadding(),
                                    bottom = 0.dp,
                                    start = innerPadding.calculateStartPadding(LayoutDirection.Ltr),
                                    end = innerPadding.calculateEndPadding(LayoutDirection.Ltr)
                                )

                                Box(
                                    modifier = Modifier
                                        .padding(adjustedPadding)
                                        .background(Color.Transparent) // ✅ خليه شفاف عشان التدرّج يبان
                                ) {
                                    MdaNavHost(
                                        navController = navController,
                                        moviesRepository = moviesRepository,
                                        actorsRepository = actorRepository,
                                        movieDetailsRepository = movieDetailsRepository,
                                        localDao = database.mediaDao(),
                                        onTopBarStateChange = { newState -> topBarState = newState },
                                        GenreViewModel = genreViewModel,
                                        SearchViewModel = searchViewModel,
                                        actorViewModel = actorViewModel,
                                        favoritesViewModel = favoritesViewModel,
                                        authViewModel = authViewModel,
                                        authRepository = authRepository
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}