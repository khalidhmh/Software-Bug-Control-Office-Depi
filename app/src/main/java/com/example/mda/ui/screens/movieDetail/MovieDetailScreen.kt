@file:OptIn(ExperimentalLayoutApi::class)

package com.example.mda.ui.screens.movieDetail

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowBackIos
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.core.graphics.ColorUtils
import androidx.palette.graphics.Palette
import coil.compose.rememberAsyncImagePainter
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.example.mda.data.local.entities.MediaEntity
import com.example.mda.data.local.entities.MoviesViewedEntitty
import com.example.mda.data.repository.MovieDetailsRepository
import com.example.mda.data.repository.MoviesRepository
import com.example.mda.ui.navigation.TopBarState
import com.example.mda.ui.screens.movieDetail.components.CastItem
import com.example.mda.ui.screens.movieDetail.components.VideoThumbnail
import com.example.mda.ui.screens.favorites.FavoritesViewModel
import com.example.mda.ui.screens.favorites.components.FavoriteButton
import com.example.mda.data.remote.model.Movie
import com.example.mda.ui.screens.actordetails.widgets.InfoCard
import com.example.mda.ui.screens.profile.history.MoviesHistoryViewModel
import com.google.accompanist.swiperefresh.*
import kotlinx.coroutines.launch


@Composable
fun SurfaceChip(text: String) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = MaterialTheme.shapes.small,
        modifier = Modifier.padding(0.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}
@Composable
 fun ExpandableText(
    text: String,
    collapsedLines: Int = 6
) {
    var expanded by remember { mutableStateOf(false) }
    Column {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = if (expanded) Int.MAX_VALUE else collapsedLines,
            overflow = TextOverflow.Ellipsis
        )
        Spacer(Modifier.height(6.dp))
        TextButton(onClick = { expanded = !expanded }) {
            Text(if (expanded) "Show less" else "Show more")
        }
    }
}


