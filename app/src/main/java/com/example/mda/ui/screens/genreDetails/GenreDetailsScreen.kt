package com.example.mda.ui.screens.genreDetails

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.ViewList
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.mda.data.repository.MoviesRepository
import com.example.mda.filteration.FilterDialog
import com.example.mda.ui.navigation.TopBarState
import com.example.mda.ui.screens.auth.AuthViewModel
import com.example.mda.ui.screens.favorites.FavoritesViewModel
import com.example.mda.ui.screens.genreDetails.components.ErrorText
import com.example.mda.ui.screens.genreDetails.components.LoadMoreListener
import com.example.mda.ui.screens.genreDetails.components.MediaTypeFilterRow
import com.example.mda.ui.screens.genreDetails.components.MoviesGrid
import com.example.mda.ui.screens.genreDetails.components.MoviesList
import com.example.mda.util.GenreDetailsViewModelFactory
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshIndicator
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

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
    val genreName = remember(genreNameRaw) { URLDecoder.decode(genreNameRaw, StandardCharsets.UTF_8.toString()) }
    val viewModel: GenreDetailsViewModel = viewModel(factory = GenreDetailsViewModelFactory(repository))

    var isGridView by remember { mutableStateOf(true) }
    var showFilterDialog by remember { mutableStateOf(false) }
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
                        Icon(imageVector = Icons.Default.FilterList, contentDescription = "Filter")
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
        MediaTypeFilterRow(
            selectedFilter = viewModel.mediaTypeFilter,
            onFilterChange = { filter -> viewModel.setMediaTypeFilter(filter, genreId) }
        )

        SwipeRefresh(
            state = rememberSwipeRefreshState(isRefreshing = viewModel.isLoading),
            onRefresh = { viewModel.resetAndLoad(genreId) },
            indicator = { s, t ->
                SwipeRefreshIndicator(
                    s, t,
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

                    viewModel.error != null -> ErrorText(
                        message = viewModel.error ?: "",
                        modifier = Modifier.align(Alignment.Center)
                    )

                    else -> {
                        if (isGridView) {
                            val gridState = rememberLazyGridState()
                            LaunchedEffect(viewModel.movies) { gridState.scrollToItem(0) }

                            LoadMoreListener(
                                gridState = gridState,
                                viewModel = viewModel,
                                genreId = genreId
                            )

                            MoviesGrid(
                                movies = viewModel.movies,
                                isLoading = viewModel.isLoading,
                                gridState = gridState,
                                onMovieClick = { movie, mediaType ->
                                    navController.navigate("detail/$mediaType/${movie.id}")
                                },
                                favoritesViewModel = favoritesViewModel,
                                isAuthenticated = authUiState.isAuthenticated,
                                onLoginRequired = { navController.navigate("settings") }
                            )
                        } else {
                            val listState = rememberLazyListState()
                            LaunchedEffect(viewModel.movies) { listState.scrollToItem(0) }

                            LoadMoreListener(
                                listState = listState,
                                viewModel = viewModel,
                                genreId = genreId
                            )

                            MoviesList(
                                movies = viewModel.movies,
                                isLoading = viewModel.isLoading,
                                listState = listState,
                                onMovieClick = { movie, mediaType ->
                                    navController.navigate("detail/$mediaType/${movie.id}")
                                },
                                favoritesViewModel = favoritesViewModel,
                                isAuthenticated = authUiState.isAuthenticated,
                                onLoginRequired = { navController.navigate("settings") }
                            )
                        }
                    }
                }
            }
        }
    }
}