package com.example.mda.ui.screens.actors

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
    val gridState = rememberLazyGridState()
    val listState = rememberLazyListState()

    // Pagination trigger
    LaunchedEffect(
        gridState.layoutInfo.visibleItemsInfo.lastOrNull()?.index,
        listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index
    ) {
        val lastVisibleItemIndex = if (viewType == ViewType.GRID) {
            gridState.layoutInfo.visibleItemsInfo.lastOrNull()?.index
        } else {
            listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index
        }

        // Log for debug
        android.util.Log.d(
            "ActorsView",
            "Last visible index: $lastVisibleItemIndex / actors.size=${actors.size}"
        )

        if (lastVisibleItemIndex != null && lastVisibleItemIndex >= actors.size - 6) {
            android.util.Log.d("ActorsView", "Triggering loadMoreActors()")
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
        }
    } else {
        LazyColumn(
            state = listState,
            modifier = modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
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
            item { Box(modifier = Modifier.height(66.dp)) }
        }
    }
}