@Composable
fun RecommendationsSimilarTabs(
    recommendations: List<MediaEntity>,
    similar: List<MediaEntity>,
    navController: NavController
) {
    val recCount = recommendations.size
    val simCount = similar.size
    if (recCount == 0 && simCount == 0) return
    var selected by remember { mutableStateOf(0) }
    Surface(
        tonalElevation = 2.dp,
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = "Discover",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(Modifier.height(8.dp))
            TabRow(
                selectedTabIndex = selected,
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                contentColor = MaterialTheme.colorScheme.onSurfaceVariant
            ) {
                Tab(
                    selected = selected == 0,
                    onClick = { selected = 0 },
                    selectedContentColor = MaterialTheme.colorScheme.primary,
                    unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    text = { Text("Recommendations $recCount", style = MaterialTheme.typography.titleSmall) }
                )
                Tab(
                    selected = selected == 1,
                    onClick = { selected = 1 },
                    selectedContentColor = MaterialTheme.colorScheme.primary,
                    unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    text = { Text("Similar $simCount", style = MaterialTheme.typography.titleSmall) }
                )
            }
            Spacer(Modifier.height(8.dp))
        when (selected) {
            0 -> {
                if (recCount == 0) {
                    Text(
                        text = "No recommendations",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                } else {
                    androidx.compose.foundation.lazy.LazyRow(
                        contentPadding = PaddingValues(horizontal = 0.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(items = recommendations) { item: MediaEntity ->
                            SimilarItemCard(
                                media = item,
                                onClick = {
                                    navController.navigate("detail/${item.mediaType ?: "movie"}/${item.id}")
                                }
                            )
                        }
                    }
                }
            }
            else -> {
                if (simCount == 0) {
                    Text(
                        text = "No similar",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                } else {
                    androidx.compose.foundation.lazy.LazyRow(
                        contentPadding = PaddingValues(horizontal = 0.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(items = similar) { item: MediaEntity ->
                            SimilarItemCard(
                                media = item,
                                onClick = {
                                    navController.navigate("detail/${item.mediaType ?: "movie"}/${item.id}")
                                }
                            )
                        }
                    }
                }
            }
        }
        }
    }
}

@Composable
fun SimilarItemCard(
    media: MediaEntity,
    onClick: () -> Unit
) {
    val posterUrl = media.posterPath?.let { "https://image.tmdb.org/t/p/w342$it" }
    Column(
        modifier = Modifier
            .width(120.dp)
            .clickable { onClick() }
    ) {
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier
                .height(180.dp)
                .fillMaxWidth()
        ) {
            if (posterUrl != null) {
                Image(
                    painter = rememberAsyncImagePainter(posterUrl),
                    contentDescription = media.title ?: media.name ?: "Similar",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = "No image",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = media.title?.takeIf { it.isNotBlank() } ?: (media.name ?: "-"),
            style = MaterialTheme.typography.bodySmall,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
        Spacer(modifier = Modifier.height(4.dp))
        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Star, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(14.dp))
                Spacer(Modifier.width(4.dp))
                Text(text = String.format("%.1f", media.voteAverage ?: 0.0), style = MaterialTheme.typography.labelSmall)
            }
            val year = (media.releaseDate ?: media.firstAirDate)?.take(4) ?: ""
            if (year.isNotEmpty()) {
                Text(text = year, style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}

@Composable
fun KeyValueItem(title: String, value: String?, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = value ?: "-",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun DetailInfoCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    Surface(
        tonalElevation = 1.dp,
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.width(8.dp))
                Column {
                    Text(text = title, style = MaterialTheme.typography.bodyMedium)
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun AboutMovieCard(details: MediaEntity) {
    Surface(
        tonalElevation = 2.dp,
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    color = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = "IMDb",
                        color = MaterialTheme.colorScheme.onPrimary,
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.padding(
                            horizontal = 8.dp,
                            vertical = 4.dp
                        )
                    )
                }
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "About movie",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                KeyValueItem(
                    "Status",
                    details.status,
                    modifier = Modifier.weight(1f)
                )
                KeyValueItem(
                    "Original language",
                    details.spokenLanguages?.firstOrNull(),
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                KeyValueItem(
                    "Budget",
                    details.budget?.takeIf { it > 0 }?.let { "$it" },
                    modifier = Modifier.weight(1f)
                )
                KeyValueItem(
                    "Revenue",
                    details.revenue?.takeIf { it > 0 }?.let { "$it" },
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                KeyValueItem(
                    "Production country",
                    details.productionCountries?.firstOrNull(),
                    modifier = Modifier.weight(1f)
                )
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Production companies",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(4.dp))
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        (details.productionCompanies ?: emptyList()).take(6)
                            .forEach { comp ->
                                AssistChip(onClick = {}, label = { Text(comp) })
                            }
                        if ((details.productionCompanies?.size ?: 0) == 0) {
                            Text(
                                "-",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun ExpandableOverview(text: String) {
    var expanded by remember { mutableStateOf(false) }
    Surface(
        tonalElevation = 2.dp,
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Overview",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                IconButton(onClick = { expanded = !expanded }) {
                    Icon(
                        imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = if (expanded) "Collapse" else "Expand"
                    )
                }
            }
            Spacer(Modifier.height(6.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = if (expanded) Int.MAX_VALUE else 4,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun MovieDetailsContent(
    details: MediaEntity,
    navController: NavController,
    favoritesViewModel: FavoritesViewModel,
    moviehistoryViewModel: MoviesHistoryViewModel,
    similar: List<MediaEntity>,
    recommendations: List<MediaEntity>,
    providers: com.example.mda.data.repository.MovieDetailsRepository.ProvidersGrouped?,
    reviews: List<com.example.mda.data.remote.model.ReviewItem>,
    keywords: List<com.example.mda.data.remote.model.KeywordItem>
) {
    LaunchedEffect(details.id) {
        moviehistoryViewModel.saveViewedMovie(
            MoviesViewedEntitty(
                id = details.id,
                name = details.title,
                posterPath = details.posterPath,
                backdropPath = details.backdropPath,
                mediaType = details.mediaType
            )
        )
    }
    val scroll = rememberScrollState()
    val bgUrl =
        "https://image.tmdb.org/t/p/original${details.backdropPath ?: details.posterPath ?: ""}"
    val context = LocalContext.current
    var isDarkBackdrop by remember(bgUrl) { mutableStateOf<Boolean?>(null) }
    LaunchedEffect(bgUrl) {
        if (bgUrl.isNotBlank()) {
            val loader: ImageLoader = ImageLoader(context)
            val req = ImageRequest.Builder(context)
                .data(bgUrl)
                .allowHardware(false)
                .build()
            val res = loader.execute(req)
            if (res is SuccessResult) {
                val bmp = (res.drawable as? android.graphics.drawable.BitmapDrawable)?.bitmap
                if (bmp != null && !bmp.isRecycled) {
                    val pal = Palette.from(bmp).clearFilters().generate()
                    val color = pal.getDominantColor(0xFF444444.toInt())
                    isDarkBackdrop = ColorUtils.calculateLuminance(color) < 0.5
                }
            }
        }
    }
    Box(modifier = Modifier.fillMaxSize()) {
        // Persistent background image
        Image(
            painter = rememberAsyncImagePainter(bgUrl),
            contentDescription = null,
            modifier = Modifier.matchParentSize(),
            contentScale = ContentScale.Crop
        )
        // Global scrim from top to improve readability for content
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0x99000000),
                            Color(0x33000000),
                            MaterialTheme.colorScheme.background // fade to background at bottom
                        )
                    )
                )
        )

        // Foreground scrollable content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scroll)
        ) {
            Spacer(Modifier.height(140.dp))
            // Title row with Poster thumbnail and Favorite button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val headerTextColor = when (isDarkBackdrop) {
                    true -> Color.White
                    false -> Color.Black
                    else -> MaterialTheme.colorScheme.onBackground
                }
                val chipBg = when (isDarkBackdrop) {
                    true -> Color(0x40FFFFFF)
                    false -> Color(0x40000000)
                    else -> MaterialTheme.colorScheme.surfaceVariant
                }
                val chipFg = when (isDarkBackdrop) {
                    true -> Color.White.copy(alpha = 0.9f)
                    false -> Color.Black.copy(alpha = 0.9f)
                    else -> MaterialTheme.colorScheme.onSurfaceVariant
                }
                val thumbUrl = details.posterPath?.let { "https://image.tmdb.org/t/p/w185$it" }
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Box(
                        modifier = Modifier
                            .height(96.dp)
                            .width(72.dp)
                    ) {
                        if (thumbUrl != null) {
                            Image(
                                painter = rememberAsyncImagePainter(thumbUrl),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                }

                Spacer(Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = details.title ?: details.name ?: "Unknown",
                        color = headerTextColor,
                        style = MaterialTheme.typography.titleLarge,
                    )
                    Spacer(Modifier.height(6.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Surface(
                            color = chipBg,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Outlined.CalendarToday,
                                    contentDescription = null,
                                    tint = chipFg,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(Modifier.width(6.dp))
                                Text(
                                    text = details.releaseDate ?: details.firstAirDate ?: "-",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = chipFg
                                )
                            }
                        }
                        details.runtime?.let {
                            Surface(
                                color = chipBg,
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(
                                        horizontal = 10.dp,
                                        vertical = 6.dp
                                    ), verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Default.Timer,
                                        contentDescription = null,
                                        tint = chipFg,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(Modifier.width(6.dp))
                                    Text(
                                        text = "${it} min",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = chipFg
                                    )
                                }
                            }
                        }
                        Surface(
                            color = chipBg,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.Star,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(Modifier.width(6.dp))
                                val avg = String.format("%.1f", details.voteAverage ?: 0.0)
                                val count = details.voteCount?.toString() ?: "0"
                                Text(
                                    text = "$avg | $count",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = chipFg
                                )
                            }
                        }
                    }
                }

                // Convert MediaEntity to Movie for FavoriteButton
                val movie = Movie(
                    id = details.id,
                    title = details.title,
                    name = details.name,
                    overview = details.overview,
                    posterPath = details.posterPath,
                    backdropPath = details.backdropPath,
                    releaseDate = details.releaseDate,
                    firstAirDate = details.firstAirDate,
                    voteAverage = details.voteAverage ?: 0.0,
                    mediaType = details.mediaType,
                    adult = details.adult,
                    genreIds = details.genreIds
                )

                FavoriteButton(
                    movie = movie,
                    viewModel = favoritesViewModel,
                    showBackground = false,
                    modifier = Modifier.size(48.dp)
                )
            }
            Spacer(Modifier.height(12.dp))

            // ✅ عرض معلومات إضافية (Runtime, Status, Budget, Revenue, Votes)
            Row(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                val rt = details.runtime
                if (rt != null) Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "${rt} min",
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
                val st = details.status
                if (st != null) Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = st,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
                val vc = details.voteCount
                if (vc != null) Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "${vc} votes",
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
                val b = details.budget
                if (b != null && b > 0) Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "Budget: $b$",
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
                val r = details.revenue
                if (r != null && r > 0) Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "Revenue: $r$",
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }

            Spacer(Modifier.height(12.dp))
            Divider(
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
                thickness = 1.dp,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Spacer(Modifier.height(12.dp))

            // Tagline
            details.tagline?.let { tagline ->
                if (tagline.isNotEmpty()) {
                    Text(
                        text = "\"$tagline\"",
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    Spacer(Modifier.height(8.dp))
                }
            }
            ExpandableOverview(text = details.overview ?: "No overview available")
            Spacer(Modifier.height(16.dp))

            // ✅ روابط وتعريفات إضافية
            Row(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                details.imdbId?.let { imdb ->
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "IMDB: $imdb",
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
                details.homepage?.let { site ->
                    if (site.isNotEmpty()) Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = site,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }
            Spacer(Modifier.height(12.dp))

            // ✅ عرض genres (مع التفاف تلقائي وتقليل العدد للهواتف)
            val genres = details.genres ?: emptyList()
            if (genres.isNotEmpty()) {
                FlowRow(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    genres.take(8).forEach { genre: String ->
                        AssistChip(onClick = {}, label = { Text(genre) })
                    }
                }
            }
            Spacer(Modifier.height(16.dp))

            AboutMovieCard(details)
            Spacer(Modifier.height(16.dp))

            MediaTabs(details)
            Spacer(Modifier.height(16.dp))

            // ✅ لغات وشركات ودول الإنتاج (ضمن خلفية منفصلة)
            val languages = details.spokenLanguages ?: emptyList()
            val companies = details.productionCompanies ?: emptyList()
            val countries = details.productionCountries ?: emptyList()
            if (languages.isNotEmpty() || companies.isNotEmpty() || countries.isNotEmpty()) {
                Surface(
                    tonalElevation = 2.dp,
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surface,
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "Production",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(Modifier.height(8.dp))
                        if (languages.isNotEmpty()) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                languages.take(6).forEach { lang: String -> AssistChip(onClick = {}, label = { Text(lang) }) }
                            }
                            Spacer(Modifier.height(8.dp))
                        }
                        if (companies.isNotEmpty()) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                companies.take(6).forEach { comp: String -> AssistChip(onClick = {}, label = { Text(comp) }) }
                            }
                            Spacer(Modifier.height(8.dp))
                        }
                        if (countries.isNotEmpty()) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                countries.take(6).forEach { c: String -> AssistChip(onClick = {}, label = { Text(c) }) }
                            }
                        }
                    }
                }
            }

            // ✅ عرض Cast (الممثلين)
            val cast = details.cast
            if (!cast.isNullOrEmpty()) {
                Text(
                    text = "Top Billed Cast",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Spacer(Modifier.height(12.dp))
                androidx.compose.foundation.lazy.LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(items = cast) { castMember: com.example.mda.data.local.entities.Cast ->
                        CastItem(
                            cast = castMember,
                            onClick = { actorId ->
                                navController.navigate("ActorDetails/$actorId")
                            }
                        )
                    }
                }
                Spacer(Modifier.height(12.dp))
                Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
                    OutlinedButton(
                        onClick = { /* TODO: navigate to full cast if available */ },
                        shape = RoundedCornerShape(24.dp)
                    ) {
                        Text("Full Cast & Crew")
                    }
                }
                Spacer(Modifier.height(24.dp))
            }

            // JustWatch availability with provider logos and link
            providers?.let { pg ->
                val uri = LocalUriHandler.current
                Surface(
                    tonalElevation = 2.dp,
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surface,
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Available on JustWatch",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Spacer(Modifier.width(8.dp))
                            AssistChip(
                                onClick = { pg.link?.let { uri.openUri(it) } },
                                label = { Text("US") })
                        }
                        Spacer(Modifier.height(12.dp))
                        ProviderLogosRow(
                            title = "Buy",
                            logos = pg.buy,
                            onOpen = { pg.link?.let { uri.openUri(it) } })
                        if (pg.rent.isNotEmpty()) {
                            Spacer(Modifier.height(8.dp))
                            ProviderLogosRow(
                                title = "Rent",
                                logos = pg.rent,
                                onOpen = { pg.link?.let { uri.openUri(it) } })
                        }
                        if (pg.stream.isNotEmpty()) {
                            Spacer(Modifier.height(8.dp))
                            ProviderLogosRow(
                                title = "Stream",
                                logos = pg.stream,
                                onOpen = { pg.link?.let { uri.openUri(it) } })
                        }
                    }
                }
                Spacer(Modifier.height(16.dp))
            }

            // Release dates (show only if we have at least one date)
            if (!details.releaseDate.isNullOrBlank() || !details.firstAirDate.isNullOrBlank()) Surface(
                tonalElevation = 2.dp,
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "Release dates", style = MaterialTheme.typography.titleMedium)
                        Spacer(Modifier.width(8.dp))
                        AssistChip(onClick = {}, label = { Text("US") })
                    }
                    Spacer(Modifier.height(12.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        DetailInfoCard(
                            Icons.Outlined.CalendarToday,
                            details.releaseDate ?: details.firstAirDate ?: "-",
                            "Theatrical",
                            Modifier.weight(1f)
                        )
                        DetailInfoCard(
                            Icons.Outlined.CalendarToday,
                            "-",
                            "Digital",
                            Modifier.weight(1f)
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        DetailInfoCard(
                            Icons.Outlined.CalendarToday,
                            "-",
                            "Stream",
                            Modifier.weight(1f)
                        )
                        DetailInfoCard(Icons.Outlined.CalendarToday, "-", "-", Modifier.weight(1f))
                    }
                    Spacer(Modifier.height(12.dp))
                    Row(modifier = Modifier.fillMaxWidth()) {
                        OutlinedButton(
                            onClick = { /* TODO open more releases */ },
                            shape = RoundedCornerShape(24.dp)
                        ) { Text("More") }
                    }
                }
            }
            Spacer(Modifier.height(16.dp))

            RecommendationsSimilarTabs(
                recommendations = recommendations,
                similar = similar,
                navController = navController
            )
            Spacer(Modifier.height(16.dp))

            // Reviews section
            if (reviews.isNotEmpty()) {
                Spacer(Modifier.height(16.dp))
                Text(
                    text = "Reviews",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Spacer(Modifier.height(8.dp))
                reviews.take(1).forEach { r: com.example.mda.data.remote.model.ReviewItem ->
                    Surface(
                        tonalElevation = 2.dp,
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.surface,
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .fillMaxWidth()
                    ) {
                        Column(Modifier.padding(12.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(shape = RoundedCornerShape(99.dp), color = MaterialTheme.colorScheme.surfaceVariant) {
                                    Box(Modifier.size(40.dp)) {}
                                }
                                Spacer(Modifier.width(8.dp))
                                Text(text = "A review by ${r.author ?: "-"}")
                                r.authorDetails?.rating?.let { rt ->
                                    Spacer(Modifier.width(6.dp))
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Star, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(14.dp))
                                        Spacer(Modifier.width(4.dp))
                                        Text(String.format("%.1f", rt))
                                    }
                                }
                            }
                            Spacer(Modifier.height(8.dp))
                            ExpandableText(text = r.content ?: "")
                        }
                    }
                }
            }

            // Keywords section (ضمن خلفية منفصلة)
            if (keywords.isNotEmpty()) {
                Spacer(Modifier.height(16.dp))
                Surface(
                    tonalElevation = 2.dp,
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surface,
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "Keywords",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(Modifier.height(8.dp))
                        FlowRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            keywords.take(12).forEach { k: com.example.mda.data.remote.model.KeywordItem ->
                                AssistChip(onClick = {}, label = { Text(k.name) })
                            }
                        }
                    }
                }
            }
            Spacer(Modifier.height(40.dp))
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieDetailsScreen(
    id: Int,
    isTvShow: Boolean = false,
    navController: NavController,
    repository: MovieDetailsRepository,
    onTopBarStateChange: (TopBarState) -> Unit,
    favoritesViewModel: FavoritesViewModel,
    moviehistoryViewModel: MoviesHistoryViewModel
) {
    val viewModel: MovieDetailsViewModel = viewModel(factory = MovieDetailsViewModelFactory(repository))
    val scope = rememberCoroutineScope()
    val isLoading by viewModel.isLoading.collectAsState()
    val refreshState = rememberSwipeRefreshState(isRefreshing = isLoading)

    val details by viewModel.details.collectAsState()
    val error by viewModel.error.collectAsState()
    val similar by viewModel.similar.collectAsState()
    val recommendations by viewModel.recommendations.collectAsState()
    val providers by viewModel.providers.collectAsState()
    val reviews by viewModel.reviews.collectAsState()
    val keywords by viewModel.keywords.collectAsState()


    // Load from cache first
    LaunchedEffect(id, isTvShow) {
        scope.launch {
            if (isTvShow) viewModel.loadTvDetails(id)
            else viewModel.loadMovieDetails(id)
        }
    }

    LaunchedEffect(details) {
        details?.let {
            Log.d("MovieDetailScreen", "✅ Details loaded: ${it.title}")
            Log.d("MovieDetailScreen", "🎭 Cast: ${it.cast?.size ?: 0} members")
            Log.d("MovieDetailScreen", "🎬 Videos: ${it.videos?.size ?: 0} videos")
        }
    }
    LaunchedEffect(details) {
        onTopBarStateChange(
            TopBarState(
                // العنوان هو اسم الفيلم، أو فارغ أثناء التحميل
                title = details?.title ?: details?.name ?: "",
                // إضافة أيقونة الرجوع
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack, // استخدام الأيقونة الموحدة
                            contentDescription = "Back"
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
                if (isTvShow) viewModel.loadTvDetails(id, fromNetwork = true)
                else viewModel.loadMovieDetails(id, fromNetwork = true)
            }
        }
    ) {

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)

        ) {
            when {
                isLoading -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }

                error != null -> Text(
                    text = "Error: $error",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.Center)
                )

                details != null -> AnimatedVisibility(visible = true, enter = fadeIn()) {
                    MovieDetailsContent(
                        details = details!!,
                        navController = navController,
                        favoritesViewModel = favoritesViewModel,
                        moviehistoryViewModel = moviehistoryViewModel,
                        similar = similar,
                        recommendations = recommendations,
                        providers = providers,
                        reviews = reviews?.results ?: emptyList(),
                        keywords = keywords?.all() ?: emptyList()
                    )
                }

                else -> Text(
                    "No details available",
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}

