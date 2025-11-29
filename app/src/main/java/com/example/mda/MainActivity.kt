package com.example.mda

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
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
import com.example.mda.data.SettingsDataStore
import com.example.mda.data.datastore.IntroDataStore
import com.example.mda.data.local.LocalRepository
import com.example.mda.data.local.database.AppDatabase
import com.example.mda.data.remote.RetrofitInstance
import com.example.mda.data.repository.*
import com.example.mda.ui.home.getGreetingMessage
import com.example.mda.ui.navigation.*
import com.example.mda.ui.screens.actors.ActorViewModel
import com.example.mda.ui.screens.actors.ActorViewModelFactory
import com.example.mda.ui.screens.favorites.FavoritesViewModel
import com.example.mda.ui.screens.favorites.FavoritesViewModelFactory
import com.example.mda.ui.screens.genreScreen.GenreViewModel
import com.example.mda.ui.screens.home.HomeViewModel
import com.example.mda.ui.screens.home.HomeViewModelFactory
import com.example.mda.ui.screens.onboarding.OnboardingScreen
import com.example.mda.ui.screens.profile.history.HistoryViewModel
import com.example.mda.ui.screens.profile.history.HistoryViewModelFactory
import com.example.mda.ui.screens.profile.history.MoviesHistoryViewModel
import com.example.mda.ui.screens.profile.history.MoviesHistoryViewModelFactory
import com.example.mda.ui.screens.search.SearchViewModel
import com.example.mda.ui.theme.AppBackgroundGradient
import com.example.mda.ui.theme.AppTopBarColors
import com.example.mda.ui.theme.MovieAppTheme
import com.example.mda.util.GenreViewModelFactory
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {

    // ======= Database & Repository =======
    private lateinit var database: AppDatabase
    private lateinit var localRepository: LocalRepository
    private lateinit var moviesRepository: MoviesRepository
    private lateinit var movieDetailsRepository: MovieDetailsRepository
    private lateinit var actorRepository: ActorsRepository
    private lateinit var favoritesRepository: FavoritesRepository

    // ======= ViewModels =======
    private lateinit var searchViewModel: SearchViewModel
    private lateinit var historyViewModel: HistoryViewModel
    private lateinit var moviesHistoryViewModel: MoviesHistoryViewModel
    private lateinit var actorViewModel: ActorViewModel
    private lateinit var favoritesViewModel: FavoritesViewModel
    private lateinit var authViewModel: com.example.mda.ui.screens.auth.AuthViewModel

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // ======= Room Database Setup =======
        database = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "mda_db"
        ).fallbackToDestructiveMigration().build()

        localRepository = LocalRepository(database.mediaDao(), database.searchHistoryDao())
        moviesRepository = MoviesRepository(RetrofitInstance.api, localRepository)
        movieDetailsRepository = MovieDetailsRepository(RetrofitInstance.api, database.mediaDao())
        actorRepository = ActorsRepository(RetrofitInstance.api, database.actorDao())

        // ======= Session & Favorites & Auth =======
        val sessionManager = com.example.mda.data.datastore.SessionManager(applicationContext)

        favoritesRepository = FavoritesRepository(
            localRepo = localRepository,
            api = RetrofitInstance.api,
            sessionManager = sessionManager
        )

        val historyRepository = HistoryRepository(database.historyDao())
        val moviesHistoryRepository = MoviesHistoryRepository(database.MoviehistoryDao())

        val authRepository = AuthRepository(
            RetrofitInstance.api,
            sessionManager
        )
        authViewModel = com.example.mda.ui.screens.auth.AuthViewModel(authRepository)

        // ======= SearchViewModel Factory =======
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
        setContent {
            val dataStore = SettingsDataStore(applicationContext)
            val themeMode by dataStore.themeModeFlow.collectAsState(initial = 0)

            val darkTheme = when (themeMode) {
                2 -> true
                1 -> false
                else -> isSystemInDarkTheme()
            }
            val context = this
            val introDataStore = remember { IntroDataStore(context) }
            val isIntroShownFlow = introDataStore.isIntroShown
            val isIntroShown by isIntroShownFlow.collectAsState(initial = null)
            val scope = rememberCoroutineScope()
            LaunchedEffect(isIntroShown) {
                println("🔥 IntroDataStore value = $isIntroShown")
            }

            val navController = rememberNavController()
            MovieAppTheme(darkTheme = darkTheme) {

                // 🌈 Gradient Background
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(AppBackgroundGradient(darkTheme))
                ) {
                    when (isIntroShown) {
                        null -> {
                            // ⏳ Loading
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(32.dp),
                                contentAlignment = Alignment.Center
                            ) { CircularProgressIndicator() }
                        }

                        false -> {
                            // ✳️ Onboarding Screen
                            OnboardingScreen(navController)
                        }

                        true -> {
                            // ✳️ Main App
                            val mediaDao = remember { database.mediaDao() }

                            // ViewModels
                            val homeViewModel: HomeViewModel =
                                viewModel(factory = HomeViewModelFactory(moviesRepository, authRepository))
                            val genreViewModel: GenreViewModel =
                                viewModel(factory = GenreViewModelFactory(moviesRepository))
                            val searchVM: SearchViewModel = viewModel(factory = searchViewModelFactory)
                            searchViewModel = searchVM

                            val favoritesVM: FavoritesViewModel =
                                viewModel(factory = FavoritesViewModelFactory(favoritesRepository))
                            favoritesViewModel = favoritesVM

                            val historyVM: HistoryViewModel =
                                viewModel(factory = HistoryViewModelFactory(historyRepository))
                            historyViewModel = historyVM

                            val moviesHistoryVM: MoviesHistoryViewModel =
                                viewModel(
                                    factory = MoviesHistoryViewModelFactory(
                                        moviesHistoryRepository
                                    )
                                )
                            moviesHistoryViewModel = moviesHistoryVM

                            val actorvm: ActorViewModel =
                                viewModel(factory = ActorViewModelFactory(actorRepository))

                            actorViewModel = actorvm

                            var topBarState by remember { mutableStateOf(TopBarState()) }

                            Scaffold(
                                contentWindowInsets = WindowInsets(0, 0, 0, 0),
                                containerColor = Color.Transparent,
                                topBar = {
                                    val backStackEntry by navController.currentBackStackEntryAsState()
                                    val currentRoute =
                                        navController.currentBackStackEntryAsState().value?.destination?.route

                                    // ✅ Hide TopAppBar for these routes
                                    val hideTopBarRoutes = listOf(
                                        "splash",
                                        "ActorDetails/{personId}",
                                        "detail/{mediaType}/{id}",
                                        "onboarding",
                                        "login",
                                        "signup",
                                        "account",
                                        "kids"  // ✅ Added kids route
                                    )

                                    if (currentRoute != null && currentRoute !in hideTopBarRoutes) {
                                        val (topBarBg, topBarText) =
                                            AppTopBarColors(darkTheme = darkTheme)
                                        val resetTopBar = currentRoute in listOf("about_app", "help_faq", "privacy_policy")
                                        val titleToShow = if (resetTopBar) {
                                            when (currentRoute) {
                                                "about_app" -> "About"
                                                "help_faq" -> "Help & FAQ"
                                                "privacy_policy" -> "Privacy Policy"
                                                else -> ""
                                            }
                                        } else if (currentRoute == "home") {
                                            getGreetingMessage()
                                        } else if (topBarState.title.isNotEmpty()) {
                                            topBarState.title
                                        } else {
                                            when (currentRoute) {
                                                "movies" -> "Movies"
                                                "actors" -> "People"
                                                "search" -> "Search"
                                                "HistoryScreen" -> "History"
                                                "settings" -> "Settings"
                                                else -> ""
                                            }
                                        }

                                        val subtitleToShow = if (currentRoute == "home") "What do you want to watch?" else topBarState.subtitle
                                        if (currentRoute == "home") {
                                            Surface(
                                                color = topBarBg,
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .statusBarsPadding()
                                            ) {
                                                Column(
                                                    modifier = Modifier
                                                        .padding(horizontal = 16.dp, vertical = 10.dp)
                                                ) {
                                                    Text(
                                                        text = titleToShow, // 🟢 نستخدم القيمة اللي عدلناها فوق
                                                        style = MaterialTheme.typography.headlineSmall.copy(color = topBarText)
                                                    )

                                                    subtitleToShow?.let {
                                                        Text(
                                                            text = it,
                                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                                color = topBarText.copy(alpha = 0.6f)
                                                            ),
                                                            modifier = Modifier.padding(top = 2.dp)
                                                        )
                                                    }
                                                }
                                            }
                                        } else {
                                            // ✳️ باقي الشاشات العادية
                                            TopAppBar(
                                                title = { Text(titleToShow, color = topBarText) },
                                                colors = TopAppBarDefaults.topAppBarColors(
                                                    containerColor = topBarBg,
                                                    titleContentColor = topBarText,
                                                    navigationIconContentColor = topBarText,
                                                    actionIconContentColor = topBarText
                                                ),
                                                navigationIcon = {
                                                    if (topBarState.showBackButton) {
                                                        IconButton(onClick = { navController.navigateUp() }) {
                                                            Icon(
                                                                imageVector = Icons.Default.ArrowBack,
                                                                contentDescription = "Back"
                                                            )
                                                        }
                                                    }
                                                },
                                                actions = { topBarState.actions(this) }
                                            )
                                        }
                                    }
                                },
                                bottomBar = {
                                    val backStackEntry by navController.currentBackStackEntryAsState()
                                    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
                                    val hideBottomBarRoutes = listOf(
                                        "splash",
                                        "ActorDetails/{personId}",
                                        "detail/{mediaType}/{id}",
                                        "login",
                                        "signup",
                                        "account",
                                        "kids"
                                    )

                                    if (currentRoute != null && currentRoute !in hideBottomBarRoutes) { // 🟢 أضفنا شرط التأكيد
                                        val buttons = listOf(
                                            ButtonData("home", "Home", Icons.Default.Home),
                                            ButtonData("movies", "Movies", Icons.Default.Movie),
                                            ButtonData("actors", "People", Icons.Default.People),
                                            ButtonData("search", "Search", Icons.Default.Search),
                                            ButtonData("settings", "Settings", Icons.Default.Settings)
                                        )
                                        val (topBarBg) = AppTopBarColors(darkTheme = darkTheme)

                                        AnimatedNavigationBar(
                                            navController = navController,
                                            buttons = buttons,
                                            barColor = topBarBg,
                                            circleColor = MaterialTheme.colorScheme.background,
                                            selectedColor = MaterialTheme.colorScheme.primary,
                                            unselectedColor = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }
                            ) { innerPadding ->


                                val navBarInsets = WindowInsets.navigationBars.asPaddingValues()
                                val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
                                val isKidsRoute = currentRoute == "kids"
                                LaunchedEffect(currentRoute) {
                                    topBarState = if (currentRoute == "home") topBarState
                                    else TopBarState() // نفرغ الحالة عند الانتقال لصفحة أخرى
                                }
                                Box(
                                    modifier = Modifier.padding(
                                        top = innerPadding.calculateTopPadding(),
                                        bottom = if (isKidsRoute) 0.dp else navBarInsets.calculateBottomPadding(),
                                        start = innerPadding.calculateLeftPadding(LayoutDirection.Ltr),
                                        end = innerPadding.calculateRightPadding(LayoutDirection.Ltr)
                                    )
                                ) {
                                MdaNavHost(
                                        navController = navController,
                                        moviesRepository = moviesRepository,
                                        actorsRepository = actorRepository,
                                        movieDetailsRepository = movieDetailsRepository,
                                        localDao = mediaDao,
                                        localRepository = localRepository,
                                        onTopBarStateChange = { newState -> topBarState = newState },
                                        genreViewModel = genreViewModel,
                                        searchViewModel = searchViewModel,
                                        actorViewModel = actorViewModel,
                                        favoritesViewModel = favoritesViewModel,
                                        authViewModel = authViewModel,
                                        historyViewModel = historyViewModel,
                                        moviesHistoryViewModel = moviesHistoryViewModel,
                                        authRepository = authRepository,
                                        darkTheme = darkTheme,
                                    homeViewModel = homeViewModel

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