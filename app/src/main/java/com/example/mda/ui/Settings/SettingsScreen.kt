package com.example.mda.ui.screens.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.mda.data.SettingsDataStore
import com.example.mda.ui.navigation.TopBarState
import com.example.mda.ui.screens.auth.AuthUiState
import com.example.mda.ui.screens.auth.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController,
    onTopBarStateChange: (TopBarState) -> Unit,
    authViewModel: AuthViewModel?
) {
    val context = LocalContext.current
    val dataStore = remember { SettingsDataStore(context) }
    val viewModel: SettingsViewModel = viewModel(factory = SettingsViewModelFactory(dataStore))

    val theme by viewModel.themeMode.collectAsState()
    val notifications by viewModel.notificationsEnabled.collectAsState()
    val uiState by authViewModel?.uiState?.collectAsState() ?: remember { mutableStateOf(AuthUiState()) }

    val isLoggedIn = uiState.isAuthenticated
    val account = uiState.accountDetails
    LaunchedEffect(Unit) {
        onTopBarStateChange(
            TopBarState(
                title = "Settings",
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        ProfileCard(
            isLoggedIn = isLoggedIn,
            userName = account?.name ?: account?.username,
            userEmail = account?.id?.toString(),
            onClick = { navController.navigate("profile") },
            onLoginClick = { navController.navigate("login") }
        )
        Text("Other settings", style = MaterialTheme.typography.labelLarge)

        SettingsGroupCard {
            SettingsItem(Icons.Default.Person, "Profile details") { navController.navigate("profile_details")}
            Divider()
            SettingsItem(Icons.Default.Lock, "Password") { navController.navigate("change_password")}
            Divider()
            SettingsItem(
                Icons.Default.Notifications, "Notifications",
                isToggle = true,
                toggleState = notifications,
                onToggleChange = { viewModel.updateNotifications(it) }
            )
            Divider()
            SettingsItem(
                Icons.Default.DarkMode, "Dark Mode",
                isToggle = true,
                toggleState = theme == 2,
                onToggleChange = { viewModel.updateTheme(if (it) 2 else 1) }
            )
        }

        SettingsGroupCard {
            SettingsItem(Icons.Default.Language, "Language") { navController.navigate("language_settings")}
            Divider()
            SettingsItem(Icons.Default.Security, "Privacy Settings") { navController.navigate("privacy_settings")}
            Divider()
            SettingsItem(Icons.Default.Help, "Help / FAQ") { navController.navigate("help_faq")}
            Divider()
            SettingsItem(Icons.Default.Info, "About") {   navController.navigate("about_app")}
        }

        SettingsGroupCard {
            Divider()
            SettingsItem(
                Icons.Default.Logout,
                "Log out",
                textColor = MaterialTheme.colorScheme.error,
                iconColor = MaterialTheme.colorScheme.error
            ) {
                 }
        }

        Spacer(Modifier.height(80.dp))

    }
}

@Composable
fun SettingsGroupCard(content: @Composable ColumnScope.() -> Unit) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(content = content)
    }
}

@Composable
fun SettingsItem(
    icon: ImageVector,
    title: String,
    textColor: Color = MaterialTheme.colorScheme.onSurface,
    iconColor: Color = MaterialTheme.colorScheme.onSurface,
    isToggle: Boolean = false,
    toggleState: Boolean = false,
    onToggleChange: ((Boolean) -> Unit)? = null,
    onClick: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = onClick != null && !isToggle) { onClick?.invoke() }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = title, tint = iconColor)
        Spacer(Modifier.width(16.dp))
        Text(title, color = textColor, modifier = Modifier.weight(1f))
        if (isToggle && onToggleChange != null) {
            Switch(checked = toggleState, onCheckedChange = onToggleChange)
        } else {
            Icon(
                Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = Color.Gray
            )
        }
    }
}
@Composable
fun ProfileCard(
    isLoggedIn: Boolean,
    userName: String?,
    userEmail: String?,
    onClick: () -> Unit,
    onLoginClick: () -> Unit
) {

    Card(
        onClick = {
            if (isLoggedIn) onClick() else onLoginClick()
        },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(60.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.padding(12.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))
            if (isLoggedIn) {

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = userName ?: "User",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = userEmail ?: "",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                }

                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = Color.Gray
                )

            } else {
                Column {
                    Text(
                        text = "Login or Sign up",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Access your account to sync settings",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}