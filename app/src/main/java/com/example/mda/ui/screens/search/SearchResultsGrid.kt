package com.example.mda.ui.screens.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.mda.data.local.entities.MediaEntity
import com.example.mda.data.repository.mappers.toMovie
import com.example.mda.ui.screens.favorites.FavoritesViewModel
import com.example.mda.ui.screens.favorites.components.FavoriteButton
import com.example.mda.ui.screens.components.MovieCardGridWithFavorite

@Composable
fun SearchResultsGrid(
    results: List<MediaEntity>,
    onItemClick: (MediaEntity) -> Unit,
    favoritesViewModel: FavoritesViewModel,
    navController: NavController,
    isAuthenticated: Boolean,
    gridState: LazyGridState = rememberLazyGridState()
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(150.dp),
        state = gridState,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(bottom = 106.dp, top = 16.dp)
    ) {
        items(
            items = results,
            key = { "${it.mediaType}-${it.id}" }
        ) { media ->
            val movie = media.toMovie()
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(4.dp)
                    .width(150.dp)
            ) {
                MovieCardGridWithFavorite(
                    movie = media,
                    onClick = { onItemClick(media) },
                    favoriteButton = {
                        FavoriteButton(
                            movie = movie,
                            viewModel = favoritesViewModel,
                            showBackground = true,
                            isAuthenticated = isAuthenticated,
                            onLoginRequired = { navController.navigate("settings") }
                        )
                    }
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = media.title ?: media.name ?: "Unknown",
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}