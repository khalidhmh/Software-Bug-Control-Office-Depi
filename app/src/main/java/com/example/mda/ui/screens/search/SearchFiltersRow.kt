package com.example.mda.ui.screens.search

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.mda.localization.LocalizationKeys
import com.example.mda.localization.localizedString

@Composable
fun SearchFiltersRow(
    selectedFilter: String,
    onFilterChange: (String) -> Unit
) {
    val filters = listOf("all", "movies", "tv", "people")

    Row(
        Modifier
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        filters.forEach { filter ->
            val selected = selectedFilter.equals(filter, ignoreCase = true)

            FilterChip(
                selected = selected,
                onClick = { onFilterChange(filter) },
                label = {
                    Text(
                        when (filter) {
                            "all" -> localizedString(LocalizationKeys.SEARCH_FILTER_ALL)
                            "movies" -> localizedString(LocalizationKeys.SEARCH_FILTER_MOVIES)
                            "tv" -> localizedString(LocalizationKeys.SEARCH_FILTER_TV)
                            "people" -> localizedString(LocalizationKeys.SEARCH_FILTER_PEOPLE)
                            else -> filter.replaceFirstChar { it.uppercaseChar() }
                        },
                        color = if (selected)
                            MaterialTheme.colorScheme.onPrimary
                        else
                            MaterialTheme.colorScheme.onSurface
                    )
                },
                shape = RoundedCornerShape(20.dp),
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    labelColor = MaterialTheme.colorScheme.onSurface,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                ),
                border = FilterChipDefaults.filterChipBorder(
                    enabled = true,
                    selected = selected,
                    borderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                    selectedBorderColor = Color.Transparent,
                    borderWidth = 1.dp,
                    selectedBorderWidth = 0.dp
                )
            )
        }
    }
}