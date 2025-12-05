package com.example.mda.ui.screens.genreDetails.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.mda.data.local.entities.MediaEntity
import com.example.mda.ui.screens.favorites.FavoritesViewModel
import com.example.mda.ui.screens.favorites.components.FavoriteButton
import com.example.mda.ui.screens.genreDetails.mediaTypeFilterIsTv
import com.example.mda.ui.screens.genreDetails.toMovie

@Composable
fun MoviesList(
    movies: List<MediaEntity>,
    isLoading: Boolean,
    listState: LazyListState,
    onMovieClick: (MediaEntity, String) -> Unit,
    favoritesViewModel: FavoritesViewModel,
    isAuthenticated: Boolean,
    onLoginRequired: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        state = listState,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier
    ) {
        items(movies, key = { it.id }) { movie ->
            val mediaType = if (movie.mediaTypeFilterIsTv()) "tv" else "movie"
            Box {
                MovieCardList(movie = movie) { onMovieClick(movie, mediaType) }
                FavoriteButton(
                    movie = movie.toMovie(),
                    viewModel = favoritesViewModel,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(8.dp),
                    showBackground = true,
                    isAuthenticated = isAuthenticated,
                    onLoginRequired = onLoginRequired
                )
            }
        }
        if (isLoading) {
            item { LoadingIndicator() }
        }
    }
}