package com.example.mda.ui.screens.home.homeScreen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.mda.data.remote.model.Movie
import com.example.mda.ui.screens.favorites.FavoritesViewModel
import com.example.mda.ui.screens.favorites.components.FavoriteButton

@Composable
fun PopularSection(
    popularMovies: List<Movie>,
    onMovieClick: (Movie) -> Unit,
    onViewMoreClick: () -> Unit,
    favoritesViewModel: FavoritesViewModel,
    navController: NavController,
    isAuthenticated: Boolean
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = com.example.mda.localization.localizedString(com.example.mda.localization.LocalizationKeys.HOME_POPULAR_MOVIES),
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
            ViewMoreButton(onClick = onViewMoreClick)
        }

        Spacer(modifier = Modifier.height(8.dp))

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(popularMovies) { movie ->
                MovieCardWithFavorite(
                    movie = movie,
                    onClick = onMovieClick,
                    favoriteButton = {
                        FavoriteButton(
                            movie = movie,
                            viewModel = favoritesViewModel,
                            showBackground = true,
                            isAuthenticated = isAuthenticated,
                            onLoginRequired = { navController.navigate("settings") }
                        )
                    }
                )
            }
        }
    }
}

