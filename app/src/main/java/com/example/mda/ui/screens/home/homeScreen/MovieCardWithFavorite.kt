package com.example.mda.ui.screens.home.homeScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.mda.data.remote.model.Movie

@Composable
fun MovieCardWithFavorite(
    movie: Movie,
    onClick: (Movie) -> Unit,
    favoriteButton: @Composable () -> Unit
) {
    Card(
        onClick = { onClick(movie) },
        modifier = Modifier
            .width(150.dp)
            .aspectRatio(0.70f),
        shape = RoundedCornerShape(12.dp)
    ) {
        Box {
            AsyncImage(
                model = "https://image.tmdb.org/t/p/w500${movie.posterPath}",
                contentDescription = movie.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // تقييم فوق يسار
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(6.dp)
                    .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(6.dp))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = "⭐ ${String.format("%.1f", movie.voteAverage)}",
                    color = Color.White,
                    fontSize = 12.sp
                )
            }

            // زر القلب فوق يمين
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(6.dp)
            ) {
                favoriteButton()
            }


        }
    }
}
