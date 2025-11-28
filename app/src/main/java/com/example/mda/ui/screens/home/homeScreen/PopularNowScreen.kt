package com.example.mda.ui.screens.home.homeScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.mda.data.local.entities.MediaEntity
import com.example.mda.data.repository.mappers.toMovie
import com.example.mda.ui.navigation.TopBarState
import com.example.mda.ui.screens.auth.AuthViewModel
import com.example.mda.ui.screens.favorites.FavoritesViewModel
import com.example.mda.ui.screens.favorites.components.FavoriteButton
import com.example.mda.ui.screens.home.HomeViewModel
import com.google.accompanist.swiperefresh.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PopularNowScreen(
    navController: NavController,
    homeViewModel: HomeViewModel,
    favoritesViewModel: FavoritesViewModel,
    authViewModel: AuthViewModel,
    onTopBarStateChange: (TopBarState) -> Unit
) {
    val authUiState by authViewModel.uiState.collectAsState()

    var showGrid by remember { mutableStateOf(true) }
    var selectedFilter by remember { mutableStateOf(MediaTypeFilter.MOVIES) }

    // البيانات
    val movies = homeViewModel.popularMovies.collectAsState(initial = emptyList()).value
    val tvShows = homeViewModel.popularTvShows.collectAsState(initial = emptyList()).value

    val itemsList =
        if (selectedFilter == MediaTypeFilter.MOVIES) movies else tvShows

    val refreshState = rememberSwipeRefreshState(isRefreshing = false)
    val gridState = rememberLazyGridState()
    val listState = rememberLazyListState()

    onTopBarStateChange(
        TopBarState(
            title = "Popular Now",
            showBackButton = true
        )
    )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Transparent)
        ) {
            MediaTypeFilterRow(
                selectedFilter = selectedFilter,
                onFilterChange = { selectedFilter = it }
            )

            SwipeRefresh(
                state = refreshState,
                onRefresh = {},
                modifier = Modifier.weight(1f)
            ) {
                Box(Modifier.fillMaxSize()) {
                    if (showGrid) {
                        LazyVerticalGrid(
                            state = gridState,
                            columns = GridCells.Fixed(2),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(itemsList, key = { it.id }) { item ->
                                Box {
                                    MovieCardGrid(popularItem = item) {
                                        val type =
                                            if (selectedFilter == MediaTypeFilter.TV_SHOWS) "tv" else "movie"
                                        navController.navigate("detail/$type/${item.id}")
                                    }
                                    FavoriteButton(
                                        movie = item.toMovie(),
                                        viewModel = favoritesViewModel,
                                        modifier = Modifier
                                            .align(Alignment.TopEnd)
                                            .padding(8.dp),
                                        showBackground = true,
                                        isAuthenticated = authUiState.isAuthenticated,
                                        onLoginRequired = { navController.navigate("profile") }
                                    )
                                }
                            }
                        }
                    } else {
                        LazyColumn(
                            state = listState,
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(itemsList, key = { it.id }) { item ->
                                Box {
                                    MovieCardListItem(item = item) {
                                        val type =
                                            if (selectedFilter == MediaTypeFilter.TV_SHOWS) "tv" else "movie"
                                        navController.navigate("detail/$type/${item.id}")
                                    }
                                    FavoriteButton(
                                        movie = item.toMovie(),
                                        viewModel = favoritesViewModel,
                                        modifier = Modifier
                                            .align(Alignment.TopEnd)
                                            .padding(8.dp),
                                        showBackground = true,
                                        isAuthenticated = authUiState.isAuthenticated,
                                        onLoginRequired = { navController.navigate("profile") }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

@Composable
fun MediaTypeFilterRow(
    selectedFilter: MediaTypeFilter,
    onFilterChange: (MediaTypeFilter) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        MediaTypeFilterChip(
            text = "Movies",
            selected = selectedFilter == MediaTypeFilter.MOVIES,
            onClick = { onFilterChange(MediaTypeFilter.MOVIES) },
            modifier = Modifier.weight(1f)
        )
        MediaTypeFilterChip(
            text = "TV Shows",
            selected = selectedFilter == MediaTypeFilter.TV_SHOWS,
            onClick = { onFilterChange(MediaTypeFilter.TV_SHOWS) },
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun MediaTypeFilterChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .height(40.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        color = if (selected) {
            MaterialTheme.colorScheme.primary
        } else {
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        },
        border = if (!selected) ButtonDefaults.outlinedButtonBorder else null
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                color = if (selected)
                    MaterialTheme.colorScheme.onPrimary
                else
                    MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}

@Composable
fun MovieCardGrid(popularItem: MediaEntity, onClick: () -> Unit) {
    Card(
        modifier = Modifier.clickable(onClick = onClick),
        shape = MaterialTheme.shapes.medium
    ) {
        AsyncImage(
            model = "https://image.tmdb.org/t/p/w500${popularItem.posterPath}",
            contentDescription = popularItem.title ?: popularItem.name,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
        )
    }
}

@Composable
fun MovieCardListItem(item: MediaEntity, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
        )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {
            AsyncImage(
                model = "https://image.tmdb.org/t/p/w300${item.posterPath}",
                contentDescription = item.title ?: item.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .width(120.dp)
                    .height(180.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp)) {
                Text(
                    text = item.title ?: item.name ?: "No Title",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(8.dp))
                item.voteAverage?.let {
                    if (it > 0)
                        Text(
                            text = " ${String.format("%.1f", it)}",
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.bodyMedium
                        )
                }
            }
        }
    }
}

enum class MediaTypeFilter { MOVIES, TV_SHOWS }