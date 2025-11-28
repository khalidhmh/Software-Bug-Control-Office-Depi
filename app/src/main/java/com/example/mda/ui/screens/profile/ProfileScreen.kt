package com.example.mda.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.mda.ui.navigation.TopBarState
import com.example.mda.ui.screens.auth.AccountInfoRow
import com.example.mda.ui.screens.auth.AuthViewModel
import com.example.mda.ui.screens.favorites.FavoritesViewModel
import com.example.mda.ui.theme.AppBackgroundGradient

@Composable
fun ProfileScreen(
    navController: NavController,
    favoritesViewModel: FavoritesViewModel,
    authViewModel: AuthViewModel?,
    onTopBarStateChange: (TopBarState) -> Unit
) {
    val uiState = authViewModel?.uiState?.collectAsState()?.value
    val account = uiState?.accountDetails


    LaunchedEffect(Unit) {
        onTopBarStateChange(TopBarState(title = "Profile", showBackButton = true))
        authViewModel?.fetchAccountDetails()
    }


    LaunchedEffect(uiState?.isAuthenticated) {
        if(authViewModel?.uiState?.value?.isAuthenticated == true){
        favoritesViewModel.syncFavoritesFromTmdb();
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 80.dp)
    ) {
        when {
            uiState?.isLoading == true -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = MaterialTheme.colorScheme.primary
                )
            }

            uiState?.error != null -> {
                Text(
                    text = "Error: ${uiState.error}",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            account != null -> {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(40.dp))

                    // ===== Avatar =====
                    Box(
                        modifier = Modifier
                            .size(110.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)),
                        contentAlignment = Alignment.Center
                    ) {
                        val avatarUrl = account.avatar?.tmdb?.avatarPath
                        if (avatarUrl != null) {
                            AsyncImage(
                                model = "https://image.tmdb.org/t/p/w200$avatarUrl",
                                contentDescription = "Avatar",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(110.dp)
                                    .clip(CircleShape)
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Person,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(56.dp),
                                contentDescription = null
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = account.name.ifEmpty { account.username },
                        color = MaterialTheme.colorScheme.onBackground,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )

                    Text(
                        text = "@${account.username}",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 14.sp
                    )

                    Spacer(modifier = Modifier.height(28.dp))

                    // ===== Account Information Card =====
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
                        tonalElevation = 2.dp,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Text(
                                text = "Account Information",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            AccountInfoRow("Account ID", account.id.toString())
                            AccountInfoRow("Language", account.iso6391.uppercase())
                            AccountInfoRow("Country", account.iso31661.uppercase())
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // ===== Logout card (زي الشكل القديم) =====
                    Surface(
                        onClick = { authViewModel?.logout()
                                  favoritesViewModel.clearLocalFavorites()
                                  navController.navigate("Settings")},
                        shape = RoundedCornerShape(14.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                        tonalElevation = 2.dp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Start,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 20.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Logout,
                                contentDescription = "Logout",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(
                                text = "Logout",
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }
    }
}