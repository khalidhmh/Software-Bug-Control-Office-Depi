package com.example.mda

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.mda.data.remote.api.TmdbApi
import com.example.mda.ui.genreScreen.GenreScreen
import com.example.mda.ui.moivebygenrescreen.GenreDetailsScreen
import com.example.mda.ui.theme.MovieAppTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.mda.data.remote.RetrofitInstance
import com.example.mda.data.repository.MoviesRepository
import com.example.mda.ui.Screens.home.HomeViewModel
import com.example.mda.ui.Screens.home.HomeViewModelFactory
import com.example.mda.ui.navigation.AnimatedNavigationBar
import com.example.mda.ui.navigation.ButtonData
import com.example.mda.ui.navigation.MdaNavHost

class MainActivity : ComponentActivity() {

    // ✅ إنشاء Repository مرة واحدة
    private val moviesRepository = MoviesRepository(RetrofitInstance.api)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MovieAppTheme {
                // ⚡️ إنشاء الـ ViewModel مع Factory
                val factory = HomeViewModelFactory(moviesRepository)
                val homeViewModel: HomeViewModel = viewModel(factory = factory)

                // ⚡️ إنشاء NavController
                val navController = rememberNavController()

                Scaffold(
                    bottomBar = {
                        val buttons = listOf(
                            ButtonData("home", "Home", Icons.Default.Home),
                            ButtonData("movies", "Movies", Icons.Default.Movie),
                            ButtonData("Actors", "People", Icons.Default.People),
                            ButtonData("profile", "Profile", Icons.Default.Person),
                        )
                        AnimatedNavigationBar(
                            navController = navController,
                            buttons = buttons,
                            barColor = Color(0xFF101528),
                            circleColor = Color(0xFF1E2238),
                            selectedColor = Color(0xFF4FC3F7),
                            unselectedColor = Color.Gray,
                        )
                    },
                    containerColor = Color(0xFF101528)
                ) { innerPadding ->
                    Box(modifier = Modifier.padding(0.dp)) {
                        // 🔗 تمرير الـ ViewModel أو Repository حسب الحاجة
                        MdaNavHost(
                            navController = navController,
                            homeViewModel = homeViewModel,
                            repository = moviesRepository
                        )
                    }
                }
            }
        }
    }
}

