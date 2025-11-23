package com.example.mda.ui.kids

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.foundation.clickable
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ExperimentalMaterial3Api
import com.example.mda.data.local.entities.MediaEntity
import com.example.mda.data.repository.MoviesRepository
import com.example.mda.ui.kids.KidsFilter.filterKids
import com.example.mda.ui.screens.components.MovieCardGridWithFavorite
import com.example.mda.ui.screens.favorites.FavoritesViewModel
import com.example.mda.ui.kids.favorites.KidsFavoriteButton
import com.example.mda.data.remote.model.Movie
import com.example.mda.ui.screens.search.SearchFiltersRow
import com.example.mda.ui.kids.search.KidsSearchStore
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.foundation.shape.RoundedCornerShape
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
    var isFocused by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        ExposedDropdownMenuBox(
            expanded = expanded && allSuggestions.isNotEmpty(),
            onExpandedChange = { expanded = it }
        ) {
            OutlinedTextField(
                value = query,
                onValueChange = { q ->
                    query = q
                    expanded = q.isNotBlank() && isFocused
                    searchJob?.cancel()
                    searchJob = scope.launch {
                        delay(350)
                        if (q.isNotBlank()) {
                            val remote = withContext(Dispatchers.IO) {
                                val type = when (selectedFilter.lowercase()) {
                                    "movies" -> "movie"
                                    "tv" -> "tv"
                                    else -> "all"
                                }
                                moviesRepository.searchByType(q, type)
                            }
                            results = filterKids(remote)
                        } else {
                            results = emptyList()
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor()
                    .onFocusChanged { f ->
                        val now = f.isFocused
                        if (isFocused && !now) expanded = false
                        isFocused = now
                    },
                placeholder = { Text("Search kids-safe content...") },
                singleLine = true,
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon = {
                    if (query.isNotEmpty()) {
                        IconButton(onClick = {
                            query = ""
                            expanded = false
                            results = emptyList()
                        }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear")
                        }
                    }
                },
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = {
                    focusManager.clearFocus()
                    expanded = false
                    scope.launch { KidsSearchStore.saveQuery(context, query) }
                    // trigger search immediately
                    searchJob?.cancel()
                    searchJob = scope.launch {
                        val remote = withContext(Dispatchers.IO) {
                            val type = when (selectedFilter.lowercase()) {
                                "movies" -> "movie"
                                "tv" -> "tv"
                                else -> "all"
                            }
                            moviesRepository.searchByType(query, type)
                        }
                        results = filterKids(remote)
                    }
                }),
                shape = RoundedCornerShape(16.dp)
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
                            // run search for suggestion
                            searchJob?.cancel()
                            searchJob = scope.launch {
                                val remote = withContext(Dispatchers.IO) {
                                    val type = when (selectedFilter.lowercase()) {
                                        "movies" -> "movie"
                                        "tv" -> "tv"
                                        else -> "all"
                                    }
                                    moviesRepository.searchByType(suggestion, type)
                                }
                                results = filterKids(remote)
                            }
                        }
                    )
                }
            }
        }
        Spacer(Modifier.height(8.dp))
        SearchFiltersRow(
            selectedFilter = selectedFilter,
            onFilterChange = { newFilter ->
                selectedFilter = newFilter
                if (query.isNotBlank()) {
                    searchJob?.cancel()
                    searchJob = scope.launch {
                        delay(150)
                        val remote = withContext(Dispatchers.IO) {
                            val type = when (newFilter.lowercase()) {
                                "movies" -> "movie"
                                "tv" -> "tv"
                                else -> "all"
                            }
                            moviesRepository.searchByType(query, type)
                        }
                        results = filterKids(remote)
                    }
                }
            }
        )
        // Recent searches section when idle (query blank)
        if (query.isBlank() && allSuggestions.isNotEmpty()) {
            Spacer(Modifier.height(12.dp))
            androidx.compose.material3.Text("Recent Searches", style = androidx.compose.material3.MaterialTheme.typography.titleMedium)
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                androidx.compose.material3.TextButton(onClick = { scope.launch { KidsSearchStore.clearHistory(context) } }) {
                    androidx.compose.material3.Icon(Icons.Default.Delete, contentDescription = null)
                    Spacer(Modifier.width(4.dp))
                    androidx.compose.material3.Text("Clear All", color = androidx.compose.material3.MaterialTheme.colorScheme.error)
                }
            }
            Spacer(Modifier.height(4.dp))
            androidx.compose.foundation.lazy.LazyColumn(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(allSuggestions.size) { idx ->
                    val record = allSuggestions[idx]
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .clickable {
                                query = record
                                focusManager.clearFocus()
                                scope.launch { KidsSearchStore.saveQuery(context, record) }
                                searchJob?.cancel()
                                searchJob = scope.launch {
                                    val remote = withContext(Dispatchers.IO) {
                                        val type = when (selectedFilter.lowercase()) {
                                            "movies" -> "movie"
                                            "tv" -> "tv"
                                            else -> "all"
                                        }
                                        moviesRepository.searchByType(record, type)
                                    }
                                    results = filterKids(remote)
                                }
                            }
                            .padding(vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        androidx.compose.material3.Text(record)
                        androidx.compose.material3.IconButton(onClick = { scope.launch { KidsSearchStore.deleteOne(context, record) } }) {
                            androidx.compose.material3.Icon(Icons.Default.Delete, contentDescription = null)
                        }
                    }
                    androidx.compose.material3.Divider()
                }
            }
        }
        Spacer(Modifier.height(12.dp))
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
