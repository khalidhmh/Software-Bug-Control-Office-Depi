package com.example.mda.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mda.ui.Screens.home.HomeScreen.BannerSection
import com.example.mda.ui.Screens.home.HomeScreen.ForYouSection
import com.example.mda.ui.Screens.home.HomeScreen.PopularSection
import com.example.mda.ui.Screens.home.HomeScreen.TrendingSection
import com.example.mda.ui.Screens.home.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(viewModel: HomeViewModel) {

    val trendingMovies = viewModel.trendingMovies.collectAsState(initial = emptyList()).value
    val popularMovies = viewModel.popularMovies.collectAsState(initial = emptyList()).value
    val popularTvShows = viewModel.popularTvShows.collectAsState(initial = emptyList()).value
    val popularMixed = viewModel.popularMixed.collectAsState(initial = emptyList()).value
    val topRatedMovies = viewModel.topRatedMovies.collectAsState(initial = emptyList()).value

    // 🔹 حفظ حالة Scroll
    val scrollState = rememberSaveable(saver = LazyListState.Saver) { LazyListState() }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF101528),
                    titleContentColor = Color.White,
                    actionIconContentColor = Color.White
                ),
                title = {
                    Column {
                        Text(
                            text = "Good Evening",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 25.sp
                        )
                        Text(
                            text = "What do you want to watch?",
                            color = Color.Gray,
                            fontSize = 18.sp
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {}) {
                        Icon(Icons.Filled.Person, contentDescription = "Profile", tint = Color.White)
                    }
                    IconButton(onClick = {}) {
                        Icon(Icons.Filled.Settings, contentDescription = "Settings", tint = Color.White)
                    }
                }
            )
        }
    ) { innerPadding ->

        LazyColumn(
            state = scrollState,
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF101528))
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 100.dp) // مساحة للـ BottomBar
        ) {

            // 🔹 Banner section (يظهر كامل أوله وآخره)
            item { BannerSection(movies = popularMixed) }

            // 🔹 ForYou section
            item {
                ForYouSection(
                    movies = popularMovies,
                    tvShows = popularTvShows,
                    onMovieClick = { /* navigate to detail */ }
                )
            }

            // 🔹 Trending section
            item {
                TrendingSection(
                    trendingMovies = trendingMovies,
                    selectedWindow = viewModel.selectedTimeWindow,
                    onTimeWindowChange = { timeWindow -> viewModel.loadTrending(timeWindow) },
                    onMovieClick = { /* navigate */ }
                )
            }

            // 🔹 Popular section
            item {
                PopularSection(
                    popularMovies = popularMixed,
                    onMovieClick = { /* navigate */ },
                    onViewMoreClick = { /* show all popular */ }
                )
            }
        }
    }
}
