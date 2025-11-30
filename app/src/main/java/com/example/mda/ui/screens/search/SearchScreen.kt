package com.example.mda.ui.screens.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.example.mda.data.local.entities.MediaEntity
import com.example.mda.data.local.entities.SearchHistoryEntity
import com.example.mda.ui.navigation.TopBarState
import com.example.mda.ui.screens.actors.ActorGridItem
import com.example.mda.ui.screens.favorites.FavoritesViewModel
import com.example.mda.ui.screens.auth.AuthViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    navController: NavHostController,
    viewModel: SearchViewModel,
    onTopBarStateChange: (TopBarState) -> Unit,
    favoritesViewModel: FavoritesViewModel,
    authViewModel: AuthViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val query by viewModel.query.collectAsStateWithLifecycle()
    val selectedFilter by viewModel.selectedFilter.collectAsStateWithLifecycle()
    val focusManager = LocalFocusManager.current
    val gridScrollState = rememberLazyGridState()
    val authUiState by authViewModel.uiState.collectAsState()
    val scope = rememberCoroutineScope()

    var isSearchDone by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 20.dp)
    ) {

        SearchBarComposable(
            query = query,
            onQueryChange = {
                viewModel.onQueryChange(it)
                isSearchDone = false
            },
            onSearch = {
                focusManager.clearFocus()
                isSearchDone = false
                scope.launch {
                    viewModel.submitSearch()
                    delay(300)
                    isSearchDone = true
                }
            },
            placeholderText = "Search movies, shows, people..."
        )

        Spacer(Modifier.height(10.dp))

        SearchFiltersRow(selectedFilter = selectedFilter) {
            viewModel.onFilterSelected(it)
        }

        Spacer(Modifier.height(16.dp))

        when (val state = uiState) {

            UiState.Loading -> Box(
                Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }

            is UiState.History -> {
                if (query.isBlank() && state.items.isNotEmpty()) {
                    RecentSearchesList(state.items, viewModel)
                }
            }

            /** 🔵 عرض نتائج البحث */
            is UiState.Success -> {
                val results = state.results
                isSearchDone = true

                if (results.isEmpty()) {
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
                                text = "No results found for \"$query\"",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(Modifier.height(6.dp))
                            Text(
                                text = "Try searching for another movie, show or person 🎬",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                            )
                        }
                    }
                } else {
                    if (selectedFilter == "people") {
                        LazyVerticalGrid(
                            columns = GridCells.Adaptive(140.dp),
                            contentPadding = PaddingValues(8.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            state = gridScrollState
                        ) {
                            items(results) { person ->
                                ActorGridItem(
                                    actor = person.toActorModel(),
                                    navController = navController
                                )
                            }
                        }
                    } else {
                        SearchResultsGrid(
                            results = results,
                            onItemClick = {
                                navController.navigate("detail/${it.mediaType}/${it.id}")
                            },
                            favoritesViewModel = favoritesViewModel,
                            navController = navController,
                            isAuthenticated = authUiState.isAuthenticated
                        )
                    }
                }
            }


            is UiState.Error -> Box(
                Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "Error: ${state.message}",
                    color = MaterialTheme.colorScheme.error
                )
            }

            UiState.Empty -> {
                if (query.isNotBlank()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(bottom = 76.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
                                modifier = Modifier.size(70.dp)
                            )
                            Spacer(Modifier.height(12.dp))
                            Text(
                                text = "No results found for \"$query\"",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(
                                text = "Try searching for another movie or person ",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                            Spacer(Modifier.height(40.dp))
                        }
                    }
                }
                // 🟢 لو المستخدم لسه ما كتبش حاجة خالص → يعرض Recent Searches
                else {
                    when (val history = uiState) {
                        is UiState.History -> {
                            if (history.items.isNotEmpty()) {
                                RecentSearchesList(history.items, viewModel)
                            }
                        }
                        else -> Box(
                            Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "Start typing to search ",
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            else -> Unit
        }
    }
}


private fun MediaEntity.toActorModel() =
    com.example.mda.data.remote.model.Actor(
        id = id,
        name = name ?: title.orEmpty(),
        profilePath = posterPath ?: backdropPath,
        knownFor = null,
        biography = null,
        birthday = null,
        knownForDepartment = null,
        placeOfBirth = null
    )

@Composable
fun RecentSearchesList(
    items: List<SearchHistoryEntity>,
    viewModel: SearchViewModel
) {
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
                "Recent Searches",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
            TextButton(onClick = { viewModel.clearHistory() }) {
                Text("Clear all", color = MaterialTheme.colorScheme.error)
            }
        }

        HorizontalDivider(
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.padding(top = 8.dp)
        ) {
            items(items) { record ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            viewModel.onQueryChange(record.query)
                            viewModel.submitSearch()
                        }
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        record.query,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    IconButton(onClick = { viewModel.deleteOne(record.query) }) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                HorizontalDivider(
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                )
            }
        }
    }
}