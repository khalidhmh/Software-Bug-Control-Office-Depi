package com.example.mda

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Movie
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
import com.example.mda.ui.navigation.BottomNavigationBar
import com.example.mda.ui.navigation.ButtonData
import com.example.mda.ui.navigation.MdaNavHost

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                // 🧱 إنشاء الريبو والفاكتوري
                val repository = MoviesRepository(RetrofitInstance.api)
                val factory = HomeViewModelFactory(repository)

                // ⚙️ إنشاء الـ ViewModel داخل الـ Compose scope
                val homeViewModel: HomeViewModel = viewModel(factory = factory)

                // 🧭 إنشاء الـ NavController
                val navController = rememberNavController()

                // 🧩 Scaffold فيه الـ BottomBar والمحتوى
                Scaffold(
//                    bottomBar = {
//                        BottomNavigationBar(navController = navController)
//                    }
                    bottomBar = {
                        val buttons = listOf(
                            ButtonData("home", "Home", Icons.Default.Home),
                            ButtonData("movies", "Movies", Icons.Default.Movie),
                            ButtonData("tv", "TV", Icons.Default.Tv),
                            ButtonData("profile", "Profile", Icons.Default.Person),
                        )

                        AnimatedNavigationBar(
                            navController = navController,
                            buttons = buttons,
                            barColor = Color(0xFF101528),
                            circleColor = Color(0xFF1E2238),
                            selectedColor = Color(0xFF4FC3F7),
                            unselectedColor = Color.Gray
                        )
                    }

                ) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding.calculateTopPadding())) {
                        // 🔗 تمرير الـ ViewModel إلى NavHost
                        MdaNavHost(
                            navController = navController,
                            homeViewModel = homeViewModel
                        )
                    }
                }
            }
        }
    }
}
