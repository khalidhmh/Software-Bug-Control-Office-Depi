package com.example.mda.ui.screens.actors

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.mda.R
import com.example.mda.data.remote.model.Actor

@Composable
fun ActorListItem(
    actor: Actor,
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val imageUrl = actor.profilePath?.let { "https://image.tmdb.org/t/p/w500$it" }
    Log.d("ActorListItem", "Loading image for ${actor.name}: $imageUrl")

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { navController.navigate("ActorDetails/${actor.id}") }
            .padding(vertical = 8.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Box(
            modifier = Modifier
                .width(80.dp)
                .aspectRatio(0.7f)
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            AsyncImage(
                model = imageUrl,
                contentDescription = actor.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.matchParentSize(),
                placeholder = painterResource(id = R.drawable.person_placeholder),
                error = painterResource(id = R.drawable.person_placeholder)
            )

            if (imageUrl.isNullOrBlank()) {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(MaterialTheme.colorScheme.inverseOnSurface),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.person_placeholder),
                        contentDescription = "Placeholder",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(48.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = actor.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))

            val knownForTitles = actor.knownFor
                ?.mapNotNull { it.title ?: it.name }
                ?.take(2)
                ?.joinToString(", ")
                ?: ""
            if (knownForTitles.isNotEmpty()) {
                Log.d("knownForTitles.isNotEmpty()", "Loading image for ${actor.name}: $imageUrl")
                Text(
                    text = knownForTitles,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
