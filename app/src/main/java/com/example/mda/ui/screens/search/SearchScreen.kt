package com.example.mda.ui.screens.search

import androidx.compose.animation.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.mda.data.local.entities.MediaEntity
import kotlinx.coroutines.launch

@OptIn(ExperimentalAnimationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    navController: NavController,
    viewModel: SearchViewModel
) {
    val results by viewModel.results.collectAsState()
    val history by viewModel.history.collectAsState()
    var query by remember { mutableStateOf(viewModel.query) }

    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Search") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            // 🔍 Search Field
            OutlinedTextField(
                value = query,
                onValueChange = {
                    query = it
                    viewModel.onQueryChange(it)
                },
                placeholder = { Text("Search for a movie or TV show") },
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(12.dp))

            Button(
                onClick = { viewModel.performSearch() },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Search")
            }

            Spacer(Modifier.height(16.dp))

            // ⚡ Results Section
            AnimatedVisibility(
                visible = results.isNotEmpty(),
                enter = fadeIn() + slideInVertically()
            ) {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(results) { item: MediaEntity ->
                        val displayTitle = item.title ?: item.name ?: "Unknown"
                        ListItem(
                            headlineContent = { Text(displayTitle) },
                            supportingContent = { Text(item.overview ?: "") },
                            leadingContent = { Icon(Icons.Filled.Search, contentDescription = null) },
                            modifier = Modifier.clickable {
                                navController.navigate("detail/${item.mediaType}/${item.id}")
                            }
                        )
                        Divider()
                    }
                }
            }

            // 🕓 History Section
            AnimatedVisibility(
                visible = results.isEmpty() && history.isNotEmpty(),
                enter = fadeIn()
            ) {
                Column {
                    Text("Recently Viewed", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(8.dp))
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(history.take(10)) { media ->
                            val displayTitle = media.title ?: media.name ?: "Unknown"
                            ListItem(
                                headlineContent = { Text(displayTitle) },
                                leadingContent = {
                                    Icon(Icons.Filled.History, contentDescription = null)
                                },
                                modifier = Modifier.clickable {
                                    navController.navigate("detail/${media.mediaType}/${media.id}")
                                }
                            )
                            Divider()
                        }
                    }
                }
            }
        }
    }
}
