package com.example.mda.ui.genreScreen

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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.mda.R // Make sure to import your R class for drawables
import com.example.mda.data.repository.MoviesRepository
import com.example.mda.util.GenreViewModelFactory
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberTopAppBarState
import androidx.annotation.DrawableRes
import com.example.mda.data.remote.model.Genre

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GenreScreen(
    navController: NavController,
    repository: MoviesRepository
) {
    val viewModel: GenreViewModel = viewModel(
        factory = GenreViewModelFactory(repository)
    )

    val genres = viewModel.genres
    val isLoading = viewModel.isLoading
    val error = viewModel.error

    LaunchedEffect(Unit) {
        viewModel.loadGenres()
    }

    Scaffold(
        modifier = Modifier.background(Color(0xFF101528)),
        topBar = {
            Column {
                // 🔹 TopAppBar ثابت بلون متناسق
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

                // ✨ Divider بسيط شفاف يفصل بين التوب بار والمحتوى
                Divider(
                    color = Color.White.copy(alpha = 0.15f), // شفاف خفيف جدًا
                    thickness = 1.dp
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
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Something went wrong",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White
                        )
                        Text(
                            text = error,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }

                else -> {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 24.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(genres, key = { it.id }) { genre ->
                            GenreGridCard(
                                genre = genre,
                                imageUrl = getGenrePlaceholderImage(genre.name)
                            ) {
                                navController.navigate("genre_details/${genre.id}/${genre.name}")
                            }
                        }
                    }
                }
            }
        }
    }
}

// 🎨 كروت الأنواع بنفس اللون الجديد
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


// Helper function to get a local drawable resource for a genre
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