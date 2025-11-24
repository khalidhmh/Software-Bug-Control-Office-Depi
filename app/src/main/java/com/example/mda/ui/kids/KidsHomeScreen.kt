package com.example.mda.ui.kids

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.background
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import com.example.mda.data.local.entities.MediaEntity
import com.example.mda.data.repository.MoviesRepository
import com.example.mda.ui.kids.KidsFilter.filterKids
import com.example.mda.ui.screens.components.MovieCardGridWithFavorite
import com.example.mda.ui.screens.favorites.FavoritesViewModel
import com.example.mda.ui.kids.favorites.KidsFavoriteButton
import com.example.mda.data.remote.model.Movie
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import coil.compose.AsyncImage
import androidx.compose.material3.Card
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.animation.Crossfade

@Composable
fun KidsHomeScreen(
    moviesRepository: MoviesRepository,
    favoritesViewModel: FavoritesViewModel,
    onItemClick: (MediaEntity) -> Unit,
    onOpenSection: (String) -> Unit,
) {
    val allKids = remember { mutableStateOf<List<MediaEntity>>(emptyList()) }

    val heroItems = remember { mutableStateOf<List<MediaEntity>>(emptyList()) }
    val cartoons = remember { mutableStateOf<List<MediaEntity>>(emptyList()) }
    val family = remember { mutableStateOf<List<MediaEntity>>(emptyList()) }
    val anime = remember { mutableStateOf<List<MediaEntity>>(emptyList()) }

    LaunchedEffect(Unit) {
        val movies = withContext(Dispatchers.IO) { moviesRepository.getPopularMovies() }
        val topRated = withContext(Dispatchers.IO) { moviesRepository.getTopRatedMovies() }
        val tv = withContext(Dispatchers.IO) { moviesRepository.getPopularTvShows() }
        val trending = withContext(Dispatchers.IO) { moviesRepository.getTrendingMedia("all","day") }
        val all = (movies + topRated + tv + trending).distinctBy { it.id }
        val kids = filterKids(all)
        allKids.value = kids

        heroItems.value = kids.filter { !it.backdropPath.isNullOrEmpty() }
            .distinctBy { it.id }
            .take(6)
        cartoons.value = kids.filter { it.genreIds?.contains(16) == true || (it.genres?.any { it.contains("Animation", true) } == true) }.take(20)
        family.value = kids.filter { it.genreIds?.contains(10751) == true || (it.genres?.any { it.contains("Family", true) } == true) }.take(20)
        anime.value = kids.filter { (it.title ?: it.name ?: "").contains("anime", true) || (it.genres?.any { it.contains("Anime", true) } == true) }.take(20)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize(),
        contentPadding = PaddingValues(bottom = 106.dp)
    ) {
        // Hero carousel
        item {
            if (heroItems.value.isNotEmpty()) {
                HeroCarousel(items = heroItems.value, onItemClick = onItemClick)
            }
        }

        // Sections
        if (cartoons.value.isNotEmpty()) {
            item { SectionHeader(title = "New Cartoons", onClick = { onOpenSection("cartoons") }) }
            item { MediaRow(list = cartoons.value, onItemClick = onItemClick) }
        }
        if (family.value.isNotEmpty()) {
            item { SectionHeader(title = "Family Movies", onClick = { onOpenSection("family") }) }
            item { MediaRow(list = family.value, onItemClick = onItemClick) }
        }
        if (anime.value.isNotEmpty()) {
            item { SectionHeader(title = "Anime Movies", onClick = { onOpenSection("anime") }) }
            item { MediaRow(list = anime.value, onItemClick = onItemClick) }
        }
        // Fallback: show grid of remaining items if needed could be added later.
    }
}

@Composable
private fun SectionHeader(title: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = title, style = MaterialTheme.typography.titleMedium)
        IconButton(onClick = onClick) {
            Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null, tint = Color.Gray)
        }
    }
}

@Composable
private fun MediaRow(
    list: List<MediaEntity>,
    onItemClick: (MediaEntity) -> Unit
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(list, key = { it.id }) { media ->
            KidsPosterCard(media = media, onClick = { onItemClick(media) }) {
                KidsFavoriteButton(id = media.id, showBackground = true)
            }
        }
    }
}

