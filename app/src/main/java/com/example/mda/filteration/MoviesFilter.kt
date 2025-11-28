package com.example.mda.filteration

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import com.example.mda.ui.screens.genreDetails.GenreDetailsViewModel

// ✅ Define your filter options
val filterOptions = FilterType.values().map { type ->
    when (type) {
        FilterType.ALL -> "All Movies"
        FilterType.TOP_RATED -> "Top Rated"
        FilterType.NEWEST -> "Newest"
        FilterType.POPULAR -> "Most Popular"
        FilterType.FAMILY_FRIENDLY -> "Family Friendly"
    } to type
}

enum class FilterType {
    ALL,
    TOP_RATED,
    NEWEST,
    POPULAR,
    FAMILY_FRIENDLY
}

@Composable
fun FilterDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    viewModel: GenreDetailsViewModel
) {
    if (!showDialog) return

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Filter Movies") },
        text = {
            Column {
                filterOptions.forEach { (label, type) ->
                    TextButton(
                        onClick = {
                            viewModel.setFilterType(type)
                            onDismiss()
                        }
                    ) {
                        val isSelected = viewModel.currentFilter == type
                        Text(text = if (isSelected) "✅ $label" else label)
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Close") }
        }
    )
}