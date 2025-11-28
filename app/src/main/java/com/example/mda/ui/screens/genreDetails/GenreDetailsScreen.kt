package com.example.mda.ui.screens.genreDetails

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.ViewList
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.mda.data.local.entities.MediaEntity
import com.example.mda.data.remote.model.Movie
import com.example.mda.data.repository.MoviesRepository
import com.example.mda.filteration.FilterDialog
import com.example.mda.ui.navigation.TopBarState
import com.example.mda.ui.screens.auth.AuthViewModel
import com.example.mda.ui.screens.components.MovieCardGrid
import com.example.mda.ui.screens.favorites.FavoritesViewModel
import com.example.mda.ui.screens.favorites.components.FavoriteButton
import com.example.mda.util.GenreDetailsViewModelFactory
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshIndicator
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GenreDetailsScreen(
    navController: NavController,
    repository: MoviesRepository,
    genreId: Int,
    genreNameRaw: String,
    onTopBarStateChange: (TopBarState) -> Unit,
    favoritesViewModel: FavoritesViewModel,
    authViewModel: AuthViewModel,
) {
    val authUiState by authViewModel.uiState.collectAsState()

    val genreName = remember(genreNameRaw) {
        URLDecoder.decode(genreNameRaw, StandardCharsets.UTF_8.toString())
    }

    val viewModel: GenreDetailsViewModel =
        viewModel(factory = GenreDetailsViewModelFactory(repository))

    var isGridView by remember { mutableStateOf(true) }
    var showFilterDialog by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val snackbarMessage by favoritesViewModel.snackbarMessage.collectAsState()

    LaunchedEffect(snackbarMessage) {
        snackbarMessage?.let {
            snackbarHostState.showSnackbar(it)
            favoritesViewModel.clearSnackbarMessage()
        }
    }

    LaunchedEffect(genreName) {
        onTopBarStateChange(
            TopBarState(
                title = genreName,
                showBackButton = true,
                actions = {
                    IconButton(onClick = { isGridView = !isGridView }) {
                        Icon(
                            imageVector = if (isGridView) Icons.Default.ViewList else Icons.Default.GridView,
                            contentDescription = "Toggle Layout"
                        )
                    }
                    IconButton(onClick = { showFilterDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.FilterList,
                            contentDescription = "Filter"
                        )
                    }
                }
            )
        )
    }

    if (showFilterDialog) {
        FilterDialog(
            showDialog = showFilterDialog,
            onDismiss = { showFilterDialog = false },
            viewModel = viewModel
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent)
    ) {
        // Media Type Filter Row (Movies / TV Shows)
        MediaTypeFilterRow(
            selectedFilter = viewModel.mediaTypeFilter,
            onFilterChange = { filter ->
                viewModel.setMediaTypeFilter(filter, genreId)
            }
        )

        SwipeRefresh(
            state = rememberSwipeRefreshState(isRefreshing = viewModel.isLoading),
            onRefresh = { viewModel.resetAndLoad(genreId) },
            indicator = { s, t ->
                SwipeRefreshIndicator(
                    s,
                    t,
                    backgroundColor = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = MaterialTheme.colorScheme.onSurface
                )
            },
            modifier = Modifier.weight(1f)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Transparent)
            ) {
                when {
                    viewModel.isLoading && viewModel.movies.isEmpty() -> Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    }

                    viewModel.error != null -> Text(
                        text = "Error: ${viewModel.error}",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.Center)
                    )

                    else -> {
                        if (isGridView) {
                            val gridState = rememberLazyGridState()

                            LaunchedEffect(viewModel.movies) {
                                gridState.scrollToItem(0)
                            }

                            LoadMoreListener(
                                gridState = gridState,
                                viewModel = viewModel,
                                genreId = genreId
                            )

                            LazyVerticalGrid(
                                state = gridState,
                                columns = GridCells.Fixed(2),
                                contentPadding = PaddingValues(16.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp),
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                items(viewModel.movies, key = { it.id }) { movie ->
                                    Box {
                                        MovieCardGrid(movie = movie) {
                                            val mediaType = if (viewModel.mediaTypeFilter == MediaTypeFilter.TV_SHOWS) "tv" else "movie"
                                            navController.navigate("detail/$mediaType/${movie.id}")
                                        }

                                        FavoriteButton(
                                            movie = movie.toMovie(),
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
                                if (viewModel.isLoading) {
                                    item(span = { GridItemSpan(2) }) {
                                        LoadingIndicator()
                                    }
                                }
                            }
                        } else {
                            val listState = rememberLazyListState()

                            LaunchedEffect(viewModel.movies) {
                                listState.scrollToItem(0)
                            }

                            LoadMoreListener(
                                listState = listState,
                                viewModel = viewModel,
                                genreId = genreId
                            )

                            LazyColumn(
                                state = listState,
                                contentPadding = PaddingValues(16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                items(viewModel.movies, key = { it.id }) { movie ->
                                    Box {
                                        MovieCardList(movie = movie) {
                                            val mediaType = if (viewModel.mediaTypeFilter == MediaTypeFilter.TV_SHOWS) "tv" else "movie"
                                            navController.navigate("detail/$mediaType/${movie.id}")
                                        }

                                        FavoriteButton(
                                            movie = movie.toMovie(),
                                            viewModel = favoritesViewModel,
                                            modifier = Modifier
                                                .align(Alignment.TopStart)
                                                .padding(8.dp),
                                            showBackground = true,
                                            onLoginRequired = { navController.navigate("profile") }
                                        )
                                    }
                                }
                                if (viewModel.isLoading) {
                                    item { LoadingIndicator() }
                                }
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
        border = if (!selected) {
            ButtonDefaults.outlinedButtonBorder
        } else null
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                color = if (selected) {
                    MaterialTheme.colorScheme.onPrimary
                } else {
                    MaterialTheme.colorScheme.onSurface
                },
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}

@Composable
fun LoadMoreListener(
    gridState: LazyGridState? = null,
    listState: LazyListState? = null,
    viewModel: GenreDetailsViewModel,
    genreId: Int
) {
    LaunchedEffect(gridState, listState, viewModel.movies.size) {
        snapshotFlow {
            val total =
                gridState?.layoutInfo?.totalItemsCount ?: listState?.layoutInfo?.totalItemsCount
                ?: 0
            val lastVisible = gridState?.layoutInfo?.visibleItemsInfo?.lastOrNull()?.index
                ?: listState?.layoutInfo?.visibleItemsInfo?.lastOrNull()?.index ?: 0
            lastVisible to total
        }.collect { (last, total) ->
            if (last >= total - 4 && !viewModel.isLoading) {
                viewModel.loadMoviesByGenre(genreId)
            }
        }
    }
}

@Composable
fun MovieCardList(movie: MediaEntity, onClick: () -> Unit) {
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
                model = "https://image.tmdb.org/t/p/w300${movie.posterPath}",
                contentDescription = movie.title ?: movie.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .width(120.dp)
                    .height(180.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp)) {
                Text(
                    text = movie.title ?: movie.name ?: "No Title",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = movie.releaseDate ?: movie.firstAirDate ?: "",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = movie.overview ?: "",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun LoadingIndicator() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
    }
}

// Extension function to convert MediaEntity to Movie
private fun MediaEntity.toMovie(): Movie {
    return Movie(
        id = this.id,
        title = this.title,
        name = this.name,
        overview = this.overview,
        posterPath = this.posterPath,
        backdropPath = this.backdropPath,
        releaseDate = this.releaseDate,
        firstAirDate = this.firstAirDate,
        voteAverage = this.voteAverage ?: 0.0,
        mediaType = this.mediaType,
        adult = this.adult,
        genreIds = this.genreIds
    )
}