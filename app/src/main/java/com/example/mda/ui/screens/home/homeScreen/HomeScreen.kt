@file:Suppress("DEPRECATION")

package com.example.mda.ui.home

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
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
import com.example.mda.localization.LocalizationKeys
import com.example.mda.localization.localizedString
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
    val trendingEntities by viewModel.trendingMedia.collectAsState()
    val mixedEntities by viewModel.popularMixed.collectAsState()

    val recMoviesEntities by viewModel.recommendedMovies.collectAsState()
    val recTvEntities by viewModel.recommendedTvShows.collectAsState()

    val trendingList = remember(trendingEntities) { trendingEntities.map { it.toMovie() } }
    val bannerList = remember(mixedEntities) { mixedEntities.map { it.toMovie() } }

    val recommendedMoviesList = remember(recMoviesEntities) { recMoviesEntities.map { it.toMovie() } }
    val recommendedTvShowsList = remember(recTvEntities) { recTvEntities.map { it.toMovie() } }

    val scrollState = rememberSaveable(saver = LazyListState.Saver) { LazyListState() }

    var refreshing by remember { mutableStateOf(false) }
    val refreshState = rememberSwipeRefreshState(isRefreshing = refreshing)
    val coroutineScope = rememberCoroutineScope()
    val authUiState by authViewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        if (trendingEntities.isEmpty() || mixedEntities.isEmpty()) {
            viewModel.onUserActivityDetected(forceRefresh = true)
        } else {
            viewModel.onUserActivityDetected(forceRefresh = false)
        }
    }

    val greetingKey = remember {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        when (hour) {
            in 5..11 -> LocalizationKeys.HOME_GREETING_MORNING
            in 12..16 -> LocalizationKeys.HOME_GREETING_AFTERNOON
            in 17..20 -> LocalizationKeys.HOME_GREETING_EVENING
            else -> LocalizationKeys.HOME_GREETING_NIGHT
        }
    }
    
    val titleText = localizedString(greetingKey)
    val subtitleText = localizedString(LocalizationKeys.HOME_SUBTITLE)

    LaunchedEffect(titleText, subtitleText) {
        onTopBarStateChange(
            TopBarState(
                title = titleText,
                subtitle = subtitleText
            )
        )
    }

    SwipeRefresh(
        state = refreshState,
        onRefresh = {
            refreshing = true
            coroutineScope.launch {
                viewModel.loadTrending("day")
                viewModel.loadPopularData()
                viewModel.loadTopRated()
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

            item {
                AnimatedVisibility(
                    visible = bannerList.isNotEmpty(),
                    enter = fadeIn() + slideInVertically()
                ) {
                    BannerSection(movies = bannerList)
                }
            }

            item {
                val showRecommendations = recommendedMoviesList.isNotEmpty() || recommendedTvShowsList.isNotEmpty()

                AnimatedVisibility(
                    visible = showRecommendations,
                    enter = fadeIn()
                ) {
                    ForYouSection(
                        recommendedMovies = recommendedMoviesList,
                        recommendedTvShows = recommendedTvShowsList,
                        onMovieClick = { m ->
                            navController.navigate("detail/${m.mediaType}/${m.id}")
                        },
                        favoritesViewModel = favoritesViewModel,
                        navController = navController,
                        isAuthenticated = authUiState.isAuthenticated
                    )
                }
            }

            item {
                TrendingSection(
                    trendingMovies = trendingList,
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

            item {
                PopularSection(
                    popularMovies = bannerList,
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