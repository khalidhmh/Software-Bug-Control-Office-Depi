package com.example.mda.ui.screens.movieDetail

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowBackIos
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.mda.data.local.entities.MediaEntity
import com.example.mda.data.repository.MovieDetailsRepository
import com.example.mda.data.repository.MoviesRepository
import com.example.mda.ui.navigation.TopBarState
import com.example.mda.ui.screens.movieDetail.components.CastItem
import com.example.mda.ui.screens.movieDetail.components.VideoThumbnail
import com.example.mda.ui.screens.favorites.FavoritesViewModel
import com.example.mda.ui.screens.favorites.components.FavoriteButton
import com.example.mda.data.remote.model.Movie
import com.google.accompanist.swiperefresh.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieDetailsScreen(
    id: Int,
    isTvShow: Boolean = false,
    navController: NavController,
    repository: MovieDetailsRepository,
    onTopBarStateChange: (TopBarState) -> Unit,
    favoritesViewModel: FavoritesViewModel
) {
    val viewModel: MovieDetailsViewModel = viewModel(factory = MovieDetailsViewModelFactory(repository))
    val scope = rememberCoroutineScope()
    var refreshing by remember { mutableStateOf(false) }
    val refreshState = rememberSwipeRefreshState(isRefreshing = refreshing)

    val details by viewModel.details.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()


    // Load from cache first
    LaunchedEffect(id, isTvShow) {
        Log.d("MovieDetailScreen", "📱 Loading details for ID: $id, isTvShow: $isTvShow")
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
                refreshing = true
                scope.launch {
                    if (isTvShow) viewModel.loadTvDetails(id, fromNetwork = true)
                    else viewModel.loadMovieDetails(id, fromNetwork = true)
                }
                refreshing = false
            }
        ) {

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)

            ) {
                when {
                    isLoading -> Box(Modifier.fillMaxSize(), Alignment.Center) {
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
                            favoritesViewModel = favoritesViewModel
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
private fun MovieDetailsContent(
    details: MediaEntity,
    navController: NavController,
    favoritesViewModel: FavoritesViewModel
) {
    val scroll = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scroll)
    ) {
        val img = "https://image.tmdb.org/t/p/original${details.backdropPath ?: details.posterPath ?: ""}"

        Image(
            painter = rememberAsyncImagePainter(img),
            contentDescription = details.title ?: details.name,
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp),
            contentScale = ContentScale.Crop
        )

        Spacer(Modifier.height(12.dp))
        
        // Title and Favorite Button Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = details.title ?: details.name ?: "Unknown",
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.weight(1f)
            )
            
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
                modifier = Modifier.size(56.dp)
            )
        }
        
        Spacer(Modifier.height(6.dp))
        Text(
            text = "${details.voteAverage ?: 0.0} ⭐   ${details.releaseDate ?: details.firstAirDate ?: ""}",
            color = MaterialTheme.colorScheme.secondary,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Spacer(Modifier.height(12.dp))
        
        // ✅ عرض معلومات إضافية (Runtime, Status, Budget, Revenue, Votes)
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            details.runtime?.let { runtime ->
                SurfaceChip(text = "${runtime} min")
            }
            details.status?.let { status ->
                SurfaceChip(text = status)
            }
            details.voteCount?.let { count ->
                SurfaceChip(text = "${count} votes")
            }
            // 🆕 Budget & Revenue
            details.budget?.let { b -> if (b > 0) SurfaceChip(text = "Budget: $b$") }
            details.revenue?.let { r -> if (r > 0) SurfaceChip(text = "Revenue: $r$") }
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
        
        Text(
            text = details.overview ?: "No overview available",
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.9f),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Spacer(Modifier.height(16.dp))

        // ✅ روابط وتعريفات إضافية
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            details.imdbId?.let { imdb ->
                SurfaceChip(text = "IMDB: $imdb")
            }
            details.homepage?.let { site ->
                if (site.isNotEmpty()) SurfaceChip(text = site)
            }
        }
        Spacer(Modifier.height(12.dp))

        // ✅ عرض genres
        val genres = details.genres ?: emptyList()
        if (genres.isNotEmpty()) {
            Row(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                genres.forEach { genre ->
                    AssistChip(onClick = {}, label = { Text(genre) })
                }
            }
        }
        Spacer(Modifier.height(16.dp))

        // ✅ لغات وشركات ودول الإنتاج
        val languages = details.spokenLanguages ?: emptyList()
        val companies = details.productionCompanies ?: emptyList()
        val countries = details.productionCountries ?: emptyList()
        if (languages.isNotEmpty() || companies.isNotEmpty() || countries.isNotEmpty()) {
            Text(
                text = "Production",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Spacer(Modifier.height(8.dp))
            if (languages.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    languages.take(6).forEach { lang -> AssistChip(onClick = {}, label = { Text(lang) }) }
                }
                Spacer(Modifier.height(8.dp))
            }
            if (companies.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    companies.take(6).forEach { comp -> AssistChip(onClick = {}, label = { Text(comp) }) }
                }
                Spacer(Modifier.height(8.dp))
            }
            if (countries.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    countries.take(6).forEach { c -> AssistChip(onClick = {}, label = { Text(c) }) }
                }
                Spacer(Modifier.height(16.dp))
            }
        }

        // ✅ عرض Cast (الممثلين)
        val cast = details.cast
        if (!cast.isNullOrEmpty()) {
            Text(
                text = "Cast",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Spacer(Modifier.height(12.dp))
            androidx.compose.foundation.lazy.LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(cast) { castMember ->
                    CastItem(
                        cast = castMember,
                        onClick = { actorId ->
                            navController.navigate("ActorDetails/$actorId")
                        }
                    )
                }
            }
            Spacer(Modifier.height(24.dp))
        }

        // ✅ عرض Videos (التريلرات)
        val videos = details.videos
        if (!videos.isNullOrEmpty()) {
            Text(
                text = "Videos & Trailers",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Spacer(Modifier.height(12.dp))
            androidx.compose.foundation.lazy.LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(videos) { video ->
                    VideoThumbnail(video)
                }
            }
            Spacer(Modifier.height(24.dp))
        }

        Spacer(Modifier.height(40.dp))
    }
}

@Composable
private fun SurfaceChip(text: String) {
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
