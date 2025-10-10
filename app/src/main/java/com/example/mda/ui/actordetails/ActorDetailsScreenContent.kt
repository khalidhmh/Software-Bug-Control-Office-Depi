package com.example.mda.ui.actordetails

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.mda.R
import com.example.mda.data.remote.model.ActorFullDetails
import com.example.mda.ui.actordetails.widgets.ActorProfile
import com.example.mda.ui.actordetails.widgets.BiographyCard
import com.example.mda.ui.actordetails.widgets.BirthCard
import com.example.mda.ui.actordetails.widgets.MovieCard
import com.example.mda.ui.actordetails.widgets.SocialIcon
import com.example.mda.ui.actordetails.widgets.SummaryCard

@Composable
fun ActorDetailsScreenContent(
    actor: ActorFullDetails,
    movieCount: Int,
    tvShowCount: Int,
    age: Int? ,
    navController: NavController
) {
    val uriHandler = LocalUriHandler.current
    var showAll by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF0D0D20), Color(0xFF1A1A40))
                )
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // ✅ Profile Section
        item {
            Spacer(modifier = Modifier.height(24.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(360.dp)
            ) {
                ActorProfile(
                    profilePath = actor.profile_path,
                    name = actor.name,
                    gender = actor.gender ,
                    navController = navController
                )
            }
        }

        // ✅ Biography
        item {
            Spacer(modifier = Modifier.height(24.dp))
            BiographyCard(
                text = actor.biography ?: "No biography available",
                minimizedMaxLines = 3
            )
        }
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp) // ✅ ensures both cards have the same height
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                SummaryCard(
                    tvShows = tvShowCount.toString(),
                    movies = movieCount.toString(),
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight() // ✅ equal height
                )

                if (age != null) {
                    BirthCard(
                        dateOfBirth = actor.birthday ?: "Unknown",
                        age = age.toString(),
                        placeOfBirth = actor.place_of_birth ?: "Unknown",
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight() // ✅ equal height
                    )
                }
            }
        }


        item {
            Spacer(
                modifier = Modifier
                    .height(12.dp)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier.padding(start = 8.dp),
                    text = "FilmoGraphy",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium
                )
                TextButton(
                    onClick = { showAll = !showAll },

                    ) {
                    Text(
                        text = if (showAll) "View Less" else "View More",
                        color = Color(0xFFAAAAFF),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        actor.combined_credits?.cast?.takeIf { it.isNotEmpty() }?.let { castList ->
            // State to toggle between showing 4 and all items


            val displayedList = if (showAll) castList else castList.take(4)

            items(displayedList.size) { index ->
                val movie = displayedList[index]
                MovieCard(navController = navController,
                    title = movie.title ?: movie.name ?: "Unknown",
                    posterUrl = movie.poster_path,
                    role = movie.character ?: "Actor",
                    movie =movie
                )
            }
        }
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start
            ) {
                Text(
                    text = "Connect",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold, textAlign = TextAlign.Start,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                actor.external_ids.instagram_id?.let {
                    SocialIcon(R.drawable.instagram) {
                        uriHandler.openUri("https://www.instagram.com/$it")
                    }
                }
                actor.external_ids.twitter_id?.let {
                    SocialIcon(R.drawable.twitter) {
                        uriHandler.openUri("https://twitter.com/$it")
                    }
                }
                actor.external_ids.imdb_id?.let {
                    SocialIcon(R.drawable.network) {
                        uriHandler.openUri("https://www.imdb.com/name/$it/")
                    }
                }
            }
            Spacer(modifier = Modifier.height(120.dp))
        }
    }
}

