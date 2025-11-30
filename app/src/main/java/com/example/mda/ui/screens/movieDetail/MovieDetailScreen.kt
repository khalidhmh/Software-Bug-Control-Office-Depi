@file:OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)

package com.example.mda.ui.screens.movieDetail

import android.graphics.drawable.BitmapDrawable
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.core.graphics.ColorUtils
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.palette.graphics.Palette
import coil.ImageLoader
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.example.mda.data.local.entities.Cast
import com.example.mda.data.local.entities.MediaEntity
import com.example.mda.data.local.entities.MoviesViewedEntitty
import com.example.mda.data.local.entities.Video
import com.example.mda.data.remote.model.KeywordItem
import com.example.mda.data.remote.model.Movie
import com.example.mda.data.remote.model.ReviewItem
import com.example.mda.data.repository.MovieDetailsRepository
import com.example.mda.localization.LocalizationKeys
import com.example.mda.localization.localizedString
import com.example.mda.ui.navigation.TopBarState
import com.example.mda.ui.screens.auth.AuthViewModel
import com.example.mda.ui.screens.favorites.FavoritesViewModel
import com.example.mda.ui.screens.favorites.components.FavoriteButton
import com.example.mda.ui.screens.movieDetail.components.CastItem
import com.example.mda.ui.screens.movieDetail.components.VideoThumbnail
import com.example.mda.ui.screens.profile.history.MoviesHistoryViewModel
import com.example.mda.ui.theme.AppVerticalGradient
import com.example.mda.ui.theme.PrimaryBlue
import com.example.mda.ui.theme.TextSecondaryDark
import com.google.accompanist.swiperefresh.*
import kotlinx.coroutines.launch

// --- Helper Components ---

@Composable
fun StyledChip(
    text: String,
    icon: ImageVector? = null,
    onClick: () -> Unit = {}
) {
    Surface(
        onClick = onClick,
        shape = MaterialTheme.shapes.medium, // Uses Shape.kt (8.dp)
        color = MaterialTheme.colorScheme.surfaceVariant, // Uses DarkSurfaceVariant
        contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.primary // Uses PrimaryBlue
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge, // Uses Type.kt
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
        color = MaterialTheme.colorScheme.primary, // Uses PrimaryBlue
        modifier = Modifier.padding(bottom = 8.dp, top = 4.dp)
    )
}

@Composable
fun ProductionSection(details: MediaEntity) {
    val languages = details.spokenLanguages ?: emptyList()
    val companies = details.productionCompanies ?: emptyList()
    val countries = details.productionCountries ?: emptyList()

    if (languages.isEmpty() && companies.isEmpty() && countries.isEmpty()) return

    Surface(
        tonalElevation = 1.dp, // Subtle elevation
        shape = MaterialTheme.shapes.large, // Uses Shape.kt (16.dp)
        color = MaterialTheme.colorScheme.surface, // Uses DarkSurface
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = localizedString(LocalizationKeys.DETAIL_PRODUCTION_DETAILS),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(Modifier.height(12.dp))

            // 1. Spoken Languages
            if (languages.isNotEmpty()) {
                SectionHeader(title = localizedString(LocalizationKeys.DETAIL_SPOKEN_LANGUAGES))

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    languages.take(6).forEach { lang ->
                        StyledChip(text = lang, icon = Icons.Default.Language)
                    }
                }
                Spacer(Modifier.height(16.dp))
            }

            // 2. Countries
            if (countries.isNotEmpty()) {
                SectionHeader(title = localizedString(LocalizationKeys.DETAIL_PRODUCTION_COUNTRIES))

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    countries.take(6).forEach { country ->
                        StyledChip(text = country, icon = Icons.Default.Public)
                    }
                }
                Spacer(Modifier.height(16.dp))
            }

            // 3. Companies
            if (companies.isNotEmpty()) {
                SectionHeader(title = localizedString(LocalizationKeys.DETAIL_COMPANIES))

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    companies.take(8).forEach { comp ->
                        StyledChip(text = comp, icon = Icons.Default.Business)
                    }
                }
            }
        }
    }
}

