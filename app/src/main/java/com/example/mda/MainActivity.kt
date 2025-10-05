package com.example.mda

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.mda.data.remote.RetrofitInstance
import com.example.mda.data.remote.api.TmdbApi
import com.example.mda.data.repository.MoviesRepository
import com.example.mda.ui.genreScreen.GenreScreen
import com.example.mda.ui.moivebygenrescreen.GenreDetailsScreen
import com.example.mda.ui.theme.MovieAppTheme

class MainActivity : ComponentActivity() {

    // ✅ إنشاء Repository مرة واحدة
    private val moviesRepository = MoviesRepository(api = RetrofitInstance.api)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MovieAppTheme {
                Surface(
                    modifier = Modifier,
                    color = MaterialTheme.colorScheme.background
                ) {
                    MovieAppNav(moviesRepository)
                }
            }
        }
    }
}

@Composable
fun MovieAppNav(repository: MoviesRepository) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "genres"
    ) {
        // 🟢 شاشة الجينرا الرئيسية
        composable("genres") {
            GenreScreen(
                navController = navController,
                repository = repository // ⚡️ تمرير Repository
            )
        }

        // 🔵 شاشة تفاصيل الجينرا (الأفلام)
        composable(
            route = "genre_details/{genreId}/{genreName}",
            arguments = listOf(
                navArgument("genreId") { type = NavType.IntType },
                navArgument("genreName") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val genreId = backStackEntry.arguments?.getInt("genreId") ?: 0
            val genreName = backStackEntry.arguments?.getString("genreName") ?: ""
            GenreDetailsScreen(
                navController = navController,
                repository = repository, // ⚡️ تمرير Repository
                genreId = genreId,
                genreNameRaw = genreName
            )
        }
    }
}
