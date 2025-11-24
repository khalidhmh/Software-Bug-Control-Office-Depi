package com.example.mda.ui.kids

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.mda.ui.kids.KidsFilter.filterKids
import com.example.mda.ui.screens.components.MovieCardGridWithFavorite
import com.example.mda.ui.kids.favorites.KidsFavoriteButton
import com.example.mda.data.local.entities.MediaEntity
import com.example.mda.data.remote.model.Movie
import com.example.mda.ui.kids.favorites.KidsFavoritesStore
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.flow.combine

@Composable
fun KidsFavoritesScreen(
    localRepository: com.example.mda.data.local.LocalRepository,
    onItemClick: (MediaEntity) -> Unit,
) {
    val context = LocalContext.current
    val allItems by localRepository.getAll().collectAsState(initial = emptyList())
    val kidsIds by KidsFavoritesStore.favoritesIdsFlow(context).collectAsState(initial = emptySet())
    val kidsFavorites = filterKids(allItems.filter { it.id in kidsIds })

    LazyVerticalGrid(
        columns = GridCells.Adaptive(140.dp),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 106.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(kidsFavorites, key = { it.id }) { media ->
            MovieCardGridWithFavorite(
                movie = media,
                onClick = { onItemClick(media) },
                favoriteButton = {
                    KidsFavoriteButton(id = media.id, showBackground = true)
                }
            )
        }
    }
}

private fun MediaEntity.toMovie(): Movie = Movie(
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
