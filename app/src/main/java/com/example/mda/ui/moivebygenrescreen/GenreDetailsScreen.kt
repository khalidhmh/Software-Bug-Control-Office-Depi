package com.example.mda.ui.moivebygenrescreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.mda.data.repository.MoviesRepository
import com.example.mda.ui.components.MovieCardGrid
import com.example.mda.util.GenreDetailsViewModelFactory
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

    val viewModel: GenreDetailsViewModel = viewModel(
        factory = GenreDetailsViewModelFactory(repository)
    )

    val movies = viewModel.movies
    val isLoading = viewModel.isLoading
    val error = viewModel.error

    val listState = rememberLazyGridState()

    LaunchedEffect(listState, movies.size) {
        snapshotFlow {
            val layoutInfo = listState.layoutInfo
            val totalItems = layoutInfo.totalItemsCount
            val lastVisibleItemIndex = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            Pair(lastVisibleItemIndex, totalItems)
        }.collect { (lastVisibleItemIndex, totalItems) ->

            val shouldLoadMore = lastVisibleItemIndex >= totalItems - 4

            if (shouldLoadMore && !isLoading) {
                viewModel.loadMoviesByGenre(genreId)
            }
        }
    }


    // first load
    LaunchedEffect(genreId) {
        viewModel.resetAndLoad(genreId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("$genreName Movies") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF191E2A),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { padding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF101528))
                .padding(padding)
        ) {
            when {
                isLoading && movies.isEmpty() -> Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) { CircularProgressIndicator() }

                error != null -> Text(
                    text = "Error: $error",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.Center)
                )

                else -> LazyVerticalGrid(
                    state = listState,
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(movies) { movie ->
                        MovieCardGrid(movie = movie, onClick = {})
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
                                    color = Color.White.copy(alpha = 0.7f)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
