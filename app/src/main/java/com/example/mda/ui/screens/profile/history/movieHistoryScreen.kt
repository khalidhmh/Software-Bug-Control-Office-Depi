package com.example.mda.ui.screens.profile.history

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.example.mda.ui.navigation.TopBarState
import java.util.Date

@Composable
fun MoviesHistoryScreen(
    navController: NavController,
    onTopBarStateChange: (TopBarState) -> Unit,
    moviesHistoryViewModel: MoviesHistoryViewModel
) {

    val moviesHistory by moviesHistoryViewModel.history.observeAsState(emptyList())

    LaunchedEffect(Unit) {
        onTopBarStateChange(
            TopBarState(
                title = "Movies History",
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { moviesHistoryViewModel.clearHistory() }) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = null
                        )
                    }
                }
            )
        )
    }
    if (moviesHistory.isEmpty()) {
        emptyScreen("No movie viewing history found.", "Try watching some movies!")
    }
    LazyColumn(modifier = Modifier.fillMaxWidth().padding(bottom = 50.dp)) {
        items(moviesHistory) { movie ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(all = 16.dp).clickable {// will navigate to Movie details screen (Fares)
                        navController.navigate("detail/${movie.mediaType ?: "movie"}/${movie.id}")
                    }

            ) {
                val img =
                    "https://image.tmdb.org/t/p/original${movie.backdropPath ?: movie.posterPath ?: ""}"
                // Poster
                Image(
                    painter = rememberAsyncImagePainter(img),
                    contentDescription = movie.name ?: "",
                    modifier = Modifier
                        .width(120.dp)       // Set your desired width
                        .height(180.dp),     // Set your desired height
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.width(8.dp))
                // Name & viewed time
                Column {
                    Text(text = movie.name ?: "Unknown")
                    Text(
                        text = "Viewed: ${Date(movie.viewedAt)}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}
