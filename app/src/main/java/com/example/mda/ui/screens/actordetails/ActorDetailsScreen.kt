package com.example.mda.ui.screens.actordetails

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.mda.ui.actor.ActorViewModel
import com.example.mda.data.repository.ActorsRepository
import com.example.mda.ui.actordetails.calculateAge
import com.example.mda.ui.navigation.TopBarState
import com.example.mda.ui.screens.favorites.FavoritesViewModel
import com.example.mda.ui.theme.AppBackgroundGradient
import com.example.mda.ui.screens.profile.history.HistoryViewModel
import com.google.accompanist.swiperefresh.*
import kotlinx.coroutines.launch
import kotlin.math.log
import com.example.mda.ui.screens.auth.AuthViewModel

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActorDetailsScreen(
    personId: Int,
    navController: NavHostController,
    repository: ActorsRepository,
    onTopBarStateChange: (TopBarState) -> Unit,
    favoritesViewModel: FavoritesViewModel,
    historyViewModel: HistoryViewModel,
    authViewModel: AuthViewModel
) {
    val vm: ActorViewModel = viewModel(factory = ActorViewModel.ActorViewModelFactory(repository))
    val actor by vm.actorFullDetails.collectAsState()
    val loading by vm.loading.collectAsState()
    val error by vm.error.collectAsState()
    val movieCount by vm.movieCount.collectAsState()
    val tvShowCount by vm.tvShowCount.collectAsState()
    val scope = rememberCoroutineScope()

    var refreshing by remember { mutableStateOf(false) }
    val refreshState = rememberSwipeRefreshState(isRefreshing = refreshing)

    LaunchedEffect(personId) {
        vm.loadActorFullDetails(personId)
        Log.d("ActorDetailsScreen","ActorDetailsScreen received personId=$personId")
    }

        SwipeRefresh(
            state = refreshState,
            onRefresh = {
                scope.launch {
                    refreshing = true
                    try {
                        vm.loadActorFullDetails(personId, forceRefresh = true)
                    }finally {
                        refreshing = false
                    }
                }
            },
            indicator = { state, trigger ->
                SwipeRefreshIndicator(
                    state,
                    trigger,
                    backgroundColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.primary
                )
            }
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(AppBackgroundGradient())

            ) {
                when {
                    loading -> Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) { CircularProgressIndicator(color = MaterialTheme.colorScheme.primary) }

                    error != null -> Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) { Text("Error: $error", color = MaterialTheme.colorScheme.error) }

                    actor != null -> AnimatedVisibility(
                        visible = true,
                        enter = fadeIn() + slideInVertically()
                    ) {
                        ActorDetailsScreenContent(
                            actor = actor!!,
                            movieCount = movieCount,
                            tvShowCount = tvShowCount,
                            age = actor?.birthday?.let { calculateAge(it) },
                            navController = navController,
                            favoritesViewModel = favoritesViewModel,
                            historyViewModel = historyViewModel,
                            authViewModel = authViewModel
                        )
                    }

                    else -> Text(
                        text = "No details available",
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }
    }

