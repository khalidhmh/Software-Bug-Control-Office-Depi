package com.example.mda.ui.screens.genreDetails.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.mda.data.local.entities.MediaEntity
import com.example.mda.ui.screens.components.MovieCardGrid
import com.example.mda.ui.screens.favorites.FavoritesViewModel
import com.example.mda.ui.screens.favorites.components.FavoriteButton
import com.example.mda.ui.screens.genreDetails.mediaTypeFilterIsTv
import com.example.mda.ui.screens.genreDetails.toMovie

@Composable
fun MoviesGrid(
    movies: List<MediaEntity>,
    isLoading: Boolean,
    gridState: LazyGridState,
    onMovieClick: (MediaEntity, String) -> Unit,
    favoritesViewModel: FavoritesViewModel,
    isAuthenticated: Boolean,
    onLoginRequired: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        state = gridState,
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier
    ) {
        items(movies, key = { it.id }) { movie ->
            val mediaType = if (movie.mediaTypeFilterIsTv()) "tv" else "movie"
            Box {
                MovieCardGrid(movie = movie) { onMovieClick(movie, mediaType) }
                FavoriteButton(
                    movie = movie.toMovie(),
                    viewModel = favoritesViewModel,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp),
                    showBackground = true,
                    isAuthenticated = isAuthenticated,
                    onLoginRequired = onLoginRequired
                )
            }
        }
        if (isLoading) {
            item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(2) }) {
                LoadingIndicator()
            }
        }
    }
}