package com.example.mda.ui.screens.genre

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.mda.R
import com.example.mda.data.remote.model.Genre
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GenreScreen(
    navController: NavController,
    viewModel: GenreViewModel
) {
    val genres by viewModel.genres.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val scope = rememberCoroutineScope()

    Scaffold(
        modifier = Modifier.background(Color(0xFF101528)),
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        Text(
                            text = "Discover Genres 🎬",
                            color = Color.White
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color(0xFF1A2233),
                        titleContentColor = Color.White
                    )
                )
                HorizontalDivider(
                    thickness = 1.dp,
                    color = Color.White.copy(alpha = 0.15f)
                )
            }
        }
    ) { padding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFF101528))
        ) {
            when {
                isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Color.White)
                    }
                }

                error != null -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.CloudOff,
                            contentDescription = "Error",
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Something went wrong",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White
                        )
                        Text(
                            text = error ?: "",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        Button(
                            onClick = { scope.launch { viewModel.refreshGenres() } },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1A2233))
                        ) {
                            Text("Retry", color = Color.White)
                        }
                    }
                }

                else -> {
                    LazyVerticalGrid(
                        contentPadding = PaddingValues(bottom = 106.dp, start = 16.dp, end = 16.dp, top = 24.dp),
                        columns = GridCells.Fixed(2),
//                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 24.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(genres, key = { it.id }) { genre ->
                            GenreGridCard(
                                genre = genre,
                                imageUrl = getGenrePlaceholderImage(genre.name)
                            ) {
                                navController.navigate("moviesByGenre/${genre.id}")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun GenreGridCard(genre: Genre, @DrawableRes imageUrl: Int, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = Color(0xFF191E2A)),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
            ) {
                Image(
                    painter = painterResource(id = imageUrl),
                    contentDescription = "${genre.name} icon",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.25f)),
                                startY = 0f,
                                endY = 1000f
                            )
                        )
                )
            }

            Text(
                text = genre.name,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 12.dp)
            )
        }
    }
}

// Helper function to get drawable image for each genre
@DrawableRes
fun getGenrePlaceholderImage(genreName: String): Int {
    return when (genreName.lowercase()) {
        "action" -> R.drawable.action
        "adventure" -> R.drawable.adventure
        "animation" -> R.drawable.anime
        "comedy" -> R.drawable.comedy
        "crime" -> R.drawable.crime
        "documentary" -> R.drawable.documentary
        "drama" -> R.drawable.drama
        "family" -> R.drawable.family
        "fantasy" -> R.drawable.fantasy
        "history" -> R.drawable.history
        "horror" -> R.drawable.horror
        "music" -> R.drawable.musical
        "mystery" -> R.drawable.mystery
        "romance" -> R.drawable.romance
        "science fiction" -> R.drawable.scifi
        "tv movie" -> R.drawable.psychology
        "thriller" -> R.drawable.thriller
        "war" -> R.drawable.war
        "western" -> R.drawable.western
        else -> R.drawable.family
    }
}
