package com.example.mda.ui.screens.search

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
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
import androidx.compose.ui.focus.onFocusChanged
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
import com.example.mda.ui.screens.auth.AuthViewModel

/**
 * Search Screen - Animated Search Bar Position
 */
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
    val snackbarHostState = remember { SnackbarHostState() }
    val authUiState by authViewModel.uiState.collectAsState()

    // 1. State to track focus
    var isSearchFocused by remember { mutableStateOf(false) }

    // 2. Logic: It stays up if you are typing (focused) OR if there is text (results shown)
    val isSearchActive = isSearchFocused || query.isNotEmpty()

    // 3. Animation: Move between 140.dp (Original low position) and 0.dp (Top position)
    val animatedTopPadding by animateDpAsState(
        targetValue = if (isSearchActive) 0.dp else 140.dp,
        animationSpec = spring(stiffness = Spring.StiffnessLow), // Smooth motion
        label = "SearchBarAnimation"
    )

    // Setup Top Bar
    LaunchedEffect(Unit) {
        onTopBarStateChange(TopBarState(title = "Discover"))
    }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                title = { },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        },
        snackbarHost = {
            SnackbarHost(snackbarHostState) { data ->
                Snackbar(
                    snackbarData = data,
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {

            // 4. The Animated Spacer that pushes the Search Bar down or pulls it up
            Spacer(modifier = Modifier.height(animatedTopPadding))

            // Search TextField
            OutlinedTextField(
                value = query,
                onValueChange = { viewModel.onQueryChange(it) },
                placeholder = {
                    Text(
                        "Search movies, shows, people...",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                leadingIcon = {
                    Icon(
                        Icons.Default.Search,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                trailingIcon = {
                    if (query.isNotEmpty()) {
                        IconButton(onClick = { viewModel.onQueryChange("") }) {
                            Icon(
                                Icons.Default.Clear,
                                contentDescription = "Clear",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                shape = RoundedCornerShape(30.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = Color.Transparent,
                    cursorColor = MaterialTheme.colorScheme.primary,
                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                ),
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = {
                    focusManager.clearFocus() // This allows it to drop down if text is empty
                    viewModel.submitSearch()
                }),
                modifier = Modifier
                    .fillMaxWidth()
                    // 5. Detect Focus Changes
                    .onFocusChanged { focusState ->
                        isSearchFocused = focusState.isFocused
                    }
                    .padding(top = 4.dp)
                    .height(56.dp)
            )

            Spacer(Modifier.height(10.dp))

            SearchFiltersRow(selectedFilter = selectedFilter) {
                viewModel.onFilterSelected(it)
            }

            Spacer(Modifier.height(16.dp))

            // Content Area - Only visible/interactive when needed
            // The Box ensures the Column doesn't jump in width/layout unexpectedly
            Box(modifier = Modifier.weight(1f)) {
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
                                favoritesViewModel = favoritesViewModel,
                                navController = navController,
                                isAuthenticated = authUiState.isAuthenticated
                            )
                        }
                    }

                    is UiState.History -> {
                        // History shows when we have items and search is "active"
                        if (state.items.isNotEmpty()) {
                            RecentSearchesList(state.items, viewModel)
                        }
                    }

                    UiState.Empty -> Box(
                        Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "No results found",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
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

                    else -> {
                        // Default state
                    }
                }
            }
        }
    }
}

// ... Helper functions (toActorModel, RecentSearchesList) remain the same ...
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