// --- UPDATED Genres Section (Matching Keywords Style) ---
@Composable
fun GenresSection(
    genres: List<Pair<Int, String>>,
    navController: NavController
) {
    if (genres.isEmpty()) return

    Surface(
        tonalElevation = 1.dp,
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surface,
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Category,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = localizedString(LocalizationKeys.DETAIL_GENRES),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Spacer(Modifier.height(12.dp))
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                genres.forEach { (id, name) ->
                    // Uses the same style as KeywordsSection (Circle, Border, Transparent-ish bg)
                    Surface(
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                        shape = CircleShape,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
                        modifier = Modifier
                            .clickable {
                                if (id != -1) {
                                    navController.navigate("genre_details/$id/$name")
                                }
                            }
                    ) {
                        Text(
                            text = name,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun KeywordsSection(keywords: List<KeywordItem>) {
    if (keywords.isEmpty()) return

    Surface(
        tonalElevation = 1.dp,
        shape = MaterialTheme.shapes.large, // Uses Shape.kt (16.dp)
        color = MaterialTheme.colorScheme.surface, // Uses DarkSurface
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Tag,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = localizedString(LocalizationKeys.DETAIL_KEYWORDS),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Spacer(Modifier.height(12.dp))

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                keywords.take(15).forEach { k ->
                    // Keyword Tag
                    Surface(
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f), // Uses DarkContainer
                        shape = CircleShape,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
                        modifier = Modifier
                            .padding(bottom = 8.dp)
                            .clickable { /* Handle click */ }
                    ) {
                        Text(
                            text = "#${k.name}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }
    }
}

// --- Rest of the Screen ---

@Composable
fun ExpandableText(
    text: String,
    collapsedLines: Int = 6
) {
    var expanded by remember { mutableStateOf(false) }
    Column {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium, // Uses Type.kt
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = if (expanded) Int.MAX_VALUE else collapsedLines,
            overflow = TextOverflow.Ellipsis
        )
        Spacer(Modifier.height(6.dp))
        TextButton(onClick = { expanded = !expanded }) {
            Text(if (expanded) localizedString(LocalizationKeys.COMMON_SHOW_LESS) else localizedString(LocalizationKeys.COMMON_SHOW_MORE), color = MaterialTheme.colorScheme.primary)
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
        tonalElevation = 1.dp,
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surface,
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = localizedString(LocalizationKeys.DETAIL_DISCOVER),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(Modifier.height(8.dp))
            TabRow(
                selectedTabIndex = selected,
                containerColor = MaterialTheme.colorScheme.surfaceVariant, // DarkSurfaceVariant
                contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selected]),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            ) {
                Tab(
                    selected = selected == 0,
                    onClick = { selected = 0 },
                    selectedContentColor = MaterialTheme.colorScheme.primary,
                    unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    text = { Text(localizedString(LocalizationKeys.DETAIL_RECOMMENDATIONS_COUNT, "count", recCount.toString()), style = MaterialTheme.typography.labelLarge) }
                )
                Tab(
                    selected = selected == 1,
                    onClick = { selected = 1 },
                    selectedContentColor = MaterialTheme.colorScheme.primary,
                    unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    text = { Text(localizedString(LocalizationKeys.DETAIL_SIMILAR_COUNT, "count", simCount.toString()), style = MaterialTheme.typography.labelLarge) }
                )
            }
            Spacer(Modifier.height(12.dp))

            val list = if (selected == 0) recommendations else similar
            val emptyMsg = if (selected == 0) localizedString(LocalizationKeys.DETAIL_NO_RECOMMENDATIONS) else localizedString(LocalizationKeys.DETAIL_NO_SIMILAR)

            if (list.isEmpty()) {
                Text(
                    text = emptyMsg,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            } else {
                androidx.compose.foundation.lazy.LazyRow(
                    contentPadding = PaddingValues(horizontal = 0.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(items = list) { item: MediaEntity ->
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
            shape = MaterialTheme.shapes.medium, // Shape.kt
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
                        text = localizedString(LocalizationKeys.COMMON_NO_IMAGE),
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
            overflow = TextOverflow.Ellipsis,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(4.dp))
        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.Star,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.tertiary, // Uses RatingYellow from Theme
                    modifier = Modifier.size(14.dp)
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    text = String.format("%.1f", media.voteAverage ?: 0.0),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            val year = (media.releaseDate ?: media.firstAirDate)?.take(4) ?: ""
            if (year.isNotEmpty()) {
                Text(text = year, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
fun ExpandableOverview(text: String) {
    var expanded by remember { mutableStateOf(false) }
    Surface(
        tonalElevation = 1.dp,
        shape = MaterialTheme.shapes.large,
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
                    text = localizedString(LocalizationKeys.DETAIL_OVERVIEW),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                IconButton(onClick = { expanded = !expanded }) {
                    Icon(
                        imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = if (expanded) localizedString(LocalizationKeys.COMMON_COLLAPSE) else localizedString(LocalizationKeys.COMMON_EXPAND),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
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
fun AboutMovieCard(details: MediaEntity) {
    Surface(
        tonalElevation = 1.dp,
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surface,
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    color = MaterialTheme.colorScheme.primary,
                    shape = MaterialTheme.shapes.small
                ) {
                    Text(
                        text = "IMDb",
                        color = MaterialTheme.colorScheme.onPrimary,
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
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
                    title = "Status",
                    value = details.status,
                    modifier = Modifier.weight(1f)
                )
                KeyValueItem(
                    title = "Original language",
                    value = details.spokenLanguages?.firstOrNull(),
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                KeyValueItem(
                    title = "Budget",
                    value = details.budget?.takeIf { it > 0 }?.let { "$it" },
                    modifier = Modifier.weight(1f)
                )
                KeyValueItem(
                    title = "Revenue",
                    value = details.revenue?.takeIf { it > 0 }?.let { "$it" },
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                KeyValueItem(
                    title = "Production country",
                    value = details.productionCountries?.firstOrNull(),
                    modifier = Modifier.weight(1f)
                )
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Production companies",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(4.dp))
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        (details.productionCompanies ?: emptyList()).take(6)
                            .forEach { comp ->
                                StyledChip(text = comp)
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
fun MovieDetailsContent(
    details: MediaEntity,
    navController: NavController,
    favoritesViewModel: FavoritesViewModel,
    moviehistoryViewModel: MoviesHistoryViewModel,
    similar: List<MediaEntity>,
    recommendations: List<MediaEntity>,
    providers: MovieDetailsRepository.ProvidersGrouped?,
    reviews: List<ReviewItem>,
    keywords: List<KeywordItem>,
    isAuthenticated: Boolean
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
    val bgUrl = "https://image.tmdb.org/t/p/original${details.backdropPath ?: details.posterPath ?: ""}"
    val context = LocalContext.current
    var isDarkBackdrop by remember(bgUrl) { mutableStateOf<Boolean?>(null) }
    val isDarkTheme = isSystemInDarkTheme()

    LaunchedEffect(bgUrl) {
        if (bgUrl.isNotBlank()) {
            val loader = ImageLoader(context)
            val req = ImageRequest.Builder(context)
                .data(bgUrl)
                .allowHardware(false)
                .build()
            val res = loader.execute(req)
            if (res is SuccessResult) {
                val bmp = (res.drawable as? BitmapDrawable)?.bitmap
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
        // Global scrim
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0x99000000),
                            Color(0x66000000),
                            MaterialTheme.colorScheme.background // Matches DarkBackground
                        )
                    )
                )
        )
        // In light theme overlay
        if (!isDarkTheme) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(Color.White.copy(alpha = 0.12f))
            )
        }

        // Top overlay actions
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 12.dp, vertical = 12.dp)
                .zIndex(1f),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(shape = CircleShape, color = Color.Black.copy(alpha = 0.35f)) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = localizedString(LocalizationKeys.COMMON_BACK),
                        tint = Color.White
                    )
                }
            }
        }

        // Foreground scrollable content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scroll)
        ) {
            val topSpacer = if (LocalConfiguration.current.screenHeightDp < 700) 64.dp else 100.dp
            Spacer(Modifier.height(topSpacer))
            // Title row
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

                val thumbUrl = details.posterPath?.let { "https://image.tmdb.org/t/p/w185$it" }
                Surface(
                    shape = MaterialTheme.shapes.medium,
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shadowElevation = 8.dp
                ) {
                    val sw = LocalConfiguration.current.screenWidthDp
                    val posterH = when {
                        sw < 360 -> 128
                        sw < 400 -> 160
                        else -> 192
                    }.dp
                    val posterW = (posterH * 3) / 4
                    Box(
                        modifier = Modifier
                            .height(posterH)
                            .width(posterW)
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

                    val chipBg = when (isDarkBackdrop) {
                        true -> Color(0x40FFFFFF)
                        false -> Color(0x40000000)
                        else -> MaterialTheme.colorScheme.surfaceVariant
                    }
                    val chipFg = when (isDarkBackdrop) {
                        true -> Color.White
                        false -> Color.Black
                        else -> MaterialTheme.colorScheme.onSurfaceVariant
                    }
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Date
                        Surface(color = chipBg, shape = MaterialTheme.shapes.small) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Outlined.CalendarToday, null, tint = chipFg, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(6.dp))
                                Text(
                                    text = details.releaseDate ?: details.firstAirDate ?: "-",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = chipFg,
                                    maxLines = 1
                                )
                            }
                        }
                        // Runtime
                        details.runtime?.let { rt ->
                            Surface(color = chipBg, shape = MaterialTheme.shapes.small) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.Timer, null, tint = chipFg, modifier = Modifier.size(16.dp))
                                    Spacer(Modifier.width(6.dp))
                                    Text("${rt} min", style = MaterialTheme.typography.bodySmall, color = chipFg, maxLines = 1)
                                }
                            }
                        }
                        // Rating
                        Surface(color = MaterialTheme.colorScheme.primary.copy(alpha = 0.85f), shape = MaterialTheme.shapes.small) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Star, null, tint = MaterialTheme.colorScheme.tertiary, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(6.dp))
                                val avg = String.format("%.1f", details.voteAverage ?: 0.0)
                                val cnt = details.voteCount?.toString() ?: "0"
                                Text(
                                    text = "$avg | $cnt",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.Black,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }

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
                    modifier = Modifier.size(48.dp),
                    isAuthenticated = isAuthenticated,
                    onLoginRequired = { navController.navigate("settings") }
                )
            }
            Spacer(Modifier.height(12.dp))

            HorizontalDivider(
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
                thickness = 1.dp,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Spacer(Modifier.height(12.dp))

            // Tagline
            details.tagline?.let { tagline ->
                if (tagline.isNotEmpty()) {
                    Text(
                        text = "\"$tagline\"",
                        color = MaterialTheme.colorScheme.secondary, // Uses AccentCyan
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontStyle = FontStyle.Italic,
                            fontWeight = FontWeight.Medium
                        ),
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .padding(horizontal = 24.dp)
                            .fillMaxWidth()
                    )
                    Spacer(Modifier.height(12.dp))
                }
            }

            ExpandableOverview(text = details.overview ?: "No overview available")
            Spacer(Modifier.height(16.dp))

            // Links
            Row(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                details.imdbId?.let { imdb ->
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = MaterialTheme.shapes.small
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
                        shape = MaterialTheme.shapes.small
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

            // --- Updated Genres Section ---
            val genresNames = details.genres ?: emptyList()
            val genresIds = details.genreIds ?: emptyList()

            val genrePairs = if (genresNames.size == genresIds.size) {
                genresIds.zip(genresNames)
            } else {
                genresNames.map { -1 to it }
            }

            if (genrePairs.isNotEmpty()) {
                GenresSection(genres = genrePairs, navController = navController)
                Spacer(Modifier.height(16.dp))
            }


            AboutMovieCard(details)
            Spacer(Modifier.height(16.dp))

            MediaTabs(details)
            Spacer(Modifier.height(16.dp))

            // Production Section
            ProductionSection(details)

            Spacer(Modifier.height(16.dp))


            val cast = details.cast
            if (!cast.isNullOrEmpty()) {
                Surface(
                    tonalElevation = 1.dp,
                    shape = MaterialTheme.shapes.large,
                    color = MaterialTheme.colorScheme.surface,
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "Top Billed Cast",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(Modifier.height(12.dp))
                        androidx.compose.foundation.lazy.LazyRow(
                            contentPadding = PaddingValues(horizontal = 0.dp), // Padding handled by container
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(items = cast) { castMember: Cast ->
                                CastItem(
                                    cast = castMember,
                                    onClick = { actorId ->
                                        navController.navigate("ActorDetails/$actorId")
                                    }
                                )
                            }
                        }
                    }
                }
                Spacer(Modifier.height(16.dp))
            }

            // JustWatch
            providers?.let { pg ->
                val hasAny = pg.buy.isNotEmpty() || pg.rent.isNotEmpty() || pg.stream.isNotEmpty()
                if (hasAny) {
                    val uri = LocalUriHandler.current
                    Surface(
                        tonalElevation = 1.dp,
                        shape = MaterialTheme.shapes.large,
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
                            if (pg.buy.isNotEmpty()) {
                                ProviderLogosRow(
                                    title = "Buy",
                                    logos = pg.buy,
                                    onOpen = { pg.link?.let { uri.openUri(it) } })
                            }
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
            }
            Spacer(Modifier.height(16.dp))

            RecommendationsSimilarTabs(
                recommendations = recommendations,
                similar = similar,
                navController = navController
            )
            Spacer(Modifier.height(16.dp))

            // Reviews
            if (reviews.isNotEmpty()) {
                Surface(
                    tonalElevation = 1.dp,
                    shape = MaterialTheme.shapes.large,
                    color = MaterialTheme.colorScheme.surface,
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "Reviews",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                        Spacer(Modifier.height(12.dp))

                        reviews.take(1).forEach { r: ReviewItem ->
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Surface(
                                        shape = CircleShape,
                                        color = MaterialTheme.colorScheme.surfaceVariant
                                    ) {
                                        Box(
                                            modifier = Modifier.size(40.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = r.author?.take(1)?.uppercase() ?: "",
                                                style = MaterialTheme.typography.titleMedium,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                    Spacer(Modifier.width(8.dp))
                                    Text(
                                        text = "A review by ${r.author ?: "-"}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    r.authorDetails?.rating?.let { rt ->
                                        Spacer(Modifier.width(6.dp))
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                Icons.Default.Star,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.tertiary, // RatingYellow
                                                modifier = Modifier.size(14.dp)
                                            )
                                            Spacer(Modifier.width(4.dp))
                                            Text(
                                                text = String.format("%.1f", rt),
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                        }
                                    }
                                }
                                Spacer(Modifier.height(12.dp))
                                ExpandableText(text = r.content ?: "")
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            KeywordsSection(keywords)

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
    moviehistoryViewModel: MoviesHistoryViewModel,
    authViewModel: AuthViewModel
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
    val authUiState by authViewModel.uiState.collectAsState()

    // Load from cache first
    LaunchedEffect(id, isTvShow) {
        scope.launch {
            if (isTvShow) viewModel.loadTvDetails(id)
            else viewModel.loadMovieDetails(id)
        }
    }

    LaunchedEffect(Unit) {
        onTopBarStateChange(TopBarState())
    }

    SwipeRefresh(
        state = rememberSwipeRefreshState(isRefreshing = false),
        onRefresh = { /* Disable refresh */ }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            when {
                // --- UPDATED LOADING STATE ---
                isLoading -> {
                    Box(modifier = Modifier.fillMaxSize()) {
                        // 1. Back Button (So user is not stuck)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .statusBarsPadding() // Handle safe area
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.3f), // Glassmorphism effect
                                contentColor = Color.White, // Forced White for visibility on dark bg
                                modifier = Modifier.size(40.dp)
                            ) {
                                IconButton(onClick = { navController.popBackStack() }) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = "Back",
                                        tint = Color.White // Forced White
                                    )
                                }
                            }
                        }

                        // 2. Centered Loading Content
                        Column(
                            modifier = Modifier.align(Alignment.Center),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            CircularProgressIndicator(
                                color = PrimaryBlue, // Explicitly use PrimaryBlue
                                trackColor = Color.White.copy(alpha = 0.1f), // Light track
                                strokeWidth = 4.dp,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Loading details...",
                                style = MaterialTheme.typography.bodyLarge,
                                color = TextSecondaryDark // Explicit light color
                            )
                        }
                    }
                }

                // --- ERROR STATE (Updated to match theme) ---
                error != null -> {
                    Box(modifier = Modifier.fillMaxSize()) {
                        // Back Button
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .statusBarsPadding()
                                .padding(12.dp)
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.3f),
                                contentColor = Color.White,
                                modifier = Modifier.size(40.dp)
                            ) {
                                IconButton(onClick = { navController.popBackStack() }) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = "Back",
                                        tint = Color.White
                                    )
                                }
                            }
                        }

                        // Error Content
                        Column(
                            modifier = Modifier
                                .align(Alignment.Center)
                                .padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = androidx.compose.material.icons.Icons.Default.Tag, // Or an Error icon if available
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(Modifier.height(16.dp))
                            Text(
                                text = "Unable to load details",
                                style = MaterialTheme.typography.titleMedium,
                                color = Color.White // Force white
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(
                                text = error ?: "Unknown error",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextSecondaryDark, // Force light grey
                                textAlign = TextAlign.Center
                            )
                            Spacer(Modifier.height(24.dp))
                            Button(
                                onClick = {
                                    scope.launch {
                                        if (isTvShow) viewModel.loadTvDetails(id, fromNetwork = true)
                                        else viewModel.loadMovieDetails(id, fromNetwork = true)
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary,
                                    contentColor = MaterialTheme.colorScheme.onPrimary
                                )
                            ) {
                                Text("Retry")
                            }
                        }
                    }
                }

                // --- SUCCESS STATE ---
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
                        keywords = keywords?.keywords ?: emptyList(),
                        isAuthenticated = authUiState.isAuthenticated
                    )
                }

                else -> Text(
                    "No details available",
                    color = TextSecondaryDark,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}

@Composable
fun ProviderLogosRow(
    title: String,
    logos: List<MovieDetailsRepository.ProviderLogo>,
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
                    shape = MaterialTheme.shapes.medium,
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
        tonalElevation = 1.dp,
        shape = MaterialTheme.shapes.large,
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
                contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selected]),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            ) {
                Tab(
                    selected = selected == 0,
                    onClick = { selected = 0 },
                    selectedContentColor = MaterialTheme.colorScheme.primary,
                    unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    text = { Text("Videos $vCount", style = MaterialTheme.typography.labelLarge) }
                )
                Tab(
                    selected = selected == 1,
                    onClick = { selected = 1 },
                    selectedContentColor = MaterialTheme.colorScheme.primary,
                    unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    text = { Text("Posters $pCount", style = MaterialTheme.typography.labelLarge) }
                )
                Tab(
                    selected = selected == 2,
                    onClick = { selected = 2 },
                    selectedContentColor = MaterialTheme.colorScheme.primary,
                    unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    text = { Text("Backdrops $bCount", style = MaterialTheme.typography.labelLarge) }
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
                            items(items = videos) { video: Video ->
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
                                    shape = MaterialTheme.shapes.medium,
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
                                    shape = MaterialTheme.shapes.medium,
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