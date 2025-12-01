package com.example.mda.ui.screens.settings

import android.os.Build
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.mda.notifications.NotificationHelper
import com.example.mda.work.InactiveUserWorker
import com.example.mda.work.SuggestedMovieWorker
import com.example.mda.work.TrendingReminderWorker
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.core.content.edit
import com.example.mda.localization.LocalizationKeys
import com.example.mda.localization.localizedString
import com.example.mda.ui.navigation.TopBarState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeveloperToolsScreen(navController: NavController, onTopBarStateChange: (TopBarState) -> Unit) {
    val context = LocalContext.current

    onTopBarStateChange(
        TopBarState(
            title = localizedString(LocalizationKeys.DEVELOPER_TOOLS),
            showBackButton = true
        )
    )


        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 20.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            DeveloperInfoCard(
                title = "Test Notification",
                icon = Icons.Default.Notifications,
                description = "Send a test notification to verify notification setup."
            ) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    NotificationHelper.sendNotification(
                        context,
                        "تست الإشعارات ",
                        "ده إشعار تجريبي عشان نتأكد إن الدنيا شغالة!"
                    )
                }
            }

            DeveloperInfoCard(
                title = "Force Start: Trending Worker",
                icon = Icons.Default.TrendingUp,
                description = "Manually trigger Trending Worker."
            ) {
                val request = OneTimeWorkRequestBuilder<TrendingReminderWorker>().build()
                WorkManager.getInstance(context).enqueue(request)
            }

            DeveloperInfoCard(
                title = "Force Start: Suggested Movie",
                icon = Icons.Default.Movie,
                description = "Run Suggested Movie Worker now."
            ) {
                val request = OneTimeWorkRequestBuilder<SuggestedMovieWorker>().build()
                WorkManager.getInstance(context).enqueue(request)
            }

            DeveloperInfoCard(
                title = "Test Inactive User (Hack Time)",
                icon = Icons.Default.BugReport,
                description = "Simulate an inactive user for testing reminders.",
                highlight = true
            ) {
                val prefs = context.getSharedPreferences("app_prefs", android.content.Context.MODE_PRIVATE)
                val threeDaysAgo = System.currentTimeMillis() - (72L * 60 * 60 * 1000)
                prefs.edit { putLong("last_open", threeDaysAgo) }

                val request = OneTimeWorkRequestBuilder<InactiveUserWorker>().build()
                WorkManager.getInstance(context).enqueue(request)
            }

            Spacer(Modifier.height(80.dp))
        }
    }

@Composable
fun DeveloperInfoCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    description: String,
    highlight: Boolean = false,
    onClick: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    val background = colorScheme.surface
    val textColor = colorScheme.onSurface

    // لو حابة تميّزي الكارت "الخطر" بشكل طفيف من غير لون صارخ
    val borderColor = if (highlight) colorScheme.primary.copy(alpha = 0.25f)
    else colorScheme.outline.copy(alpha = 0.15f)

    Card(
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, borderColor),
        colors = CardDefaults.cardColors(containerColor = background.copy(alpha = 0.95f)),
        elevation = CardDefaults.cardElevation(3.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, contentDescription = null, tint = colorScheme.primary)
                Spacer(Modifier.width(8.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = textColor
                )
            }
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall.copy(color = textColor.copy(alpha = 0.8f))
            )
        }
    }
}