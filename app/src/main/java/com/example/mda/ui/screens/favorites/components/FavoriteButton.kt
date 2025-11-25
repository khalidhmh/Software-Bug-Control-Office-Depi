package com.example.mda.ui.screens.favorites.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.example.mda.data.remote.model.Movie
import com.example.mda.ui.screens.favorites.FavoritesViewModel

@Composable
fun FavoriteButton(
    movie: Movie,
    viewModel: FavoritesViewModel,
    modifier: Modifier = Modifier,
    showBackground: Boolean = true,
    onLoginRequired: () -> Unit,           // screen decides what "login" does (navigate)
    isAuthenticated: Boolean = true,
    navController: NavController? = null,  // still here if you use it somewhere else
) {
    // dialog state is now INSIDE the button
    var showLoginDialog by remember { mutableStateOf(false) }

    // reactive favorites state from ViewModel
    val favorites by viewModel.favorites.collectAsState()
    val isFavorite = favorites.any { it.id == movie.id && it.isFavorite }

    Box(
        modifier = modifier
            .size(40.dp)
            .then(
                if (showBackground) {
                    Modifier.background(
                        color = Color.Black.copy(alpha = 0.6f),
                        shape = CircleShape
                    )
                } else Modifier
            )
            .clickable {
                if (!isAuthenticated) {
                    // show dialog instead of doing navigation directly
                    showLoginDialog = true
                } else {
                    viewModel.toggleFavorite(movie)
                }
            },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
            contentDescription = if (isFavorite) "Remove from favorites" else "Add to favorites",
            tint = if (isFavorite) Color.Red else Color.Gray,
            modifier = Modifier.size(24.dp)
        )
    }

    // login dialog shown by the button itself
    if (showLoginDialog) {
        LoginRequiredDialog(
            onDismiss = { showLoginDialog = false },
            onLogin = {
                showLoginDialog = false
                onLoginRequired()      // screen will usually call navController.navigate("profile")
            }
        )
    }
}

@Composable
private fun LoginRequiredDialog(
    onDismiss: () -> Unit,
    onLogin: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = MaterialTheme.shapes.extraLarge,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Login Required",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "You must login to add this movie to favorites.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text(
                            text = "Cancel",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = onLogin) {
                        Text(
                            text = "Login",
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }
            }
        }
    }
}
