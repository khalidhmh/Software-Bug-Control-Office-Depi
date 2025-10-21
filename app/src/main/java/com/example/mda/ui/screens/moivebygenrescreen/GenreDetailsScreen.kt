package com.example.mda.ui.screens.moivebygenrescreen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.ui.unit.LayoutDirection
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.mda.data.local.entities.MediaEntity
import com.example.mda.data.repository.MoviesRepository
import com.example.mda.ui.navigation.TopBarState
import com.example.mda.ui.screens.components.MovieCardGrid
import com.example.mda.util.GenreDetailsViewModelFactory
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshIndicator
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import kotlinx.coroutines.launch
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GenreDetailsScreen(
    navController: NavController,
    repository: MoviesRepository,
    genreId: Int,
    genreNameRaw: String,
    onTopBarStateChange: (TopBarState) -> Unit
) {
    val genreName = remember(genreNameRaw) {
        URLDecoder.decode(genreNameRaw, StandardCharsets.UTF_8.toString())
    }

    val viewModel: GenreDetailsViewModel =
        viewModel(factory = GenreDetailsViewModelFactory(repository))

    val movies = viewModel.movies
    val isLoading = viewModel.isLoading
    val error = viewModel.error
    var refreshing by remember { mutableStateOf(false) }
    val refreshState = rememberSwipeRefreshState(refreshing)
    val scope = rememberCoroutineScope()
    var isGridView by remember { mutableStateOf(true) }

    // ✅ إعداد الـ TopBar بنفس فكرة MovieDetailsScreen
    LaunchedEffect(genreName) {
        onTopBarStateChange(
            TopBarState(
                title = genreName,
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { isGridView = !isGridView }) {
                        Icon(
                            imageVector = if (isGridView) Icons.Default.ViewList else Icons.Default.GridView,
                            contentDescription = "Toggle Layout"
                        )
                    }
                }
            )
        )
    }

    SwipeRefresh(
        state = refreshState,
        onRefresh = {
            scope.launch {
                refreshing = true
                viewModel.resetAndLoad(genreId)
                refreshing = false
            }
        },
        indicator = { s, t ->
            SwipeRefreshIndicator(
                s,
                t,
                backgroundColor = Color(0xFF1A2233),
                contentColor = Color.White
            )
        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF101528))
        ) {
            when {
                isLoading && movies.isEmpty() -> Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color.White)
                }

                error != null -> Text(
                    text = "Error: $error",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.Center)
                )

                else -> {
                    if (isGridView) {
                        val gridState = rememberLazyGridState()
                        LaunchedEffect(gridState, movies.size) {
                            snapshotFlow {
                                val total = gridState.layoutInfo.totalItemsCount
                                val lastVisible = gridState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
                                Pair(lastVisible, total)
                            }.collect { (last, total) ->
                                if (last >= total - 4 && !isLoading) {
                                    viewModel.loadMoviesByGenre(genreId)
                                }
                            }
                        }

                        AnimatedVisibility(visible = true, enter = fadeIn()) {
                            LazyVerticalGrid(
                                state = gridState,
                                columns = GridCells.Fixed(2),
                                contentPadding = PaddingValues(16.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp),
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                items(movies, key = { it.id }) { movie ->
                                    MovieCardGrid(movie = movie) {
                                        navController.navigate("detail/${movie.mediaType ?: "movie"}/${movie.id}")
                                    }
                                }
                                if (isLoading) {
                                    item(span = { GridItemSpan(2) }) {
                                        LoadingIndicator()
                                    }
                                }
                            }
                        }
                    } else {
                        val listState = rememberLazyListState()
                        LaunchedEffect(listState, movies.size) {
                            snapshotFlow {
                                val total = listState.layoutInfo.totalItemsCount
                                val lastVisible = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
                                Pair(lastVisible, total)
                            }.collect { (last, total) ->
                                if (last >= total - 4 && !isLoading) {
                                    viewModel.loadMoviesByGenre(genreId)
                                }
                            }
                        }

                        AnimatedVisibility(visible = true, enter = fadeIn()) {
                            LazyColumn(
                                state = listState,
                                contentPadding = PaddingValues(16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                items(movies, key = { it.id }) { movie ->
                                    MovieCardList(movie = movie) {
                                        navController.navigate("detail/${movie.mediaType ?: "movie"}/${movie.id}")
                                    }
                                }
                                if (isLoading) {
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
fun MovieCardList(movie: MediaEntity, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A2233))
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {
            AsyncImage(
                model = "https://image.tmdb.org/t/p/w300${movie.posterPath}",
                contentDescription = movie.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .width(120.dp)
                    .height(180.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp)) {
                Text(
                    text = movie.title ?: "No Title",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = movie.releaseDate ?: "",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = movie.overview ?: "",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
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
        CircularProgressIndicator(color = Color.White.copy(alpha = 0.8f))
    }
}
