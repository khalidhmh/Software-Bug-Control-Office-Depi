package com.example.mda.ui.screens.favorites.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.mda.data.remote.model.Movie
import com.example.mda.ui.screens.favorites.FavoritesViewModel

@Composable
fun FavoriteButton(
    movie: Movie,
    navController: NavController,
    viewModel: FavoritesViewModel,
    modifier: Modifier = Modifier,
    showBackground: Boolean = true,
    isAuthenticated: Boolean
) {
    // استخدام favorites list من الـ ViewModel للحصول على reactive state
    val favorites by viewModel.favorites.collectAsState()
    val isFavorite = favorites.any { it.id == movie.id && it.isFavorite }

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
                if (!isAuthenticated) {
                    navController.navigate("profile")
                }else{
                    viewModel.toggleFavorite(movie)
                }

                viewModel.toggleFavorite(movie)
            },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
            contentDescription = if (isFavorite) "Remove from favorites" else "Add to favorites",
            tint = if (isFavorite) Color.Red else Color.Gray,
            modifier = Modifier.size(24.dp)
        )
    }
}

