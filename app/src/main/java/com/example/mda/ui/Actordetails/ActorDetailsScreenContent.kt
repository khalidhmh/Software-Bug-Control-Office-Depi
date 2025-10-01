package com.example.mda.ui.Actordetails


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyRow
import com.example.mda.R
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.mda.data.remote.model.ActorFullDetails

@Composable
fun ActorDetailsScreenContent(
    actor: ActorFullDetails
) {

    val uriHandler = LocalUriHandler.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Profile Image
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {
            Image(
                painter = rememberAsyncImagePainter("https://image.tmdb.org/t/p/w500${actor.profile_path}"),
                contentDescription = actor.name,
                modifier = Modifier
                    .width(140.dp)
                    .height(200.dp)
                    .padding(end = 12.dp),
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = actor.name,
                    style = MaterialTheme.typography.headlineSmall.copy(color = Color.White),
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Birthday: ${actor.birthday ?: "-"}",
                    color = Color.Gray
                )
                Text(
                    text = "Place: ${actor.place_of_birth ?: "-"}",
                    color = Color.Gray
                )
                Text(
                    text = "Gender: ${if (actor.gender == 1) "Female" else "Male"}",
                    color = Color.Gray
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))


        ExpandableText(
            text = actor.biography ?: "No biography available",
            minimizedMaxLines = 3
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Social Media Links
        if (actor.external_ids != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Social Media",
                style = MaterialTheme.typography.titleMedium.copy(color = Color.White),
                fontWeight = FontWeight.Bold
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                actor.external_ids?.imdb_id?.let { imdbId ->
                    SocialLink("IMDb") {
                        uriHandler.openUri("https://www.imdb.com/name/$imdbId/")
                    }
                }
                actor.external_ids?.facebook_id?.let { fbId ->
                    SocialLink("Facebook") {
                        uriHandler.openUri("https://www.facebook.com/$fbId")
                    }
                }
                actor.external_ids?.instagram_id?.let { igId ->
                    SocialLink("Instagram") {
                        uriHandler.openUri("https://www.instagram.com/$igId")
                    }
                }
                actor.external_ids?.twitter_id?.let { twId ->
                    SocialLink("Twitter") {
                        uriHandler.openUri("https://twitter.com/$twId")
                    }
                }
            }
        }



        Spacer(modifier = Modifier.height(16.dp))

        // Images Section
        if (!actor.images?.profiles.isNullOrEmpty()) {
            Text(
                text = "Photos",
                style = MaterialTheme.typography.titleMedium.copy(color = Color.White),
                fontWeight = FontWeight.Bold
            )
            LazyRow(
                modifier = Modifier.padding(top = 8.dp)
            ) {
                items(actor.images!!.profiles.size) { index ->
                    val img = actor.images!!.profiles[index]
                    Card(
                        modifier = Modifier
                            .size(120.dp)
                            .padding(end = 8.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.DarkGray)
                    ) {
                        Image(
                            painter = rememberAsyncImagePainter("https://image.tmdb.org/t/p/w500${img.file_path}"),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Movies / Credits Section
        if (!actor.combined_credits?.cast.isNullOrEmpty()) {
            Text(
                text = "Known For",
                style = MaterialTheme.typography.titleMedium.copy(color = Color.White),
                fontWeight = FontWeight.Bold
            )
            LazyRow(
                modifier = Modifier.padding(top = 8.dp)
            ) {
                items(actor.combined_credits!!.cast.size) { index ->
                    val movie = actor.combined_credits!!.cast[index]
                    if (movie.poster_path != null) {
                        Card(
                            modifier = Modifier
                                .width(120.dp)
                                .height(180.dp)
                                .padding(end = 8.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.DarkGray)
                        ) {
                            Image(
                                painter = rememberAsyncImagePainter("https://image.tmdb.org/t/p/w500${movie.poster_path}"),
                                contentDescription = movie.title ?: movie.name,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                }
            }
        }
    }
}
@Composable
fun ExpandableText(
    text: String,
    minimizedMaxLines: Int = 3 // how many lines before "expand"
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1E1E1E), // dark background for the box
        ),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            // Title aligned to left
            Text(
                text = "Biography",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFFFC107), // amber/yellow accent
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Body text
            Text(
                text = text,
                maxLines = if (expanded) Int.MAX_VALUE else minimizedMaxLines,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = Color(0xFFEEEEEE) // light gray for readability
                )
            )

            // Expand/collapse icon
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .clickable { expanded = !expanded }
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            ) {
                Icon(
                    imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = Color(0xFFFFC107), // match the title accent
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }
}

@Composable
fun SocialLink(
    platform: String,
    onClick: () -> Unit
) {
    val icon = when (platform) {
        "IMDb" -> R.drawable.network
        "Facebook" -> R.drawable.facebook
        "Instagram" -> R.drawable.facebook
        "Twitter" -> R.drawable.twitter
        else -> R.drawable.network // fallback
    }

    Icon(
        painter = painterResource(id = icon),
        contentDescription = platform,
        tint = Color.Unspecified, // keep original logo colors
        modifier = Modifier
            .size(36.dp)
            .clickable { onClick() }
    )
}
