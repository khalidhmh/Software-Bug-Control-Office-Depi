package com.example.mda.ui.Actordetails

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.mda.ui.actor.ActorViewModel

@Composable
fun ActorDetailsScreen(
    personId: Int,
    viewModel: ActorViewModel
) {
    val actor by viewModel.actorFullDetails.collectAsState()
    val loading by viewModel.loading.collectAsState()
    val error by viewModel.error.collectAsState()

    // Trigger loading when screen opens
    LaunchedEffect(personId) {
        viewModel.loadActorFullDetails(personId)
    }

    when {
        loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                androidx.compose.material3.CircularProgressIndicator()
            }
        }
        error != null -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Error: $error",
                    color = Color.Red
                )
            }
        }
        actor != null -> {
            ActorDetailsScreenContent(actor = actor!!)
        }
    }
}
