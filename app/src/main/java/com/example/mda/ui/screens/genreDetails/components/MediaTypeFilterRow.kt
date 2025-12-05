package com.example.mda.ui.screens.genreDetails.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.mda.ui.screens.genreDetails.MediaTypeFilter

@Composable
fun MediaTypeFilterRow(
    selectedFilter: MediaTypeFilter,
    onFilterChange: (MediaTypeFilter) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        MediaTypeFilterChip(
            text = "Movies",
            selected = selectedFilter == MediaTypeFilter.MOVIES,
            onClick = { onFilterChange(MediaTypeFilter.MOVIES) },
            modifier = Modifier.weight(1f)
        )
        MediaTypeFilterChip(
            text = "TV Shows",
            selected = selectedFilter == MediaTypeFilter.TV_SHOWS,
            onClick = { onFilterChange(MediaTypeFilter.TV_SHOWS) },
            modifier = Modifier.weight(1f)
        )
    }
}