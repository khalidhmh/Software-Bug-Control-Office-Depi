package com.example.mda.ui.screens.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.example.mda.data.local.entities.MediaEntity
import com.example.mda.data.local.entities.SearchHistoryEntity
import com.example.mda.data.remote.model.Actor
import com.example.mda.ui.navigation.TopBarState
import com.example.mda.ui.screens.actors.ActorGridItem
import com.example.mda.ui.screens.favorites.FavoritesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    navController: NavHostController,
    viewModel: SearchViewModel,
    onTopBarStateChange: (TopBarState) -> Unit,
    favoritesViewModel: FavoritesViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val query by viewModel.query.collectAsStateWithLifecycle()
    val selectedFilter by viewModel.selectedFilter.collectAsStateWithLifecycle()
    val focusManager = LocalFocusManager.current
    val gridScrollState = rememberLazyGridState()

    //  تهيئة التوب بار
    LaunchedEffect(Unit) {
        onTopBarStateChange(TopBarState(title = "Discover"))
    }

    Scaffold(containerColor = MaterialTheme.colorScheme.background) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {

            OutlinedTextField(
                value = query,
                onValueChange = { viewModel.onQueryChange(it) },
                placeholder = { Text("Search movies, shows, people...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon = {
                    if (query.isNotEmpty()) {
                        IconButton(onClick = { viewModel.onQueryChange("") }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear")
                        }
                    }
                },
                shape = RoundedCornerShape(30.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    cursorColor = MaterialTheme.colorScheme.onSurface,
                ),
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = {
                    focusManager.clearFocus()
                    viewModel.submitSearch()
                }),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp)
                    .height(56.dp)
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

                is UiState.Success -> {
                    if (selectedFilter == "people") {
                        LazyVerticalGrid(
                            columns = GridCells.Adaptive(140.dp),
                            contentPadding = PaddingValues(8.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            state = gridScrollState
                        ) {
                            items(state.results) { person ->
                                ActorGridItem(
                                    actor = person.toActorModel(),
                                    navController = navController
                                )
                            }
                        }
                    } else {
                        SearchResultsGrid(
                            results = state.results,
                            onItemClick = {
                                navController.navigate("detail/${it.mediaType}/${it.id}")
                            },
                            favoritesViewModel = favoritesViewModel
                        )
                    }
                }

                is UiState.History -> {
                    if (state.items.isNotEmpty()) {
                        RecentSearchesList(state.items, viewModel)
                    } else {

                    }
                }

                UiState.Empty -> Box(
                    Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) { Text("No results found") }

                is UiState.Error -> Box(
                    Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) { Text("Error: ${state.message}") }

                else -> {
                    //  LatestMoviesSection(navController, viewModel, favoritesViewModel)
                }
            }
        }
    }
}

//  تحويل MediaEntity لـ Actor Model بسيط
private fun MediaEntity.toActorModel() =
    Actor(
        id = id,
        name = name ?: title.orEmpty(),
        profilePath = posterPath ?: backdropPath,
        knownFor = null,
        biography = null,
        birthday = null,
        knownForDepartment = null,
        placeOfBirth = null
    )

// 🔹 قائمة الـ Search History
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
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                "Recent Searches",
                style = MaterialTheme.typography.titleMedium
            )
            TextButton(onClick = { viewModel.clearHistory() }) {
                Text("Clear all", color = MaterialTheme.colorScheme.error)
            }
        }

        Divider()
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(top = 8.dp)
        ) {
            items(items) { record ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            viewModel.onQueryChange(record.query)
                            viewModel.submitSearch()
                        },
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(record.query)
                    IconButton(onClick = { viewModel.deleteOne(record.query) }) {
                        Icon(Icons.Default.Delete, null)
                    }
                }
                Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
            }
        }
    }
}