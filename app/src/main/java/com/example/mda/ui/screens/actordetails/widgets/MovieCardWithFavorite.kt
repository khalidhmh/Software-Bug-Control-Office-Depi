package com.example.mda.ui.screens.actordetails.widgets

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.mda.data.remote.model.Credit
import com.example.mda.data.remote.model.Movie
import com.example.mda.ui.screens.favorites.FavoritesViewModel
import com.example.mda.ui.screens.favorites.components.FavoriteButton

@Composable
fun MovieCardWithFavorite(
    navController: NavController,
    title: String,
    posterUrl: String?,
    role: String,
    movie: Credit,
    favoritesViewModel: FavoritesViewModel
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal =  16.dp, vertical = 8.dp)
            .height(110.dp)
            .clickable(
                onClick = {
                    navController.navigate("detail/${movie.media_type ?: "movie"}/${movie.id}")
                })
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = rememberAsyncImagePainter(
                    posterUrl?.let { "https://image.tmdb.org/t/p/w500$it" }
                        ?: "https://via.placeholder.com/150"
                ),
                contentDescription = title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .width(80.dp)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp))
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = title,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = role,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 13.sp
                )
            }

            // Favorite Button
            Box(
                modifier = Modifier.padding(end = 8.dp)
            ) {
                // Convert Credit to Movie
                val movieData = Movie(
                    id = movie.id,
                    title = movie.title,
                    name = movie.name,
                    overview = "",
                    posterPath = movie.poster_path,
                    backdropPath = null,
                    releaseDate = null,
                    firstAirDate = null,
                    voteAverage = 0.0,
                    mediaType = movie.media_type,
                    adult = false,
                    genreIds = null
                )

                FavoriteButton(
                    movie = movieData,
                    viewModel = favoritesViewModel,
                    showBackground = false
                )
            }
        }
    }
}
