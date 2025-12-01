package com.example.mda.ui.screens.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.example.mda.data.local.entities.MediaEntity
import com.example.mda.data.local.entities.SearchHistoryEntity
import com.example.mda.localization.LocalizationKeys
import com.example.mda.localization.localizedString
import com.example.mda.ui.navigation.TopBarState
import com.example.mda.ui.screens.actors.ActorGridItem
import com.example.mda.ui.screens.auth.AuthViewModel
import com.example.mda.ui.screens.favorites.FavoritesViewModel
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
    val searchTitle = localizedString(LocalizationKeys.SEARCH_TITLE)
    LaunchedEffect(Unit) {
        onTopBarStateChange(
            TopBarState(
                title = searchTitle,
                showBackButton = false,
            )
        )
    }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val query by viewModel.query.collectAsStateWithLifecycle()
    val selectedFilter by viewModel.selectedFilter.collectAsStateWithLifecycle()
    val focusManager = LocalFocusManager.current
    val gridScrollState = rememberLazyGridState()
    val authUiState by authViewModel.uiState.collectAsState()
    val scope = rememberCoroutineScope()

    var isSearchDone by remember { mutableStateOf(false) }
    LaunchedEffect(authUiState.accountDetails?.id, authUiState.isAuthenticated) {
        val userIdAsString = authUiState.accountDetails?.id?.toString()
        viewModel.currentUserId = userIdAsString

        if (authUiState.isAuthenticated && userIdAsString != null) {
            viewModel.observeHistory()
        } else {
            viewModel.currentUserId = null
            viewModel.clearHistory()
            viewModel.emitIdleHistory()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 20.dp)
    ) {
        // Search bar
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
            placeholderText = localizedString(LocalizationKeys.SEARCH_PLACEHOLDER)
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

            is UiState.Success -> {
                val results = state.results
                isSearchDone = true
                if (results.isEmpty()) {
                    NoResultsContent(query)
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
                    localizedString(LocalizationKeys.SEARCH_ERROR, "error", state.message ?: ""),
                    color = MaterialTheme.colorScheme.error
                )
            }

            UiState.Empty -> {
                if (isSearchDone && query.isNotBlank()) {
                    NoResultsContent(query)
                } else if (query.isBlank()) {
                    if (state is UiState.History && state.items.isNotEmpty()) {
                        RecentSearchesList(state.items, viewModel)
                    } else {
                        Box(
                            Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                localizedString(LocalizationKeys.SEARCH_START_TYPING),
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


@Composable
private fun NoResultsContent(query: String) {
    Box(
        Modifier
            .fillMaxSize()
            .padding(bottom = 86.dp),
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
                localizedString(LocalizationKeys.SEARCH_NO_RESULTS) + " \"$query\"",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(8.dp))
            Text(
                localizedString(LocalizationKeys.SEARCH_TRY_ANOTHER),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(40.dp))
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
                localizedString(LocalizationKeys.SEARCH_RECENT_TITLE),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
            TextButton(onClick = { viewModel.clearHistory() }) {
                Text(
                    localizedString(LocalizationKeys.SEARCH_CLEAR_ALL),
                    color = MaterialTheme.colorScheme.error
                )
            }
        }

        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))

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
                    Text(record.query, color = MaterialTheme.colorScheme.onSurface)
                    IconButton(onClick = { viewModel.deleteOne(record.query) }) {
                        Icon(
                            Icons.Default.Delete,
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