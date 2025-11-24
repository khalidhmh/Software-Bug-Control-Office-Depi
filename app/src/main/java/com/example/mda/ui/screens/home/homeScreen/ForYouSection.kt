package com.example.mda.ui.screens.home.homeScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mda.data.remote.model.Movie
import com.example.mda.ui.screens.favorites.FavoritesViewModel
import com.example.mda.ui.screens.favorites.components.FavoriteButton

@Composable
fun ForYouSection(
    recommendedMovies: List<Movie>,   // 🔹 توصيات أفلام
    recommendedTvShows: List<Movie>,  // 🔹 توصيات مسلسلات
    onMovieClick: (Movie) -> Unit,
    favoritesViewModel: FavoritesViewModel
) {
    val barColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f)
    val barOverlayColor = barColor.copy(alpha = 0.6f)
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Movies", "TV Shows")

    Column {
        Text(
            text = "For You",
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp
        )
        Spacer(modifier = Modifier.height(12.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
        ) {
            Column {
                // ✅ تبويبات Movies / TV Shows
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = barOverlayColor,
                    contentColor = MaterialTheme.colorScheme.primary
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            text = { Text(title) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // ✅ كل تبويب يعرض التوصيات الخاصة به
                val itemsToShow = if (selectedTab == 0) recommendedMovies else recommendedTvShows

                if (itemsToShow.isEmpty()) {
                    Text(
                        text = "No recommendations yet.",
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                        fontSize = 14.sp,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                } else {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(itemsToShow) { item ->
                            MovieCardWithFavorite(
                                movie = item,
                                onClick = onMovieClick,
                                favoriteButton = {
                                    FavoriteButton(
                                        movie = item,
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
    }
}