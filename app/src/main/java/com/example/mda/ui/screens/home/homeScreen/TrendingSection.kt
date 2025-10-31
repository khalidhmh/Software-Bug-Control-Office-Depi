package com.example.mda.ui.screens.home.homeScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mda.data.remote.model.Movie
import com.example.mda.ui.screens.favorites.FavoritesViewModel
import com.example.mda.ui.screens.favorites.components.FavoriteButton

@Composable
fun TrendingSection(
    trendingMovies: List<Movie>,
    selectedWindow: String,
    onTimeWindowChange: (String) -> Unit,
    onMovieClick: (Movie) -> Unit,
    favoritesViewModel: FavoritesViewModel
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Trending",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )

            Row {
                val buttons = listOf("day" to "Day", "week" to "Week")
                buttons.forEach { (value, label) ->
                    Button(
                        onClick = { onTimeWindowChange(value) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (selectedWindow == value) Color.Cyan else Color.DarkGray
                        ),
                        modifier = Modifier.padding(horizontal = 4.dp)
                    ) {
                        Text(label, color = Color.White)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (trendingMovies.isEmpty()) {
            Text("Loading...", color = Color.LightGray)
        } else {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                items(trendingMovies) { m ->
                    MovieCardWithFavorite(
                        movie = m,
                        onClick = onMovieClick,
                        favoriteButton = {
                            FavoriteButton(
                                movie = m,
                                viewModel = favoritesViewModel,
                                showBackground = true
                            )
                        }
                    )
                }
            }
        }
    }
}
