package com.example.mda.ui.screens.profile.favourites

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.mda.data.local.entities.MediaEntity
import com.example.mda.data.remote.model.Movie
import com.example.mda.ui.navigation.TopBarState
import com.example.mda.ui.screens.auth.AuthViewModel
import com.example.mda.ui.screens.favorites.FavoritesViewModel
import com.example.mda.ui.screens.favorites.components.FavoriteButton
import com.example.mda.ui.screens.home.homeScreen.MovieCardWithFavorite
import com.example.mda.ui.screens.profile.history.IconType
import com.example.mda.ui.screens.profile.history.emptyScreen

@Composable
fun FavoritesScreen(
    navController: NavController,
    favoritesViewModel: FavoritesViewModel,
    onTopBarStateChange: (TopBarState) -> Unit,
    authViewModel: AuthViewModel
) {
    val favorites by favoritesViewModel.favorites.collectAsState()
    val authUiState by authViewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        onTopBarStateChange(
            TopBarState(
                title = "Favorites",
                showBackButton = true,
                actions = {}
            )
        )
    }

    // ===== الصفحة بالكامل =====
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {

        // ===== Title =====
        if (favorites.isNotEmpty()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${favorites.size} ${if (favorites.size == 1) "movie" else "movies"}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ===== الحالة الفارغة =====
        if (favorites.isEmpty()) {
            emptyScreen(
                title = "No favorite movies yet",
                subtitle = "Start adding your favorite movies from the home screen",
                iconType = IconType.MOVIE
            )
        } else {
            // ===== جريد المفضلات =====
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 8.dp),
                contentPadding = PaddingValues(bottom = 100.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(favorites) { mediaEntity ->
                    val movie = mediaEntity.toMovie()

                    MovieCardWithFavorite(
                        movie = movie,
                        onClick = {
                            val type = mediaEntity.mediaType ?: "movie"
                            navController.navigate("detail/$type/${mediaEntity.id}")
                        },
                        favoriteButton = {
                            FavoriteButton(
                                movie = movie,
                                viewModel = favoritesViewModel,
                                showBackground = true,
                                isAuthenticated = authUiState.isAuthenticated,
                                onLoginRequired = { navController.navigate("settings") }
                            )
                        }
                    )
                }
                // ✅ Spacer تحت الجريد عشان يسيب مساحة عند آخر الصفحة
                item { Spacer(modifier = Modifier.height(80.dp)) }
            }
        }
    }
}

// ✅ تحويل MediaEntity إلى Movie
fun MediaEntity.toMovie(): Movie {
    return Movie(
        id = this.id,
        title = this.title,
        name = this.name,
        overview = this.overview,
        posterPath = this.posterPath,
        backdropPath = this.backdropPath,
        releaseDate = this.releaseDate,
        firstAirDate = this.firstAirDate,
        voteAverage = this.voteAverage ?: 0.0,
        mediaType = this.mediaType,
        adult = this.adult,
        genreIds = this.genreIds
    )
}