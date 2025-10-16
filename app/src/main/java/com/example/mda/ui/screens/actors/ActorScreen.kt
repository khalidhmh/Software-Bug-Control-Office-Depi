package com.example.mda.ui.screens.actors

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.mda.data.remote.model.Actor
import com.example.mda.data.repository.MoviesRepository
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshIndicator
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.example.mda.data.repository.ActorsRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActorsScreen(
    navController: NavHostController,
    repository: ActorsRepository,
    viewModel: ActorViewModel
) {
    val uiState by viewModel.state.collectAsState()
    val viewType by viewModel.viewType.collectAsState()
    var refreshing by remember { mutableStateOf(false) }
    val refreshState = rememberSwipeRefreshState(isRefreshing = refreshing)

    SwipeRefresh(
            state = refreshState,
            onRefresh = {
                refreshing = true
                viewModel.loadActors()
                refreshing = false
            },
            indicator = { s, t ->
                SwipeRefreshIndicator(
                    state = s,
                    refreshTriggerDistance = t,
                    backgroundColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.primary
                )
            }
        ) {
            when (val state = uiState) {
                is ActorUiState.Loading -> Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }

                is ActorUiState.Success -> {
                    AnimatedVisibility(visible = state.actors.isNotEmpty(), enter = fadeIn()) {
                        ActorsView(
                            actors = state.actors,
                            viewModel = viewModel,
                            viewType = viewType,
                            navController = navController,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }

                is ActorUiState.Error -> {
                    ErrorScreen(
                        message = state.message ?: "حدث خطأ أثناء تحميل الممثلين",
                        onRetry = { viewModel.retry() },
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp)
                    )
                }
            }
        }
    }

@Composable
fun ErrorScreen(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(message, color = MaterialTheme.colorScheme.error)
            Button(onClick = onRetry) { Text("إعادة المحاولة") }
        }
    }
}
