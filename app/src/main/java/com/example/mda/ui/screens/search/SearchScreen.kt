package com.example.mda.ui.screens.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.mda.data.local.entities.MediaEntity
import com.example.mda.ui.navigation.TopBarState
import com.example.mda.ui.screens.components.MovieCardGrid
import com.example.mda.ui.screens.components.MovieCardGridWithFavorite
import com.example.mda.ui.screens.favorites.FavoritesViewModel
import com.example.mda.ui.screens.favorites.components.FavoriteButton
import com.example.mda.data.remote.model.Movie
import kotlinx.coroutines.launch

/**
 * شاشة البحث – محسّنة مع دعم التوب بار الديناميكي
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    navController: NavController,
    viewModel: SearchViewModel,
    onTopBarStateChange: (TopBarState) -> Unit,
    favoritesViewModel: FavoritesViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val query by viewModel.query.collectAsStateWithLifecycle()
    val selectedFilter by viewModel.selectedFilter.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val focusManager = LocalFocusManager.current
    val suggestionList by viewModel.suggestions.collectAsStateWithLifecycle()
    var isFocused by remember { mutableStateOf(false) }

    // ✅ تحديث التوب بار مع الأكشنز (زي باقي الصفحات)
    LaunchedEffect(Unit) {
        onTopBarStateChange(
            TopBarState(
                title = "Search",
                actions = {
                    IconButton(onClick = { viewModel.clearHistory() }) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Clear search history"
                        )
                    }
                }
            )
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->

        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp, vertical = 16.dp)
        ) {
            // -------- Search Box + Dropdown Suggestions --------
            var expanded by remember { mutableStateOf(false) }

            ExposedDropdownMenuBox(
                expanded = expanded && suggestionList.isNotEmpty(),
                onExpandedChange = { expanded = it }
            ) {
                OutlinedTextField(
                    value = query,
                    onValueChange = {
                        viewModel.onQueryChange(it)
                        expanded = it.isNotBlank() && isFocused
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                        .onFocusChanged { f ->
                            val nowFocused = f.isFocused
                            if (isFocused && !nowFocused) {
                                expanded = false
                            }
                            isFocused = nowFocused
                        },
                    placeholder = { Text("Search movies, shows, actors...") },
                    singleLine = true,
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search icon") },
                    trailingIcon = {
                        if (query.isNotEmpty()) {
                            IconButton(onClick = {
                                viewModel.onQueryChange("")
                                expanded = false
                            }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear text")
                            }
                        }
                    },
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(onSearch = {
                        focusManager.clearFocus()
                        expanded = false
                        viewModel.submitSearch()
                    }),
                    shape = RoundedCornerShape(16.dp)
                )

                // ----- Suggestion dropdown -----
                ExposedDropdownMenu(
                    expanded = expanded && suggestionList.isNotEmpty(),
                    onDismissRequest = { expanded = false }
                ) {
                    suggestionList.forEach { suggestion ->
                        DropdownMenuItem(
                            text = { Text(suggestion) },
                            onClick = {
                                viewModel.onQueryChange(suggestion)
                                expanded = false
                                focusManager.clearFocus()
                                viewModel.submitSearch()
                            }
                        )
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            // -------- Filters --------
            SearchFiltersRow(selectedFilter = selectedFilter) {
                viewModel.onFilterSelected(it)
            }

            Spacer(Modifier.height(8.dp))

            // -------- Manual search button --------
            Button(
                onClick = {
                    focusManager.clearFocus()
                    expanded = false
                    viewModel.submitSearch()
                },
                modifier = Modifier.align(Alignment.End),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Search")
            }

            Spacer(Modifier.height(8.dp))

            // -------- Unified UI States --------
            when (val state = uiState) {
                is UiState.History -> {
                    if (state.items.isNotEmpty()) {
                        Text("Recent Searches", style = MaterialTheme.typography.titleMedium)
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            TextButton(onClick = { viewModel.clearHistory() }) {
                                Text("Clear All", color = MaterialTheme.colorScheme.error)
                            }
                        }
                        Spacer(Modifier.height(4.dp))
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            items(state.items.size) { idx ->
                                val record = state.items[idx]
                                Row(
                                    Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            viewModel.onQueryChange(record.query)
                                            focusManager.clearFocus()
                                            viewModel.submitSearch()
                                        }
                                        .padding(vertical = 6.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(record.query)
                                    IconButton(onClick = {
                                        viewModel.deleteOne(record.query)
                                    }) {
                                        Icon(
                                            Icons.Default.Delete,
                                            contentDescription = "Delete history item"
                                        )
                                    }
                                }
                                Divider()
                            }
                        }
                    }
                }

                UiState.Loading -> {
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator(modifier = Modifier.size(28.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Searching...", style = MaterialTheme.typography.bodyMedium)
                    }
                }

                is UiState.Success -> {
                    SearchResultsGrid(
                        results = state.results,
                        onItemClick = { media ->
                            navController.navigate("detail/${media.mediaType}/${media.id}")
                        },
                        favoritesViewModel = favoritesViewModel
                    )
                }

                UiState.Empty -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            "No results found",
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                is UiState.Error -> {
                    val errorMessage = state.message
                    LaunchedEffect(errorMessage) {
                        snackbarHostState.showSnackbar(errorMessage)
                    }
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                "Error: $errorMessage",
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.error
                            )
                            Spacer(Modifier.height(8.dp))
                            Button(onClick = { viewModel.retryLastSearch() }) {
                                Text("Retry")
                            }
                        }
                    }
                }

                UiState.Idle -> {}
            }
        }
    }
}

@Composable
fun SearchFiltersRow(
    selectedFilter: String,
    onFilterChange: (String) -> Unit
) {
    val filters = listOf("all", "movies", "tv", "people")
    Row(
        Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        filters.forEach { filter ->
            val selected = selectedFilter.equals(filter, true)
            FilterChip(
                selected = selected,
                onClick = { onFilterChange(filter) },
                label = { Text(filter.replaceFirstChar { it.uppercaseChar() }) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    }
}

@Composable
fun SearchResultsGrid(
    results: List<MediaEntity>,
    onItemClick: (MediaEntity) -> Unit,
    favoritesViewModel: FavoritesViewModel
) {
    val gridState = rememberLazyGridState()
    LazyVerticalGrid(
        contentPadding = PaddingValues(bottom = 106.dp, top = 16.dp, start = 16.dp, end = 16.dp),
        columns = GridCells.Adaptive(150.dp),
        state = gridState,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        items(
            items = results,
            key = { "${it.mediaType}-${it.id}" }
        ) { media ->
            val movie = media.toMovie()
            MovieCardGridWithFavorite(
                movie = media,
                onClick = { onItemClick(media) },
                favoriteButton = {
                    FavoriteButton(
                        movie = movie,
                        viewModel = favoritesViewModel,
                        showBackground = true
                    )
                }
            )
        }
    }
}

// Extension function to convert MediaEntity to Movie
private fun MediaEntity.toMovie(): Movie {
    return Movie(
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
}
