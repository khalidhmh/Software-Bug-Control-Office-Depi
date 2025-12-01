package com.example.mda.ui.screens.settings

import android.os.Build
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
// 🟢 Imports from Fares (Localization)
import com.example.mda.localization.LocalizationKeys
import com.example.mda.localization.localizedString
// 🟢 Imports from Main (Notification & Workers)
import com.example.mda.notifications.NotificationHelper
import com.example.mda.ui.navigation.TopBarState
import com.example.mda.ui.screens.auth.AuthUiState
import com.example.mda.ui.screens.auth.AuthViewModel
import com.example.mda.ui.screens.favorites.FavoritesViewModel
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.mda.work.InactiveUserWorker
import com.example.mda.work.SuggestedMovieWorker
import com.example.mda.work.TrendingReminderWorker
import androidx.core.content.edit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController,
    onTopBarStateChange: (TopBarState) -> Unit,
    authViewModel: AuthViewModel?,
    FavoritesViewModel: FavoritesViewModel
) {
    val context = LocalContext.current
    val dataStore = remember { SettingsDataStore(context) }
    val viewModel: SettingsViewModel = viewModel(factory = SettingsViewModelFactory(dataStore))
    val theme by viewModel.themeMode.collectAsState()
    val notifications by viewModel.notificationsEnabled.collectAsState()

    // 🟢 قراءات مباشرة من SessionManager
    val sessionManager = remember { com.example.mda.data.datastore.SessionManager(context) }
    val uiState by authViewModel?.uiState?.collectAsState()
        ?: remember { mutableStateOf(AuthUiState()) }

    // flows من الـ DataStore
    val localName by sessionManager.accountName.collectAsState(initial = "")
    val localUsername by sessionManager.accountUsername.collectAsState(initial = "")
    val isLoggedIn = uiState.isAuthenticated
    val account = uiState.accountDetails

    // ✅ استخدام الترجمة للعنوان
    val settingsTitle = localizedString(LocalizationKeys.SETTINGS_TITLE)

    // ✅ تحديث العنوان عند تغيير اللغة (Recomposition)
    LaunchedEffect(settingsTitle) {
        onTopBarStateChange(
            TopBarState(
                title = settingsTitle,
                showBackButton = false
            )
        )

        if (authViewModel != null && uiState.isAuthenticated && uiState.accountDetails == null) {
            authViewModel.fetchAccountDetails()
            FavoritesViewModel.syncFavoritesFromTmdb()
        }
    }

    FavoritesViewModel.syncFavoritesFromTmdb()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        ProfileCard(
            isLoggedIn = isLoggedIn,
            userName = account?.name?.ifEmpty { account.username }
                ?: localName?.ifEmpty { localUsername },
            userEmail = "@${account?.username ?: localUsername}",
            onClick = { navController.navigate("profile") },
            onLoginClick = { navController.navigate("login") }
        )

        Text(
            localizedString(LocalizationKeys.SETTINGS_OTHER),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurface
        )

        // ================= Group 1: Favorites & History =================
        SettingsGroupCard {
            SettingsItem(
                Icons.Default.Favorite,
                localizedString(LocalizationKeys.SETTINGS_FAVORITES),
                onClick = { navController.navigate("Favprofile") }
            )
            Divider()
            SettingsItem(
                Icons.Default.Person,
                localizedString(LocalizationKeys.SETTINGS_ACTORS_VIEWED),
                onClick = { navController.navigate("HistoryScreen") }
            )
            Divider()
            SettingsItem(
                Icons.Default.Movie,
                localizedString(LocalizationKeys.SETTINGS_MOVIES_VIEWED),
                onClick = { navController.navigate("MovieHistoryScreen") }
            )
        }

        // ================= Group 2: Account & Display =================
        SettingsGroupCard {
            SettingsItem(Icons.Default.Lock, localizedString(LocalizationKeys.SETTINGS_PASSWORD)) {
                // not implemented yet
                // navController.navigate("change_password")
            }
            Divider()
            SettingsItem(
                Icons.Default.Notifications, localizedString(LocalizationKeys.SETTINGS_NOTIFICATIONS),
                isToggle = true,
                toggleState = notifications,
                onToggleChange = { viewModel.updateNotifications(it) }
            )
            Divider()
            SettingsItem(
                Icons.Default.DarkMode, localizedString(LocalizationKeys.SETTINGS_DARK_MODE),
                isToggle = true,
                toggleState = theme == 2,
                onToggleChange = { viewModel.updateTheme(if (it) 2 else 1) }
            )
        }

        // ================= Group 3: App Info & Dev Tools =================
        SettingsGroupCard {
            SettingsItem(Icons.Default.Language, localizedString(LocalizationKeys.SETTINGS_LANGUAGE)) { navController.navigate("language_settings") }
            Divider()
            SettingsItem(
                icon = Icons.Default.ChildCare,
                title = localizedString(LocalizationKeys.SETTINGS_KIDS_MODE)
            ) { navController.navigate("kids") }
            Divider()
            SettingsItem(Icons.Default.Security, localizedString(LocalizationKeys.SETTINGS_PRIVACY_POLICY)) { navController.navigate("privacy_policy") }
            Divider()
            SettingsItem(Icons.Default.Help, localizedString(LocalizationKeys.SETTINGS_HELP_FAQ)) { navController.navigate("help_faq") }
            Divider()
            SettingsItem(Icons.Default.Info, localizedString(LocalizationKeys.SETTINGS_ABOUT)) { navController.navigate("about_app") }

            // 👇👇👇 Developer / Testing Section (Merged from Main) 👇👇👇
            Divider()

            // 🔔 Notification Test
            Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                Button(
                    onClick = {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            NotificationHelper.sendNotification(
                                context,
                                "تست الإشعارات 🔔",
                                "ده إشعار تجريبي عشان نتأكد إن الدنيا شغالة!"
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Test Notification Now")
                }
            }

            Divider()

            // 🛠️ Trending Worker Test
            Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                Button(
                    onClick = {
                        val request = OneTimeWorkRequestBuilder<TrendingReminderWorker>().build()
                        WorkManager.getInstance(context).enqueue(request)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Force Start: Trending Worker")
                }
            }

            // 🛠️ Suggested Movie Worker Test
            Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                Button(
                    onClick = {
                        val request = OneTimeWorkRequestBuilder<SuggestedMovieWorker>().build()
                        WorkManager.getInstance(context).enqueue(request)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Force Start: Suggested Movie")
                }
            }

            // 🛠️ Inactive User Worker Test
            Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                Button(
                    onClick = {
                        // Hack logic
                        val prefs = context.getSharedPreferences("app_prefs", android.content.Context.MODE_PRIVATE)
                        val threeDaysAgo = System.currentTimeMillis() - (72L * 60 * 60 * 1000)
                        prefs.edit { putLong("last_open", threeDaysAgo) }

                        val request = OneTimeWorkRequestBuilder<InactiveUserWorker>().build()
                        WorkManager.getInstance(context).enqueue(request)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE91E63))
                ) {
                    Text("Test Inactive User (Hack Time)")
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
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
                        text = localizedString(LocalizationKeys.SETTINGS_LOGIN_PROMPT),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = localizedString(LocalizationKeys.SETTINGS_LOGIN_SUBTITLE),
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}