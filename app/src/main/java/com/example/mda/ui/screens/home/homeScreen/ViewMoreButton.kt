package com.example.mda.ui.screens.home.homeScreen

import androidx.compose.material3.*
import androidx.compose.runtime.Composable

@Composable
fun ViewMoreButton(onClick: () -> Unit) {
    TextButton(
        onClick = onClick,
        colors = ButtonDefaults.textButtonColors(
            contentColor = MaterialTheme.colorScheme.primary
        )
    ) {
        Text(
            text = "View More",
            style = MaterialTheme.typography.labelMedium
        )
    }
}
