package com.example.mda.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.mda.ui.navigation.TopBarState
import com.example.mda.ui.screens.auth.AuthViewModel
import com.example.mda.ui.screens.favorites.FavoritesViewModel
import com.example.mda.ui.screens.profile.favourites.HistorySectionButton

@Composable
fun ProfileScreen(
    navController: NavController,
    favoritesViewModel: FavoritesViewModel,
    authViewModel: AuthViewModel?,
    onTopBarStateChange: (TopBarState) -> Unit
) {
    val favorites by favoritesViewModel.favorites.collectAsState()
    val snackbarMessage by favoritesViewModel.snackbarMessage.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Get auth state safely
    val authState = authViewModel?.uiState?.collectAsState()?.value

    // Fetch account details when authenticated
    LaunchedEffect(authState?.isAuthenticated) {
        if (authState?.isAuthenticated == true && authViewModel != null) {
            // 1) Fetch TMDB account info (avatar, username, etc.)
            authViewModel.fetchAccountDetails()

            // 2) Sync FAVORITES from TMDB → Local database
            favoritesViewModel.syncFavoritesFromTmdb()
        } else if (authState?.isAuthenticated == false) {
            // 3) Clear local favorites when user LOGS OUT
            favoritesViewModel.clearLocalFavorites()
        }
    }

    LaunchedEffect(Unit) {
        onTopBarStateChange(
            TopBarState(
                title = "Profile",
                navigationIcon = null,
                actions = {
                    IconButton(onClick = { navController.navigate("settings") }) {
                        Icon(
                            Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
            )
        )
    }

    LaunchedEffect(snackbarMessage) {
        snackbarMessage?.let {
            snackbarHostState.showSnackbar(it)
            favoritesViewModel.clearSnackbarMessage()
        }
    }

    Scaffold(
        containerColor = Color.Transparent,
        snackbarHost = {
            SnackbarHost(snackbarHostState) { data ->
                Snackbar(
                    snackbarData = data,
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            // Profile Header
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Profile Avatar - Dynamic based on auth state
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    val avatarUrl = authState?.accountDetails?.avatar?.tmdb?.avatarPath
                    if (avatarUrl != null) {
                        AsyncImage(
                            model = "https://image.tmdb.org/t/p/w200$avatarUrl",
                            contentDescription = "Avatar",
                            modifier = Modifier
                                .size(120.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Profile",
                            modifier = Modifier.size(60.dp),
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // User Name - Dynamic based on auth state
                Text(
                    text = if (authState?.isAuthenticated == true && authState.accountDetails != null) {
                        val account = authState.accountDetails!!
                        account.name.ifEmpty { account.username }
                    } else {
                        "User Profile"
                    },
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Username or subtitle - Dynamic
                Text(
                    text = if (authState?.isAuthenticated == true && authState.accountDetails != null) {
                        "@${authState.accountDetails!!.username}"
                    } else {
                        "Movie enthusiast"
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Authentication buttons
                if (authViewModel != null) {
                    Spacer(modifier = Modifier.height(24.dp))

                    if (authState?.isAuthenticated == true) {
                        // Show "View Account" button if logged in
                        Button(
                            onClick = { navController.navigate("account") },
                            modifier = Modifier.fillMaxWidth(0.7f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("View TMDb Account")
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Logout button
                        OutlinedButton(
                            onClick = {
                                authViewModel.logout()
                            },
                            modifier = Modifier.fillMaxWidth(0.7f),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = MaterialTheme.colorScheme.error
                            ),
                            border = ButtonDefaults.outlinedButtonBorder.copy(
                                brush = androidx.compose.ui.graphics.SolidColor(
                                    MaterialTheme.colorScheme.error.copy(alpha = 0.5f)
                                )
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Logout")
                        }
                    } else {
                        // Show Login and Signup buttons if not logged in
                        Row(
                            modifier = Modifier.fillMaxWidth(0.9f),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Button(
                                onClick = { navController.navigate("login") },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary,
                                    contentColor = MaterialTheme.colorScheme.onPrimary
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("Login")
                            }
                            OutlinedButton(
                                onClick = { navController.navigate("signup") },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = MaterialTheme.colorScheme.primary
                                ),
                                border = ButtonDefaults.outlinedButtonBorder.copy(
                                    brush = androidx.compose.ui.graphics.SolidColor(
                                        MaterialTheme.colorScheme.primary
                                    )
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("Sign Up")
                            }
                        }
                    }
                }
            }

            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 24.dp),
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                thickness = 1.dp
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Section Buttons
            Column(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                HistorySectionButton(navController, "Favprofile", "Favorite Movies")
                HistorySectionButton(navController, "HistoryScreen", "Actors Viewed")
                HistorySectionButton(navController, "MovieHistoryScreen", "Movies Viewed")
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}