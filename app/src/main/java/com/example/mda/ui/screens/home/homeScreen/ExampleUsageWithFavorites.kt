package com.example.mda.ui.screens.home.homeScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.mda.data.remote.model.Movie
import com.example.mda.ui.screens.favorites.FavoritesViewModel
import com.example.mda.ui.screens.favorites.components.FavoriteButton

/**
 * مثال على كيفية استخدام MovieCardWithFavorite في أي Section
 * 
 * استخدم هذا المثال في:
 * - TrendingSection.kt
 * - PopularSection.kt
 * - ForYouSection.kt
 * - أي مكان آخر تعرض فيه قائمة أفلام
 */

@Composable
fun ExampleSectionWithFavorites(
    movies: List<Movie>,
    navController: NavController,
    favoritesViewModel: FavoritesViewModel,
    isAuthenticated: Boolean
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        items(movies) { movie ->
            // استخدم MovieCardWithFavorite بدلاً من MovieCard
            MovieCardWithFavorite(
                movie = movie,
                onClick = {
                    val type = movie.mediaType ?: "movie"
                    navController.navigate("detail/$type/${movie.id}")
                },
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

/**
 * مثال آخر: استخدام في Grid
 */
@Composable
fun ExampleGridWithFavorites(
    movies: List<Movie>,
    navController: NavController,
    favoritesViewModel: FavoritesViewModel,
    isAuthenticated: Boolean
) {
    androidx.compose.foundation.lazy.grid.LazyVerticalGrid(
        columns = androidx.compose.foundation.lazy.grid.GridCells.Fixed(2),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(movies.size) { index ->
            val movie = movies[index]
            MovieCardWithFavorite(
                movie = movie,
                onClick = {
                    val type = movie.mediaType ?: "movie"
                    navController.navigate("detail/$type/${movie.id}")
                },
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

/**
 * ملاحظات مهمة:
 * 
 * 1. لاستخدام FavoriteButton في أي شاشة، تحتاج إلى:
 *    - تمرير favoritesViewModel كـ parameter للشاشة
 *    - استيراد FavoriteButton و MovieCardWithFavorite
 * 
 * 2. في HomeScreen، يمكنك الحصول على favoritesViewModel من MainActivity
 *    عن طريق تمريره كـ parameter
 * 
 * 3. FavoriteButton يعمل بشكل مستقل ولا يحتاج إلى state management إضافي
 * 
 * 4. Snackbar يظهر تلقائياً من ViewModel
 * 
 * 5. البيانات تحفظ في Room Database تلقائياً
 */
