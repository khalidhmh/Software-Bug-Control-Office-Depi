@file:Suppress("DEPRECATION")

package com.example.mda.ui.home

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.mda.data.repository.mappers.toMovie
import com.example.mda.ui.navigation.TopBarState
import com.example.mda.ui.screens.home.HomeViewModel
import com.example.mda.ui.screens.home.homeScreen.*
import com.example.mda.ui.screens.favorites.FavoritesViewModel
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshIndicator
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalAnimationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    navController: NavController,
    onTopBarStateChange: (TopBarState) -> Unit,
    favoritesViewModel: FavoritesViewModel
) {
    val trending = viewModel.trendingMedia.collectAsState(initial = emptyList()).value
    val movies = viewModel.popularMovies.collectAsState(initial = emptyList()).value
    val tv = viewModel.popularTvShows.collectAsState(initial = emptyList()).value
    val mixed = viewModel.popularMixed.collectAsState(initial = emptyList()).value

    val scrollState = rememberSaveable(saver = LazyListState.Saver) { LazyListState() }

    var refreshing by remember { mutableStateOf(false) }
    val refreshState = rememberSwipeRefreshState(isRefreshing = refreshing)
    val coroutineScope = rememberCoroutineScope()

    // Update top bar (keeps previous behaviour)
    LaunchedEffect(Unit) {
        onTopBarStateChange(
            TopBarState(
                title = "Home",
                actions = {
                    // place page-specific icons if needed
                }
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
            modifier = Modifier
                .padding()
                .fillMaxSize()
        ) {
            LazyColumn(
                contentPadding = PaddingValues(bottom = 106.dp),
                state = scrollState,
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    AnimatedVisibility(
                        visible = mixed.isNotEmpty(),
                        enter = fadeIn() + slideInVertically()
                    ) {
                        BannerSection(movies = mixed.map { it.toMovie() })
                    }
                }
                item {
                    AnimatedVisibility(
                        visible = movies.isNotEmpty(),
                        enter = fadeIn()
                    ) {
                        ForYouSection(
                            movies = movies.map { it.toMovie() },
                            tvShows = tv.map { it.toMovie() },
                            onMovieClick = { m ->
                                navController.navigate("detail/${m.mediaType}/${m.id}")
                            },
                            favoritesViewModel = favoritesViewModel
                        )
                    }
                }
                item {
                    TrendingSection(
                        trendingMovies = trending.map { it.toMovie() },
                        selectedWindow = viewModel.selectedTimeWindow,
                        onTimeWindowChange = viewModel::loadTrending,
                        onMovieClick = { m ->
                            navController.navigate("detail/${m.mediaType}/${m.id}")
                        },
                        favoritesViewModel = favoritesViewModel
                    )
                }
                item {
                    PopularSection(
                        popularMovies = mixed.map { it.toMovie() },
                        onMovieClick = { m ->
                            navController.navigate("detail/${m.mediaType}/${m.id}")
                        },
                        onViewMoreClick = {},
                        favoritesViewModel = favoritesViewModel
                    )
                }
            }
        }
}
