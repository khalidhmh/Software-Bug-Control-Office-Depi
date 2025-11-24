package com.example.mda.ui.screens.actors

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import com.example.mda.data.remote.model.getKnownForTitles

@Composable
fun ActorListItem(
    actor: Actor,
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 3.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            .clickable {
                navController.navigate("actorDetails/${actor.id}")
            }
            .padding(1.dp)
    ) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp, horizontal = 16.dp), // ✅ شلنا الـ clickable المكرر هنا
            verticalAlignment = Alignment.CenterVertically
        ) {

            // صورة الممثل
            Box(
                modifier = Modifier
                    .width(80.dp)
                    .aspectRatio(0.7f)
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                AsyncImage(
                    model = "https://image.tmdb.org/t/p/w500${actor.profilePath}",
                    contentDescription = actor.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.matchParentSize(),
                    error = painterResource(id = R.drawable.person_placeholder)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // النصوص
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = actor.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(2.dp)) // ✅ قللنا المسافة من 4dp → 2dp


                val department = actor.knownForDepartment ?: "Acting"
                val infoLine = department

                Text(
                    text = infoLine,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(2.dp)) // ✅ نزّلنا ده كمان بدل 4dp

                val knownForTitles = actor.getKnownForTitles()

                if (knownForTitles.isNotEmpty()) {
                    Text(
                        text = knownForTitles,
                        style = MaterialTheme.typography.bodySmall, // ✅ bodySmall بدل bodyMedium
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}
