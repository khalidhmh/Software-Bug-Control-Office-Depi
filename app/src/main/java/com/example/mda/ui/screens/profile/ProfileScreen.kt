package com.example.mda.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.mda.ui.navigation.TopBarState
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
    val favorites by favoritesViewModel.favorites.collectAsState()
    val authState = authViewModel?.uiState?.collectAsState()?.value

    val gradientBackground = AppBackgroundGradient()

    LaunchedEffect(Unit) {
        onTopBarStateChange(TopBarState(title = "Profile", showBackButton = true))
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradientBackground)
            .verticalScroll(rememberScrollState())
            .padding(bottom = 100.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            // ===== Avatar Section =====
            Box(
                modifier = Modifier
                    .size(110.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)),
                contentAlignment = Alignment.Center
            ) {
                val avatarUrl = authState?.accountDetails?.avatar?.tmdb?.avatarPath
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
                text = authState?.accountDetails?.name?.ifEmpty { authState.accountDetails?.username ?: "Guest User" }
                    ?: "Guest User",
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )

            Text(
                text = "@${authState?.accountDetails?.username ?: "Not Signed In"}",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(28.dp))

            // ===== Cards Section =====
            ProfileSection(
                title = "Account",
                items = listOf(
                    ProfileCardItem(
                        icon = Icons.Default.Person,
                        label = "View TMDb Account"
                    ) {
                        navController.navigate("account")
                    },
                    ProfileCardItem(
                        icon = Icons.Default.Logout,
                        label = "Logout"
                    ) {
                        authViewModel?.logout()
                    }
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            ProfileSection(
                title = "Your Activity",
                items = listOf(
                    ProfileCardItem(icon = Icons.Default.Star, label = "Favorite Movies") {
                        navController.navigate("Favprofile")
                    },
                    ProfileCardItem(icon = Icons.Default.Person, label = "Actors Viewed") {
                        navController.navigate("HistoryScreen")
                    },
                    ProfileCardItem(icon = Icons.Default.Movie, label = "Movies Viewed") {
                        navController.navigate("MovieHistoryScreen")
                    }
                )
            )
        }
    }
}

@Composable
fun ProfileSection(title: String, items: List<ProfileCardItem>) {
    Column(
        modifier = Modifier.padding(horizontal = 20.dp)
    ) {
        Text(
            text = title,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.SemiBold,
            fontSize = 16.sp,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        items.forEach { item ->
            ProfileCardButton(item)
            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}

@Composable
fun ProfileCardButton(item: ProfileCardItem) {
    val backgroundColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
    Surface(
        onClick = item.onClick,
        shape = RoundedCornerShape(14.dp),
        color = backgroundColor,
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
                imageVector = item.icon,
                contentDescription = item.label,
                tint = item.accent ?: MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = item.label,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

data class ProfileCardItem(
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val label: String,
    val accent: Color? = null,
    val onClick: () -> Unit
)