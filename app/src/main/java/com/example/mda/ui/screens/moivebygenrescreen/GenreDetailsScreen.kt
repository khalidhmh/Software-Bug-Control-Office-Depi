package com.example.mda.ui.screens.moivebygenrescreen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.mda.data.local.entities.MediaEntity
import com.example.mda.data.repository.MoviesRepository
import com.example.mda.ui.screens.components.MovieCardGrid
import com.example.mda.util.GenreDetailsViewModelFactory
import com.google.accompanist.swiperefresh.*
import kotlinx.coroutines.launch
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GenreDetailsScreen(
    navController: NavController,
    repository: MoviesRepository,
    genreId: Int,
    genreNameRaw: String
) {
    val genreName = URLDecoder.decode(genreNameRaw, StandardCharsets.UTF_8.toString())
    val viewModel: GenreDetailsViewModel = viewModel(factory = GenreDetailsViewModelFactory(repository))

    val movies = viewModel.movies
    val isLoading = viewModel.isLoading
    val error = viewModel.error
    val listState = rememberLazyGridState()
    var refreshing by remember { mutableStateOf(false) }
    val refreshState = rememberSwipeRefreshState(refreshing)
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) { viewModel.resetAndLoad(genreId) }
    LaunchedEffect(listState, movies.size) {
        snapshotFlow {
            val totalItems = listState.layoutInfo.totalItemsCount
            val lastVisible = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            Pair(lastVisible, totalItems)
        }.collect { (lastVisible, totalItems) ->
            if (lastVisible >= totalItems - 4 && !isLoading) {
                viewModel.loadMoviesByGenre(genreId)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "$genreName",
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { padding ->
        SwipeRefresh(
            state = refreshState,
            onRefresh = {
                refreshing = true
                scope.launch { viewModel.resetAndLoad(genreId) }
                refreshing = false
            },
            indicator = { s, t ->
                SwipeRefreshIndicator(
                    s, t,
                    backgroundColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.primary
                )
            }
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(padding)
            ) {
                when {
                    isLoading && movies.isEmpty() -> Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    }

                    error != null -> Text(
                        text = "Error: $error",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.Center)
                    )

                    else -> AnimatedVisibility(
                        visible = movies.isNotEmpty(),
                        enter = fadeIn() + slideInVertically()
                    ) {
                        LazyVerticalGrid(
                            state = listState,
                            columns = GridCells.Fixed(2),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(movies) { movie: MediaEntity ->
                                MovieCardGrid(
                                    movie = movie,
                                    onClick = {
                                        navController.navigate("detail/${movie.mediaType ?: "movie"}/${movie.id}")
                                    }
                                )
                            }

                            if (isLoading) {
                                item(span = { GridItemSpan(2) }) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        CircularProgressIndicator(
                                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
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
}
