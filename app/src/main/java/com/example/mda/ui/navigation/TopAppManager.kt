package com.example.mda.ui.navigation

import androidx.compose.foundation.layout.RowScope
import androidx.compose.runtime.Composable


data class TopBarState(
    val title: String = "",
    val subtitle: String? = null, // 👈 أضفنا السطر ده
    val showBackButton: Boolean = false,
    val actions: @Composable RowScope.() -> Unit = {}
)
