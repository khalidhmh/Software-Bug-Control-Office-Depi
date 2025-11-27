@file:Suppress("DEPRECATION")

package com.example.mda.ui.home

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.mda.data.repository.mappers.toMovie
import com.example.mda.ui.navigation.TopBarState
import com.example.mda.ui.screens.auth.AuthViewModel
import com.example.mda.ui.screens.favorites.FavoritesViewModel
import com.example.mda.ui.screens.home.HomeViewModel
import com.example.mda.ui.screens.home.homeScreen.*
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshIndicator
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Calendar

@OptIn(ExperimentalAnimationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    navController: NavController,
    onTopBarStateChange: (TopBarState) -> Unit,
    favoritesViewModel: FavoritesViewModel,
    authViewModel: AuthViewModel
) {
    val trending = viewModel.trendingMedia.collectAsState(initial = emptyList()).value
    val movies = viewModel.popularMovies.collectAsState(initial = emptyList()).value
    val tv = viewModel.popularTvShows.collectAsState(initial = emptyList()).value
    val mixed = viewModel.popularMixed.collectAsState(initial = emptyList()).value
    val recommendations = viewModel.recommendedMedia.collectAsState(initial = emptyList()).value

    val scrollState = rememberSaveable(saver = LazyListState.Saver) { LazyListState() }

    var refreshing by remember { mutableStateOf(false) }
    val refreshState = rememberSwipeRefreshState(isRefreshing = refreshing)
    val coroutineScope = rememberCoroutineScope()
    val authUiState by authViewModel.uiState.collectAsState()

    // 🔹 أول ما الصفحة تفتح، حدّث التوصيات حسب نشاط المستخدم
    LaunchedEffect(Unit) {
        viewModel.onUserActivityDetected(forceRefresh = true)
    }

    val greeting = getGreetingMessage()
    LaunchedEffect(Unit) {
        while (true) {
            onTopBarStateChange(
                TopBarState(
                    title = greeting,
                    subtitle = "What do you want to watch?"
                )
            )
            kotlinx.coroutines.delay(5 * 60 * 1000)
        }
    }
    SwipeRefresh(
        state = refreshState,
        onRefresh = {
            refreshing = true
            coroutineScope.launch {
                viewModel.loadTrending("day")
                viewModel.loadPopularData()
                viewModel.loadTopRated()
                // ✅ تحدّث التوصيات الذكية مع كل Refresh
                viewModel.onUserActivityDetected(forceRefresh = true)
                delay(1500)
                refreshing = false
            }
        },
        indicator = { state, trigger ->
            SwipeRefreshIndicator(
                state = state,
                refreshTriggerDistance = trigger,
                backgroundColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary
            )
        },
        modifier = Modifier.fillMaxSize()
    ) {
        LazyColumn(
            contentPadding = PaddingValues(bottom = 106.dp),
            state = scrollState,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            // ---------------- Banner ----------------
            item {
                AnimatedVisibility(
                    visible = mixed.isNotEmpty(),
                    enter = fadeIn() + slideInVertically()
                ) {
                    BannerSection(movies = mixed.map { it.toMovie() })
                }
            }

            // ---------------- For You Section ----------------
            item {
                val recommendedMovies = recommendations
                    .filter { it.mediaType == "movie" }
                    .map { it.toMovie() }
                val recommendedTvShows = recommendations
                    .filter { it.mediaType == "tv" }
                    .map { it.toMovie() }

                AnimatedVisibility(
                    visible = recommendations.isNotEmpty(),
                    enter = fadeIn()
                ) {
                    ForYouSection(
                        recommendedMovies = recommendedMovies,
                        recommendedTvShows = recommendedTvShows,
                        onMovieClick = { m ->
                            navController.navigate("detail/${m.mediaType}/${m.id}")
                        },
                        favoritesViewModel = favoritesViewModel,
                        navController = navController,
                        isAuthenticated = authUiState.isAuthenticated
                    )
                }
            }

            // ---------------- Trending ----------------
            item {
                TrendingSection(
                    trendingMovies = trending.map { it.toMovie() },
                    selectedWindow = viewModel.selectedTimeWindow,
                    onTimeWindowChange = viewModel::loadTrending,
                    onMovieClick = { m ->
                        navController.navigate("detail/${m.mediaType}/${m.id}")
                    },
                    favoritesViewModel = favoritesViewModel,
                    navController = navController,
                    isAuthenticated = authUiState.isAuthenticated
                )
            }

            // ---------------- Popular ----------------
            item {
                PopularSection(
                    popularMovies = mixed.map { it.toMovie() },
                    onMovieClick = { m ->
                        navController.navigate("detail/${m.mediaType}/${m.id}")
                    },
                    onViewMoreClick = {
                        navController.navigate("popular_movies")
                    },
                    favoritesViewModel = favoritesViewModel,
                    navController = navController,
                    isAuthenticated = authUiState.isAuthenticated
                )
            }
        }
    }
}

@Composable
fun getGreetingMessage(): String {
    val calendar = remember { Calendar.getInstance() }
    val hour = calendar.get(Calendar.HOUR_OF_DAY)

    return when (hour) {
        in 5..11 -> "Good Morning"
        in 12..16 -> "Good Afternoon"
        in 17..20 -> "Good Evening"
        else -> "Good Night"
    }
}