@Composable
fun ProviderLogosRow(
    title: String,
    logos: List<com.example.mda.data.repository.MovieDetailsRepository.ProviderLogo>,
    onOpen: () -> Unit
) {
    if (logos.isEmpty()) return
    Column {
        Text(text = title, style = MaterialTheme.typography.titleSmall)
        Spacer(Modifier.height(6.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            logos.take(10).forEach { lp ->
                val logoUrl = lp.logoPath?.let { "https://image.tmdb.org/t/p/w185$it" }
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier
                        .size(40.dp)
                        .clickable { onOpen() }
                ) {
                    if (logoUrl != null) {
                        Image(
                            painter = rememberAsyncImagePainter(logoUrl),
                            contentDescription = lp.name,
                            contentScale = ContentScale.Fit
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun MediaTabs(details: MediaEntity) {
    var selected by remember { mutableStateOf(0) }
    Surface(
        tonalElevation = 2.dp,
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            val vCount = details.videos?.size ?: 0
            val pCount = details.posters?.size ?: 0
            val bCount = details.backdrops?.size ?: 0
            TabRow(
                selectedTabIndex = selected,
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                contentColor = MaterialTheme.colorScheme.onSurfaceVariant
            ) {
                Tab(
                    selected = selected == 0,
                    onClick = { selected = 0 },
                    selectedContentColor = MaterialTheme.colorScheme.primary,
                    unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    text = { Text("Videos $vCount", style = MaterialTheme.typography.titleSmall) }
                )
                Tab(
                    selected = selected == 1,
                    onClick = { selected = 1 },
                    selectedContentColor = MaterialTheme.colorScheme.primary,
                    unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    text = { Text("Posters $pCount", style = MaterialTheme.typography.titleSmall) }
                )
                Tab(
                    selected = selected == 2,
                    onClick = { selected = 2 },
                    selectedContentColor = MaterialTheme.colorScheme.primary,
                    unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    text = { Text("Backdrops $bCount", style = MaterialTheme.typography.titleSmall) }
                )
            }
        when (selected) {
            0 -> {
                val videos = details.videos ?: emptyList()
                if (videos.isEmpty()) {
                    Text(
                        text = "No videos",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(16.dp)
                    )
                } else {
                    Spacer(Modifier.height(12.dp))
                    androidx.compose.foundation.lazy.LazyRow(
                        contentPadding = PaddingValues(horizontal = 0.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(items = videos) { video: com.example.mda.data.local.entities.Video ->
                            VideoThumbnail(video)
                        }
                    }
                }
            }

            1 -> {
                val posters = details.posters ?: emptyList()
                if (posters.isEmpty()) {
                    Text(
                        text = "No posters",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(16.dp)
                    )
                } else {
                    Spacer(Modifier.height(12.dp))
                    androidx.compose.foundation.lazy.LazyRow(
                        contentPadding = PaddingValues(horizontal = 0.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(items = posters) { path: String ->
                            val url = "https://image.tmdb.org/t/p/w500$path"
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier
                                    .height(220.dp)
                                    .width(150.dp)
                            ) {
                                Image(
                                    painter = rememberAsyncImagePainter(url),
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        }
                    }
                }
            }

            2 -> {
                val backdrops = details.backdrops ?: emptyList()
                if (backdrops.isEmpty()) {
                    Text(
                        text = "No backdrops",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(16.dp)
                    )
                } else {
                    Spacer(Modifier.height(12.dp))
                    androidx.compose.foundation.lazy.LazyRow(
                        contentPadding = PaddingValues(horizontal = 0.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(items = backdrops) { path: String ->
                            val url = "https://image.tmdb.org/t/p/w780$path"
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier
                                    .height(180.dp)
                                    .width(300.dp)
                            ) {
                                Image(
                                    painter = rememberAsyncImagePainter(url),
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
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

