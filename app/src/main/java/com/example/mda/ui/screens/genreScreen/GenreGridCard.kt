package com.example.mda.ui.screens.genreScreen

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.mda.R
import com.example.mda.data.remote.model.Genre

@Composable
fun GenreGridCard(genre: Genre, @DrawableRes imageUrl: Int, onClick: () -> Unit) {
    val  barColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f)
    val barOverlayColor = barColor.copy(alpha = 0.45f)
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = barOverlayColor),
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
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.fillMaxSize()
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.15f)),
                                startY = 0f,
                                endY = 1000f
                            )
                        )
                )
            }

            Text(
                text = genre.name,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 12.dp)
            )
        }
    }
}

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