package com.example.mda.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.mda.data.local.entities.MediaEntity
import com.example.mda.data.remote.model.Movie
import com.example.mda.ui.navigation.TopBarState
import com.example.mda.ui.screens.favorites.FavoritesViewModel
import com.example.mda.ui.screens.favorites.components.FavoriteButton
import com.example.mda.ui.screens.home.homeScreen.MovieCardWithFavorite

@Composable
fun ProfileScreen(
    navController: NavController,
    favoritesViewModel: FavoritesViewModel,
    onTopBarStateChange: (TopBarState) -> Unit
) {
    val favorites by favoritesViewModel.favorites.collectAsState()
    val snackbarMessage by favoritesViewModel.snackbarMessage.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        onTopBarStateChange(
            TopBarState(
                title = "Profile",
                navigationIcon = null,
                actions = {}
            )
        )
    }

    LaunchedEffect(snackbarMessage) {
        snackbarMessage?.let {
            snackbarHostState.showSnackbar(it)
            favoritesViewModel.clearSnackbarMessage()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Profile Header
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Profile Avatar
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Profile",
                        modifier = Modifier.size(60.dp),
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "User Profile",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Movie enthusiast",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                )
            }

            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 16.dp),
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1f)
            )

            Spacer(modifier = Modifier.height(24.dp))

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
                    Text(
                        text = "Favorites",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )

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
                            .height(200.dp)
                            .background(
                                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                                shape = MaterialTheme.shapes.medium
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "💔",
                                fontSize = 48.sp
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "لا توجد أفلام مفضلة بعد",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "ابدأ بإضافة أفلامك المفضلة من الصفحة الرئيسية",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                } else {
                    // Favorites LazyRow
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(vertical = 8.dp)
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
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

// Extension function to convert MediaEntity to Movie
private fun MediaEntity.toMovie(): Movie {
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
