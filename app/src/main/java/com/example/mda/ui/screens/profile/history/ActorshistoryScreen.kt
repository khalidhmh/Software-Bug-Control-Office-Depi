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
import androidx.compose.material3.*
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
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.example.mda.R
import com.example.mda.ui.navigation.TopBarState
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HistoryScreen(
    navController: NavController,
    viewModel: HistoryViewModel,
    onTopBarStateChange: (TopBarState) -> Unit
) {
    val history by viewModel.history.observeAsState(emptyList())

    LaunchedEffect(Unit) {
        onTopBarStateChange(
            TopBarState(
                title = "History",
                showBackButton = true,
                actions = {
                    IconButton(onClick = { viewModel.clearHistory() }) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = null
                        )
                    }
                }
            )
        )
    }

    if (history.isEmpty()) {
        emptyScreen("No viewed people yet",
            "Start exploring and your viewed history will appear here.",
            IconType.PERSON
        )
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp, vertical = 12.dp)
        ) {
            items(history) { person ->

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        .clickable {
                            navController.navigate("ActorDetails/${person.id}")
                        }
                        .padding(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // صورة الممثل أو الفيلم
                        Box(
                            modifier = Modifier
                                .width(100.dp)
                                .aspectRatio(0.7f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(MaterialTheme.colorScheme.onSurfaceVariant)
                        ) {
                            AsyncImage(
                                model = "https://image.tmdb.org/t/p/w200${person.profilePath}",
                                contentDescription = person.name,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.matchParentSize(),
                                error = rememberAsyncImagePainter(R.drawable.person_placeholder)
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        // النصوص
                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = person.name ?: "Actor Name",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onBackground,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = "Viewed on: " + SimpleDateFormat(
                                    "dd MMM yyyy",
                                    Locale.getDefault()
                                ).format(Date(person.viewedAt)),
                                style = MaterialTheme.typography.bodyMedium,
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