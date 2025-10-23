package com.example.mda.ui.screens.actordetails

import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.mda.data.remote.model.ActorFullDetails
import com.example.mda.ui.screens.actordetails.widgets.*

@Composable
fun ActorDetailsScreenContent(
    actor: ActorFullDetails,
    movieCount: Int,
    tvShowCount: Int,
    age: Int?,
    navController: NavController
) {
    Log.i("error", "ActorDetailsScreenContent: ")
    var showAll by remember { mutableStateOf(false) }
    // i want to print the actor object to know the imaage don`t appear  in the bckground
   // Log.d("error", "Actor data: $actor")
    Log.d("error", "images: ${actor.images}")


    val bgGradient = Brush.verticalGradient(
        colors = listOf(
            MaterialTheme.colorScheme.background,
            MaterialTheme.colorScheme.surface
        )
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(bgGradient),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Spacer(Modifier.height(20.dp)); ActorProfile(
            actor.profile_path,
            actor.name,
            actor.gender,
            navController
        )
        }
        if (!actor.biography.isNullOrBlank()) {
            item {
                Spacer(Modifier.height(16.dp))
                BiographyCard(actor.biography)
            }
        }
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                SummaryCard(tvShowCount.toString(), movieCount.toString(), Modifier.weight(1f))
                age?.let {
                    BirthCard(
                        dateOfBirth = actor.birthday ?: "Unknown",
                        age = it.toString(),
                        placeOfBirth = actor.place_of_birth ?: "Unknown",
                        modifier = Modifier.weight(1f)
                    )
                }

            }
        }
        actor.images?.profiles?.takeIf { it.isNotEmpty() }?.let { profiles ->
            item {
                ActorPhotosSection(profiles)
            }
        }
        item {
            Spacer(Modifier.height(10.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Filmography",
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.SemiBold
                )
                TextButton(onClick = { showAll = !showAll }) {
                    Text(
                        if (showAll) "View Less" else "View More",
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        actor.combined_credits?.cast?.let { castList ->
            val displayed = if (showAll) castList else castList.take(4)
            items(displayed.distinctBy { it.id }, key = { it.id }) { movie ->
                MovieCard(
                    navController,
                    movie.title ?: movie.name ?: "Unknown",
                    movie.poster_path,
                    movie.character ?: "Actor",
                    movie
                )
            }
        }

        item {
            Spacer(Modifier.height(24.dp))
            SocialLinks(actor)
            Spacer(Modifier.height(100.dp))
        }
    }
}

@Composable
private fun SocialLinks(actor: ActorFullDetails) {
    val context = LocalContext.current
    val items = listOfNotNull(
        actor.external_ids?.instagram_id?.let { "instagram" to "https://www.instagram.com/$it" },
        actor.external_ids?.twitter_id?.let { "twitter" to "https://twitter.com/$it" },
        actor.external_ids?.imdb_id?.let { "imdb" to "https://www.imdb.com/name/$it/" },
        actor.external_ids?.imdb_id?.let { "facebook" to "https://www.imdb.com/name/$it/" }

    )
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
        items.forEach { (icon, link) ->
            val res = when (icon) {
                "instagram" -> com.example.mda.R.drawable.instagram
                "twitter" -> com.example.mda.R.drawable.twitter
                "facebook" -> com.example.mda.R.drawable.facebook
                else -> com.example.mda.R.drawable.network
            }
            SocialIcon(res) { context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(link))) }
        }
    }
}
