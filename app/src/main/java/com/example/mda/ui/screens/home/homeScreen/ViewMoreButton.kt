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
            text = com.example.mda.localization.localizedString(com.example.mda.localization.LocalizationKeys.COMMON_SHOW_MORE),
            style = MaterialTheme.typography.labelMedium
        )
    }
}
