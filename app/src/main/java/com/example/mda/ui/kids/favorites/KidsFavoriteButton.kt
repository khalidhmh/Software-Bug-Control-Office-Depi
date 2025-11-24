package com.example.mda.ui.kids.favorites

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@Composable
fun KidsFavoriteButton(
    id: Int,
    modifier: Modifier = Modifier,
    showBackground: Boolean = true,
) {
    val context = LocalContext.current
    val isFavorite by KidsFavoritesStore.isFavoriteFlow(context, id).collectAsState(initial = false)
    val scope = rememberCoroutineScope()

    Box(
        modifier = modifier
            .size(40.dp)
            .then(
                if (showBackground) {
                    Modifier.background(
                        color = Color.Black.copy(alpha = 0.6f),
                        shape = CircleShape
                    )
                } else Modifier
            )
            .clickable { 
                scope.launch { KidsFavoritesStore.toggle(context, id) }
            },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
            contentDescription = if (isFavorite) "Remove from kids favorites" else "Add to kids favorites",
            tint = if (isFavorite) Color.Red else Color.Gray,
            modifier = Modifier.size(24.dp)
        )
    }
}
