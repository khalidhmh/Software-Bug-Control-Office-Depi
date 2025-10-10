package com.example.mda.ui.Screens.MovieDetail

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.mda.data.remote.model.MovieDetailsResponse
import com.example.mda.data.repository.MoviesRepository


@Composable
fun MovieDetailsScreen(
    movieId: Int,
    navController: NavController,
    repository: MoviesRepository // pass repository from the host (or obtain via DI)
) {
    // create ViewModel using project factory style
    val factory = MovieDetailsViewModelFactory(repository)
    val viewModel: MovieDetailsViewModel = viewModel(factory = factory)

    val details by viewModel.movieDetails.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    LaunchedEffect(movieId) {
        viewModel.loadMovieDetails(movieId)
    }

    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    error?.let {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
            Text(text = "Error: $it")
        }
        return
    }

    details?.let { movie ->
        MovieDetailsContent(movie)
    } ?: Box(modifier = Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
        Text(text = "No details")
    }
}

@Composable
fun MovieDetailsContent(movie: MovieDetailsResponse) {
    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .fillMaxSize()
            .padding(16.dp)
    ) {
        val imageUrl = "https://image.tmdb.org/t/p/w780${movie.backdropPath ?: movie.posterPath}"
        Image(
            painter = rememberAsyncImagePainter(imageUrl),
            contentDescription = movie.title,
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(text = movie.title, style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = " ${movie.voteAverage} • ${movie.releaseDate ?: "N/A"}", style = MaterialTheme.typography.bodyMedium)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = movie.overview.toString(), style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.height(12.dp))

        movie.genres?.let { genres ->
            FlowRowGenres(genres.map { it.name })
        }
        Spacer(modifier = Modifier.height(12.dp))

        // Show first trailer if exists
        val trailerKey = movie.videos?.results?.firstOrNull { it.site.equals("YouTube", true) && it.type.equals("Trailer", true) }?.key
        trailerKey?.let { key ->
            Text(text = "Trailer available", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            // you can add a button to open YouTube using Intent
            Button(onClick = {
                // Open external link: https://www.youtube.com/watch?v=$key
            }) {
                Text(text = "Watch Trailer")
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun FlowRowGenres(genres: List<String>) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        genres.forEach { g ->
            AssistChip(onClick = {}, label = { Text(g) })
        }
    }
}