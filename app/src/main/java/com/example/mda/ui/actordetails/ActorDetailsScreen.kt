package com.example.mda.ui.actordetails

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mda.ui.actor.ActorViewModel
import com.example.mda.ui.actor.ActorViewModelFactory


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ActorDetailsScreen(
    personId: Int
) {
    val viewModel: ActorViewModel = viewModel(factory = ActorViewModelFactory())
    val actor by viewModel.actorFullDetails.collectAsState()
    val loading by viewModel.loading.collectAsState()
    val error by viewModel.error.collectAsState()
    val movieCount by viewModel.movieCount.collectAsState()
    val tvShowCount by viewModel.tvShowCount.collectAsState()

    val age = actor?.birthday?.let { calculateAge(it) }

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
                CircularProgressIndicator()
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
            ActorDetailsScreenContent(
                actor = actor!!,
                movieCount = movieCount,
                tvShowCount = tvShowCount ,
                age = age
            )
        }
    }
}
