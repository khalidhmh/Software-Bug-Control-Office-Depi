package com.example.mda.ui.screens.profile.favourites

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items

import com.example.mda.data.local.entities.MediaEntity
import com.example.mda.data.remote.model.Movie
import com.example.mda.ui.navigation.TopBarState
import com.example.mda.ui.screens.auth.AuthViewModel
import com.example.mda.ui.screens.favorites.FavoritesViewModel

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mda.data.repository.mappers.toMovie
import com.example.mda.ui.screens.actors.ViewType
import com.example.mda.ui.screens.favorites.components.FavoriteButton
import com.example.mda.ui.screens.home.homeScreen.MovieCardWithFavorite
import com.example.mda.ui.screens.profile.history.emptyScreen


@Composable
fun FavoritesScreen(
    navController: NavController,
    favoritesViewModel: FavoritesViewModel,
    onTopBarStateChange: (TopBarState) -> Unit
) {
    val favorites by favoritesViewModel.favorites.collectAsState()

    LaunchedEffect(Unit) {
        onTopBarStateChange(
            TopBarState(
                title = "Favourites",
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {}
            )
        )
    }


    // Favorites Section
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {


            if (favorites.isNotEmpty()) {
                Text(
                    text = "${favorites.size} ${if (favorites.size == 1) "movie" else "movies"}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (favorites.isEmpty()) {
            // Empty state
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(200.dp)
                    .background(
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                        shape = MaterialTheme.shapes.medium
                    ),
                contentAlignment = Alignment.Center,

                ) {
                emptyScreen(
                    "No favorite movies yet",
                    "Start adding your favorite movies from the home screen"
                )

            }
        } else {

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 8.dp, bottom = 100.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
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
                                showBackground = true
                            )
                        }
                    )
                }
            }



            Spacer(modifier = Modifier.height(16.dp))
        }


        // Extension function to convert MediaEntity to Movie
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
    }
}
