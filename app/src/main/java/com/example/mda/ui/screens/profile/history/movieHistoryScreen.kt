package com.example.mda.ui.screens.profile.history

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import com.example.mda.ui.navigation.TopBarState
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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
    LazyColumn(modifier = Modifier
        .fillMaxWidth()
        .padding(bottom = 50.dp)) {
        items(moviesHistory) { movie ->

            val imageUrl =
                "https://image.tmdb.org/t/p/w500${movie.backdropPath ?: movie.posterPath ?: ""}"

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .clickable {
                        navController.navigate("detail/${movie.mediaType ?: "movie"}/${movie.id}")
                    },
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                ) {

                    // Poster with loading/error state
                    Box(
                        modifier = Modifier
                            .width(120.dp)
                            .height(160.dp)
                            .clip(RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        val painter = rememberAsyncImagePainter(model = imageUrl)
                        val state = painter.state

                        Image(
                            painter = painter,
                            contentDescription = movie.name,
                            modifier = Modifier.matchParentSize(),
                            contentScale = ContentScale.Crop
                        )

                        when (state) {
                            is AsyncImagePainter.State.Loading -> {
                                CircularProgressIndicator(
                                    strokeWidth = 2.dp,
                                    modifier = Modifier.size(32.dp)
                                )
                            }

                            is AsyncImagePainter.State.Error -> {
                                Text(
                                    "No Image",
                                    color = Color.Gray,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }

                            else -> Unit
                        }
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    // Movie text information
                    Column(
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                    ) {

                        Text(
                            text = movie.name ?: "Unknown Movie",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "Viewed on: ${
                                SimpleDateFormat(
                                    "dd MMM yyyy",
                                    Locale.getDefault()
                                ).format(Date(movie.viewedAt))
                            }",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }
                }
            }
        }

    }
}
