package com.example.mda.ui.screens.profile.history

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import com.example.mda.R
import com.example.mda.ui.navigation.TopBarState
import java.text.SimpleDateFormat
import java.util.*

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
                showBackButton = true,
                actions = {
                    IconButton(onClick = { moviesHistoryViewModel.clearHistory() }) {
                        Icon(Icons.Default.Delete, contentDescription = null)
                    }
                }
            )
        )
    }

    if (moviesHistory.isEmpty()) {
        emptyScreen("No movie viewing history found.", "Try watching some movies!")
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp, vertical = 12.dp)
        ) {
            items(moviesHistory) { movie ->

                val imageUrl = "https://image.tmdb.org/t/p/w500${movie.backdropPath ?: movie.posterPath ?: ""}"

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        .clickable {
                            navController.navigate("detail/${movie.mediaType ?: "movie"}/${movie.id}")
                        }
                        .padding(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // ===== Movie Poster =====
                        Box(
                            modifier = Modifier
                                .width(120.dp)
                                .height(160.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(MaterialTheme.colorScheme.onSurfaceVariant),
                            contentAlignment = Alignment.Center
                        ) {
                            val painter = rememberAsyncImagePainter(model = imageUrl)
                            val state = painter.state

                            AsyncImage(
                                model = imageUrl,
                                contentDescription = movie.name,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.matchParentSize(),
                                error = rememberAsyncImagePainter(R.drawable.person_placeholder)
                            )

                            if (state is AsyncImagePainter.State.Loading) {
                                androidx.compose.material3.CircularProgressIndicator(
                                    strokeWidth = 2.dp,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        // ===== Movie Details =====
                        Column(
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier.align(Alignment.CenterVertically)
                        ) {
                            Text(
                                text = movie.name ?: "Unknown Movie",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onBackground,
                                fontWeight = FontWeight.Bold,
                                maxLines = 2
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "Viewed on: " + SimpleDateFormat(
                                    "dd MMM yyyy",
                                    Locale.getDefault()
                                ).format(Date(movie.viewedAt)),
                                style = MaterialTheme.typography.bodyMedium,
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
            item {
                Spacer(modifier = Modifier.height(100.dp))
            }
        }
    }
}