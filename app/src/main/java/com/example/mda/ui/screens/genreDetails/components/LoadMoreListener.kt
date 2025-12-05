package com.example.mda.ui.screens.genreDetails.components

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import com.example.mda.ui.screens.genreDetails.GenreDetailsViewModel

@Composable
fun LoadMoreListener(
    gridState: LazyGridState? = null,
    listState: LazyListState? = null,
    viewModel: GenreDetailsViewModel,
    genreId: Int
) {
    LaunchedEffect(gridState, listState, viewModel.movies.size) {
        snapshotFlow {
            val total = gridState?.layoutInfo?.totalItemsCount
                ?: listState?.layoutInfo?.totalItemsCount ?: 0
            val lastVisible = gridState?.layoutInfo?.visibleItemsInfo?.lastOrNull()?.index
                ?: listState?.layoutInfo?.visibleItemsInfo?.lastOrNull()?.index ?: 0
            lastVisible to total
        }.collect { (last, total) ->
            if (last >= total - 4 && !viewModel.isLoading) {
                viewModel.loadMoviesByGenre(genreId)
            }
        }
    }
}