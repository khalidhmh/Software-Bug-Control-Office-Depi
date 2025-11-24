package com.example.mda.ui.kids

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.mda.data.local.entities.MediaEntity
import com.example.mda.data.repository.MoviesRepository
import com.example.mda.ui.kids.KidsFilter.filterKids
import com.example.mda.ui.kids.favorites.KidsFavoriteButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun KidsSectionScreen(
    category: String,
    moviesRepository: MoviesRepository,
    onItemClick: (MediaEntity) -> Unit
) {
    val itemsState = remember { mutableStateOf<List<MediaEntity>>(emptyList()) }

    LaunchedEffect(category) {
        val movies = withContext(Dispatchers.IO) { moviesRepository.getPopularMovies() }
        val topRated = withContext(Dispatchers.IO) { moviesRepository.getTopRatedMovies() }
        val tv = withContext(Dispatchers.IO) { moviesRepository.getPopularTvShows() }
        val trending = withContext(Dispatchers.IO) { moviesRepository.getTrendingMedia("all","day") }
        val all = (movies + topRated + tv + trending).distinctBy { it.id }
        val kids = filterKids(all)
        val filtered = when (category) {
            "cartoons" -> kids.filter { it.genreIds?.contains(16) == true || (it.genres?.any { it.contains("Animation", true) } == true) }
            "family" -> kids.filter { it.genreIds?.contains(10751) == true || (it.genres?.any { it.contains("Family", true) } == true) }
            "anime" -> kids.filter { (it.title ?: it.name ?: "").contains("anime", true) || (it.genres?.any { it.contains("Anime", true) } == true) }
            else -> kids
        }
        itemsState.value = filtered
    }

    LazyVerticalGrid(
        columns = GridCells.Adaptive(140.dp),
        modifier = Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 106.dp)
    ) {
        items(itemsState.value, key = { it.id }) { media ->
            KidsPosterCard(
                media = media,
                onClick = { onItemClick(media) },
                favoriteButton = { KidsFavoriteButton(id = media.id, showBackground = true) },
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(2f/3f)
            )
        }
    }
}
