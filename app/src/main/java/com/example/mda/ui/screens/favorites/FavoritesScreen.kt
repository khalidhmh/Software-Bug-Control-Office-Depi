package com.example.mda.ui.screens.favorites

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.mda.data.local.entities.MediaEntity
import com.example.mda.data.remote.model.Movie
import com.example.mda.ui.screens.home.homeScreen.MovieCard

@Composable
fun FavoritesScreen(
    viewModel: FavoritesViewModel,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val favorites by viewModel.favorites.collectAsState()
    val snackbarMessage by viewModel.snackbarMessage.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(snackbarMessage) {
        snackbarMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearSnackbarMessage()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            if (favorites.isEmpty()) {
                // Empty state
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "لا توجد أفلام مفضلة بعد",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "ابدأ بإضافة أفلامك المفضلة من الصفحة الرئيسية",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f),
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                // Grid of favorite movies
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(favorites) { mediaEntity ->
                        val movie = mediaEntity.toMovie()
                        MovieCard(
                            movie = movie,
                            onClick = {
                                val type = mediaEntity.resolvedMediaType
                                navController.navigate("detail/$type/${mediaEntity.id}")
                            }
                        )
                    }
                }
            }
        }
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
