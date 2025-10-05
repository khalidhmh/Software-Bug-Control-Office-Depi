package com.example.mda.ui.moivebygenrescreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.mda.data.repository.MoviesRepository
import com.example.mda.ui.components.MovieCardGrid
import com.example.mda.util.GenreDetailsViewModelFactory
import java.net.URLDecoder
import java.nio.charset.StandardCharsets
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GenreDetailsScreen(
    navController: NavController,
    repository: MoviesRepository,
    genreId: Int,
    genreNameRaw: String
) {
    val genreName = URLDecoder.decode(genreNameRaw, StandardCharsets.UTF_8.toString())

    val viewModel: GenreDetailsViewModel = viewModel(
        factory = GenreDetailsViewModelFactory(repository)
    )

    val movies = viewModel.movies
    val isLoading = viewModel.isLoading
    val error = viewModel.error

    LaunchedEffect(genreId) {
        viewModel.loadMoviesByGenre(genreId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("$genreName Movies") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = androidx.compose.material.icons.Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            when {
                isLoading -> Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) { CircularProgressIndicator() }

                error != null -> Text(
                    text = "Error: $error",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.Center)
                )

                else -> LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(movies) { movie ->
                        MovieCardGrid(movie = movie, onClick ={})
                    }
                }
            }
        }
    }
}
