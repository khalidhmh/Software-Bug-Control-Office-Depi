package com.example.mda.ui.screens.actors

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.mda.data.remote.model.Actor

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ActorsView(
    actors: List<Actor>,
    viewModel: ActorViewModel,
    viewType: ViewType,
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    // 1. Save scroll position across navigation (Home <-> Actors)
    val gridState = rememberSaveable(saver = LazyGridState.Saver) { LazyGridState() }
    val listState = rememberSaveable(saver = LazyListState.Saver) { LazyListState() }

    // 2. Pagination Logic
    LaunchedEffect(
        gridState.firstVisibleItemIndex,
        listState.firstVisibleItemIndex,
        actors.size
    ) {
        if (actors.isEmpty()) return@LaunchedEffect

        val lastVisibleItemIndex = if (viewType == ViewType.GRID) {
            gridState.layoutInfo.visibleItemsInfo.lastOrNull()?.index
        } else {
            listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index
        }

        // Load more when reaching the bottom (threshold of 6 items)
        if (lastVisibleItemIndex != null && lastVisibleItemIndex >= actors.size - 6) {
            viewModel.loadMoreActors()
        }
    }

    if (viewType == ViewType.GRID) {
        LazyVerticalGrid(
            state = gridState,
            columns = GridCells.Fixed(3),
            modifier = modifier.fillMaxSize(),
            contentPadding = PaddingValues(8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(actors, key = { "grid-${it.id}" }) { actor ->
                ActorGridItem(actor = actor, navController = navController)
            }
            // Bottom spacer to avoid navigation bar overlap
            item { Spacer(modifier = Modifier.height(100.dp)) }
        }
    } else {
        LazyColumn(
            state = listState,
            modifier = modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            items(actors, key = { "list-${it.id}" }) { actor ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                ) {
                    ActorListItem(actor = actor, navController = navController)
                }
            }
            // Bottom spacer to avoid navigation bar overlap
            item { Box(modifier = Modifier.height(100.dp)) }
        }
    }
}