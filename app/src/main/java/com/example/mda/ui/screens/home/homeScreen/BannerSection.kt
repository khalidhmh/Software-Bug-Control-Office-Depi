package com.example.mda.ui.screens.home.homeScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.mda.data.remote.model.Movie
import kotlinx.coroutines.delay
import androidx.compose.animation.Crossfade

@Composable
fun BannerSection(
    movies: List<Movie>,
    slideIntervalMs: Long = 8000L // 8 ثواني
) {
    if (movies.isEmpty()) {
        // حماية من الكراش لو القائمة فاضية
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .background(Color.DarkGray),
            contentAlignment = Alignment.Center
        ) {
            Text("No movies to show", color = Color.White)
        }
        return
    }

    var currentIndex by remember { mutableStateOf(0) }

    // التأثير اللي يغير الفيلم كل فترة
    LaunchedEffect(movies) {
        while (true) {
            delay(slideIntervalMs)
            currentIndex = (currentIndex + 1) % movies.size
        }
    }

    val movie = movies[currentIndex]

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color.Black),
        contentAlignment = Alignment.BottomStart
    ) {
        // صورة الفيلم (بوستر)
        Crossfade(targetState = movie) { currentMovie ->
            AsyncImage(
                model = "https://image.tmdb.org/t/p/w780${currentMovie.posterPath}",
                contentDescription = currentMovie.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }


        // الكتابة فوق الصورة
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0x66000000))
                .padding(12.dp)
        ) {
            Text(
                text = movie.title ?: movie.name ?: "Unknown",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = movie.overview ?: "",
                color = Color.LightGray,
                maxLines = 2
            )
        }
    }
}
