package com.example.mda.ui.Settings

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.mda.ui.navigation.TopBarState
import com.example.mda.localization.LocalizationKeys
import com.example.mda.localization.localizedString

@Composable
fun AboutScreen(
    navController: NavController,
    onTopBarStateChange: (TopBarState) -> Unit
) {
    val dark = isSystemInDarkTheme()
    val colorScheme = MaterialTheme.colorScheme
    onTopBarStateChange(
        TopBarState(
            title = localizedString(LocalizationKeys.ABOUT_TITLE),
            showBackButton = true
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 20.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            HeroSection(dark)
            InfoCard(
                title = localizedString(LocalizationKeys.ABOUT_OBJECTIVES),
                icon = Icons.Default.Star,
                items = listOf(
                    localizedString(LocalizationKeys.ABOUT_OBJ_1),
                    localizedString(LocalizationKeys.ABOUT_OBJ_2),
                    localizedString(LocalizationKeys.ABOUT_OBJ_3),
                    localizedString(LocalizationKeys.ABOUT_OBJ_4),
                    localizedString(LocalizationKeys.ABOUT_OBJ_5)
                )
            )
            InfoCard(
                title = localizedString(LocalizationKeys.ABOUT_TEAM),
                icon = Icons.Default.Group,
                items = listOf(
                    localizedString(LocalizationKeys.ABOUT_TEAM_MEMBER_1),
                    localizedString(LocalizationKeys.ABOUT_TEAM_MEMBER_2),
                    localizedString(LocalizationKeys.ABOUT_TEAM_MEMBER_3),
                    localizedString(LocalizationKeys.ABOUT_TEAM_MEMBER_4),
                    localizedString(LocalizationKeys.ABOUT_TEAM_MEMBER_5)

                )
            )

            InfoCard(
                title = localizedString(LocalizationKeys.ABOUT_TECH_TITLE),
                icon = Icons.Default.Code,
                items = listOf(
                    localizedString(LocalizationKeys.ABOUT_TECH_ITEM_1),
                    localizedString(LocalizationKeys.ABOUT_TECH_ITEM_2),
                    localizedString(LocalizationKeys.ABOUT_TECH_ITEM_3),
                    localizedString(LocalizationKeys.ABOUT_TECH_ITEM_4),
                    localizedString(LocalizationKeys.ABOUT_TECH_ITEM_5),
                    localizedString(LocalizationKeys.ABOUT_TECH_ITEM_6)
                )
            )

            Spacer(Modifier.height(8.dp))
            Text(
                text = localizedString(LocalizationKeys.ABOUT_COPYRIGHT),
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                color = colorScheme.onBackground.copy(alpha = 0.7f),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Spacer(Modifier.height(80.dp))
        }
    }
}

@Composable
fun HeroSection(dark: Boolean) {
    val colorScheme = MaterialTheme.colorScheme


    val cardGradient = if (dark) {
        Brush.verticalGradient(
            colorStops = arrayOf(
                0f to colorScheme.primary.copy(alpha = 0.15f),
                0.3f to colorScheme.primary.copy(alpha = 0.10f),
                0.7f to colorScheme.surface.copy(alpha = 0.08f),
                1f to colorScheme.surface.copy(alpha = 0.05f)
            )
        )
    } else {
        Brush.verticalGradient(
            colorStops = arrayOf(
                0f to Color(0xFFE3F2FD),   // أفتح درجة فوق
                0.4f to Color(0xFFD0E8FA), // وسط فاتح ناعم
                1f to Color(0xFFB3E5FC)    // تحت أغمق درجة بسيطة
            )
        )
    }

    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
            .padding(horizontal = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .background(cardGradient)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            // 💡 محتوى الكارت
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Movie,
                    contentDescription = null,
                    tint = colorScheme.primary,
                    modifier = Modifier.size(48.dp)
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = localizedString(LocalizationKeys.ABOUT_APP_NAME),
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    color = colorScheme.primary,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = localizedString(LocalizationKeys.ABOUT_APP_DESC),
                    style = MaterialTheme.typography.bodyMedium.copy(color = colorScheme.onSurfaceVariant),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
@Composable
fun InfoCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    items: List<String>
) {
    val colorScheme = MaterialTheme.colorScheme
    val background = colorScheme.surface
    val textColor = colorScheme.onSurface

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = background.copy(alpha = 0.9f)),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
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
                    color = colorScheme.primary
                )
            }

            items.forEach { text ->
                Row(
                    verticalAlignment = Alignment.Top,
                    modifier = Modifier.padding(start = 6.dp)
                ) {
                    Text("✓", color = colorScheme.primary)
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text,
                        style = MaterialTheme.typography.bodyMedium.copy(color = textColor)
                    )
                }
            }
        }
    }
}
