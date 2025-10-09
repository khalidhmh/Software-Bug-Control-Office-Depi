package com.example.mda.ui.actors

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mda.data.remote.model.Actor

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ActorsScreen(
    viewModel: ActorViewModel = viewModel(factory = ActorViewModelFactory()),
) {
    val uiState by viewModel.state.collectAsState()

    when (val state = uiState) {
        is ActorUiState.Loading -> {
            ActorsGrid(actors = emptyList(), isLoading = true, viewModel = viewModel)
        }

        is ActorUiState.Success -> {
            ActorsGrid(actors = state.actors, isLoading = false, viewModel = viewModel)
        }

        is ActorUiState.Error -> {
            ErrorScreen(
                errorType = state.type,
                onRetry = { viewModel.loadMoreActors() }
            )
        }
    }
}
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ActorsGrid(actors: List<Actor>, isLoading: Boolean, viewModel: ActorViewModel) {
    val listState = rememberLazyGridState()

    LaunchedEffect(listState.firstVisibleItemIndex, listState.layoutInfo.totalItemsCount) {
        val lastVisible = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
        val totalItems = listState.layoutInfo.totalItemsCount
        if (lastVisible >= totalItems - 6) {
            viewModel.loadMoreActors()
        }
    }

        LazyVerticalGrid(
            state = listState,
            columns = GridCells.Fixed(3),
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(actors, key = { it.id }) { actor ->
                ActorCard(actor = actor)
            }
        }

}

@Composable
fun ErrorScreen(
    errorType: ErrorType,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = MaterialTheme.colorScheme
    val title: String
    val message: String
    val icon: ImageVector
    when (errorType) {
        is ErrorType.NetworkError -> {
            title = "Connection problem"
            message = "Please, check your internet connection and try again.\n\n"



        }
        is ErrorType.ApiError -> {
            title = "Server error"
            message = "We’re having trouble fetching data.\n(Error code: ${errorType.code})"

        }
        is ErrorType.UnknownError -> {
            title = "Unexpected error"
            message = errorType.message ?: "Please try again later."
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(colors.background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth()
        ) {

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = title,
                color = colors.onBackground,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold
                )
            )

            Spacer(modifier = Modifier.height(12.dp))


            Text(
                text = message,
                color = colors.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 20.sp),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(28.dp))

            Button(
                onClick = onRetry,
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colors.primaryContainer,
                    contentColor = colors.onPrimaryContainer
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Retry"
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Retry")
            }
        }
    }
}