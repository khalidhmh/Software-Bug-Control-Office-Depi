package com.example.mda.ui.screens.actors

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.mda.data.repository.ActorsRepository
import com.example.mda.ui.navigation.TopBarState
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshIndicator
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

@Composable
fun ActorsScreen(
    navController: NavHostController,
    actorsRepository: ActorsRepository,
    onTopBarStateChange: (TopBarState) -> Unit,
    viewModel: ActorViewModel
) {
    val uiState by viewModel.state.collectAsState()
    val viewType by viewModel.viewType.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()

    LaunchedEffect(viewType) {
        onTopBarStateChange(
            TopBarState(
                actions = {
                    IconButton(onClick = { viewModel.toggleViewType() }) {
                        Icon(
                            imageVector = if (viewType == ViewType.GRID) Icons.Default.List else Icons.Default.GridView,
                            contentDescription = "Toggle View"
                        )
                    }
                }
            )
        )
    }

    DisposableEffect(Unit) {
        onDispose {
            onTopBarStateChange(TopBarState())
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {

        val refreshState = rememberSwipeRefreshState(isRefreshing = isRefreshing)

        SwipeRefresh(
            state = refreshState,
            onRefresh = { viewModel.loadActors(forceRefresh = true) },
            indicator = { s, t ->
                SwipeRefreshIndicator(
                    state = s,
                    refreshTriggerDistance = t,
                    backgroundColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.primary
                )
            },
            modifier = Modifier.fillMaxSize()
        ) {
            when (val state = uiState) {
                is ActorUiState.Loading -> if (!isRefreshing) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    }
                }

                is ActorUiState.Success -> {
                    AnimatedVisibility(visible = state.actors.isNotEmpty(), enter = fadeIn()) {
                        ActorsView(
                            actors = state.actors,
                            viewModel = viewModel,
                            viewType = viewType,
                            navController = navController,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }

                is ActorUiState.Error -> {
                    ActorErrorScreen(
                        errorType = state.type,
                        onRetry = { viewModel.loadMoreActors() }
                    )
                }
            }
        }
    }
}



