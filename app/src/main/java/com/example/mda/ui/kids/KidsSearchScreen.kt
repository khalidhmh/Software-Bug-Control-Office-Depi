package com.example.mda.ui.kids

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.mda.data.local.entities.MediaEntity
import com.example.mda.data.remote.model.Movie
import com.example.mda.data.repository.MoviesRepository
import com.example.mda.localization.LocalizationKeys
import com.example.mda.localization.localizedString
import com.example.mda.ui.kids.favorites.KidsFavoriteButton
import com.example.mda.ui.kids.search.KidsSearchFiltersRow
import com.example.mda.ui.kids.search.KidsSearchStore
import com.example.mda.ui.screens.components.MovieCardGridWithFavorite
import com.example.mda.ui.screens.favorites.FavoritesViewModel
import com.example.mda.ui.screens.search.SearchBarComposable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KidsSearchScreen(
    moviesRepository: MoviesRepository,
    favoritesViewModel: FavoritesViewModel,
    onItemClick: (MediaEntity) -> Unit,
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    var query by remember { mutableStateOf("") }
    var results by remember { mutableStateOf<List<MediaEntity>>(emptyList()) }
    var selectedFilter by remember { mutableStateOf("all") }
    val scope = rememberCoroutineScope()
    var searchJob by remember { mutableStateOf<Job?>(null) }
    val allSuggestions by KidsSearchStore.historyFlow(context).collectAsState(initial = emptyList())
    var expanded by remember { mutableStateOf(false) }

    var isSearchDone by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 20.dp)
    ) {

        // 🔹 شريط البحث
        ExposedDropdownMenuBox(
            expanded = expanded && allSuggestions.isNotEmpty(),
            onExpandedChange = { expanded = it }
        ) {
            SearchBarComposable(
                query = query,
                onQueryChange = { q ->
                    query = q
                    isSearchDone = false
                    expanded = false
                    searchJob?.cancel()
                    searchJob = scope.launch {
                        delay(350)
                        if (q.isNotBlank()) {
                            isSearchDone = false
                            val remote = withContext(Dispatchers.IO) {
                                val type = when (selectedFilter.lowercase()) {
                                    "movies" -> "movie"
                                    "tv" -> "tv"
                                    else -> "all"
                                }
                                moviesRepository.searchByType(q, type)
                            }
                            results = KidsFilter.filterKids(
                                remote.filterNot {
                                    it.title.isNullOrBlank() ||
                                            (it.adult == true) ||
                                            ((it.genres?.isEmpty() == true) && (it.genreIds?.isEmpty() == true))
                                }
                            )
                            isSearchDone = true
                        } else {
                            results = emptyList()
                        }
                    }
                },
                onSearch = {
                    focusManager.clearFocus()
                    if (query.isNotBlank()) {
                        scope.launch { KidsSearchStore.saveQuery(context, query) }
                        searchJob?.cancel()
                        searchJob = scope.launch {
                            isSearchDone = false
                            val remote = withContext(Dispatchers.IO) {
                                val type = when (selectedFilter.lowercase()) {
                                    "movies" -> "movie"
                                    "tv" -> "tv"
                                    else -> "all"
                                }
                                moviesRepository.searchByType(query, type)
                            }
                            results = KidsFilter.filterKids(
                                remote.filterNot {
                                    it.title.isNullOrBlank() ||
                                            (it.adult == true) ||
                                            ((it.genres?.isEmpty() == true) && (it.genreIds?.isEmpty() == true))
                                }
                            )
                            isSearchDone = true
                        }
                    }
                },
                placeholderText = localizedString(LocalizationKeys.KIDS_SEARCH_PLACEHOLDER)
            )

            val filteredSuggestions = remember(query, allSuggestions) {
                if (query.isBlank()) allSuggestions else allSuggestions.filter { it.contains(query, true) }
            }
            ExposedDropdownMenu(
                expanded = expanded && filteredSuggestions.isNotEmpty(),
                onDismissRequest = { expanded = false }
            ) {
                filteredSuggestions.forEach { suggestion ->
                    DropdownMenuItem(
                        text = { Text(suggestion) },
                        onClick = {
                            query = suggestion
                            expanded = false
                            focusManager.clearFocus()
                            scope.launch { KidsSearchStore.saveQuery(context, suggestion) }
                            searchJob?.cancel()
                            searchJob = scope.launch {
                                isSearchDone = false
                                val remote = withContext(Dispatchers.IO) {
                                    val type = when (selectedFilter.lowercase()) {
                                        "movies" -> "movie"
                                        "tv" -> "tv"
                                        else -> "all"
                                    }
                                    moviesRepository.searchByType(suggestion, type)
                                }
                                results = KidsFilter.filterKids(
                                    remote.filterNot {
                                        it.title.isNullOrBlank() ||
                                                (it.adult == true) ||
                                                ((it.genres?.isEmpty() == true) && (it.genreIds?.isEmpty() == true))
                                    }
                                )
                                isSearchDone = true
                            }
                        }
                    )
                }
            }
        }

        Spacer(Modifier.height(8.dp))

        // 🔹 الفلاتر
        KidsSearchFiltersRow(
            selectedFilter = selectedFilter,
            onFilterChange = { newFilter ->
                selectedFilter = newFilter
                if (query.isNotBlank()) {
                    searchJob?.cancel()
                    searchJob = scope.launch {
                        delay(150)
                        isSearchDone = false
                        val remote = withContext(Dispatchers.IO) {
                            val type = when (newFilter.lowercase()) {
                                "movies" -> "movie"
                                "tv" -> "tv"
                                else -> "all"
                            }
                            moviesRepository.searchByType(query, type)
                        }
                        results = KidsFilter.filterKids(
                            remote.filterNot {
                                it.title.isNullOrBlank() ||
                                        (it.adult == true) ||
                                        ((it.genres?.isEmpty() == true) && (it.genreIds?.isEmpty() == true))
                            }
                        )
                        isSearchDone = true
                    }
                }
            }
        )

        // 🔹 الـ Recent Searches
        if (query.isBlank() && allSuggestions.isNotEmpty()) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp)
            ) {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        localizedString(LocalizationKeys.KIDS_RECENT_SEARCHES),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    TextButton(onClick = { scope.launch { KidsSearchStore.clearHistory(context) } }) {
                        Text(localizedString(LocalizationKeys.KIDS_CLEAR_ALL),
                            color = MaterialTheme.colorScheme.error)
                    }
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    items(allSuggestions) { record ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    query = record
                                    focusManager.clearFocus()
                                    scope.launch { KidsSearchStore.saveQuery(context, record) }
                                    searchJob?.cancel()
                                    searchJob = scope.launch {
                                        isSearchDone = false
                                        val remote = withContext(Dispatchers.IO) {
                                            val type = when (selectedFilter.lowercase()) {
                                                "movies" -> "movie"
                                                "tv" -> "tv"
                                                else -> "all"
                                            }
                                            moviesRepository.searchByType(record, type)
                                        }
                                        results = KidsFilter.filterKids(
                                            remote.filterNot {
                                                it.title.isNullOrBlank() ||
                                                        (it.adult == true) ||
                                                        ((it.genres?.isEmpty() == true) && (it.genreIds?.isEmpty() == true))
                                            }
                                        )
                                        isSearchDone = true
                                    }
                                }
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(record, color = MaterialTheme.colorScheme.onSurface)
                            IconButton(onClick = { scope.launch { KidsSearchStore.deleteOne(context, record) } }) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = localizedString(LocalizationKeys.COMMON_DELETE),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                    }
                }
            }
        }

        // 🔹 لا توجد نتائج
        if (isSearchDone && query.isNotBlank() && results.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 106.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
                        modifier = Modifier.size(70.dp)
                    )
                    Spacer(Modifier.height(12.dp))
                    Text(
                        text = localizedString(LocalizationKeys.KIDS_NO_RESULTS) + " \"$query\"",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = localizedString(LocalizationKeys.KIDS_TRY_ANOTHER),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        // 🔹 النتائج
        if (results.isNotEmpty()) {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(140.dp),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(top = 12.dp, bottom = 106.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(results, key = { it.id }) { media ->
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