@Composable
private fun HeroCarousel(
    items: List<MediaEntity>,
    onItemClick: (MediaEntity) -> Unit
) {
    var index by remember { mutableIntStateOf(0) }
    LaunchedEffect(items) {
        while (items.isNotEmpty()) {
            delay(2500)
            index = (index + 1) % items.size
        }
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp, bottom = 12.dp)
    ) {
        val current = items.getOrNull(index)
        if (current != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(320.dp)
                    .padding(horizontal = 12.dp)
                    .clip(androidx.compose.foundation.shape.RoundedCornerShape(18.dp))
            ) {
                // Backdrop image as background with subtle crossfade on change
                Crossfade(targetState = current, label = "HeroCrossfade") { item ->
                    if (item != null) {
                        AsyncImage(
                            model = item.backdropPath?.let { "https://image.tmdb.org/t/p/w780$it" }
                                ?: item.posterPath?.let { "https://image.tmdb.org/t/p/w500$it" },
                            contentDescription = item.title ?: item.name ?: "",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }

                // Gradient overlay (stronger at bottom)
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color(0x00000000),
                                    Color(0x33000000),
                                    Color(0x99000000)
                                )
                            )
                        )
                )

                // Small kids badge top-left
                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(12.dp)
                        .clip(androidx.compose.foundation.shape.RoundedCornerShape(8.dp))
                        .background(Color(0xFF00D8A0).copy(alpha = 0.9f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(text = "kids", color = Color.White, style = MaterialTheme.typography.labelSmall)
                }

                // Content overlay (title, tags, buttons)
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = current.title ?: current.name ?: "Unknown",
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color.White
                    )

                    Spacer(Modifier.height(8.dp))

                    // simple tags row (genres) + light subtitle
                    val tags = current.genres?.take(2) ?: emptyList()
                    if (tags.isNotEmpty()) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            tags.forEach { tag ->
                                Box(
                                    modifier = Modifier
                                        .clip(androidx.compose.foundation.shape.RoundedCornerShape(50))
                                        .background(Color.White.copy(alpha = 0.25f))
                                        .padding(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Text(tag, color = Color.White, style = MaterialTheme.typography.labelSmall)
                                }
                            }
                        }
                        Spacer(Modifier.height(6.dp))
                        Text(
                            text = "Animation • Family",
                            color = Color.White.copy(alpha = 0.85f),
                            style = MaterialTheme.typography.bodySmall
                        )
                        Spacer(Modifier.height(10.dp))
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Button(onClick = { onItemClick(current) }) {
                            Text("Watch")
                        }
                    }
                }

                // Page indicators
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    repeat(items.size.coerceAtMost(6)) { idx ->
                        val selected = (index % items.size.coerceAtLeast(1)) == idx
                        Box(
                            modifier = Modifier
                                .clip(androidx.compose.foundation.shape.RoundedCornerShape(50))
                                .background(if (selected) Color.White else Color.White.copy(alpha = 0.4f))
                                .height(6.dp)
                                .width(if (selected) 16.dp else 6.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun KidsPosterCard(
    media: com.example.mda.data.local.entities.MediaEntity,
    onClick: () -> Unit,
    modifier: Modifier = Modifier.width(150.dp).height(220.dp),
    favoriteButton: @Composable () -> Unit
) {
    androidx.compose.material3.Card(
        onClick = onClick,
        shape = androidx.compose.foundation.shape.RoundedCornerShape(14.dp)
    ) {
        Box(
            modifier = modifier
        ) {
            coil.compose.AsyncImage(
                model = media.posterPath?.let { "https://image.tmdb.org/t/p/w500$it" }
                    ?: media.backdropPath?.let { "https://image.tmdb.org/t/p/w500$it" },
                contentDescription = media.title ?: media.name ?: "",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(6.dp)
                    .clip(androidx.compose.foundation.shape.RoundedCornerShape(8.dp))
                    .background(Color.Black.copy(alpha = 0.55f))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = "⭐ ${String.format("%.1f", media.voteAverage ?: 0.0)}",
                    color = Color.White,
                    style = MaterialTheme.typography.labelSmall
                )
            }

            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(6.dp)
            ) {
                favoriteButton()
            }
        }
    }
}

// mapper
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
