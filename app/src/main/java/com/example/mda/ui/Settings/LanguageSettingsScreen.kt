package com.example.mda.ui.Settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.mda.localization.LocalizationKeys
import com.example.mda.localization.LocalizationManager
import com.example.mda.localization.localizedString
import com.example.mda.ui.navigation.TopBarState
import kotlinx.coroutines.launch

@Composable
fun LanguageSettingsScreen(
    navController: NavController,
    onTopBarStateChange: (TopBarState) -> Unit
) {
    val context = LocalContext.current
    val manager = remember { LocalizationManager(context) }
    val currentLanguage = manager.currentLanguage.collectAsState(initial = LocalizationManager.Language.ENGLISH).value
    val scope = rememberCoroutineScope()
    val title = localizedString(LocalizationKeys.SETTINGS_LANGUAGE)
    LaunchedEffect(title) {
        onTopBarStateChange(
            TopBarState(
                title = title,
                showBackButton = true
            )
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 560.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = localizedString(LocalizationKeys.SETTINGS_LANGUAGE_SELECT_TITLE),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            // Language Options
            LocalizationManager.Language.values().forEach { language ->
                LanguageOptionCard(
                    language = language,
                    isSelected = currentLanguage == language,
                    onClick = {
                        scope.launch {
                            manager.setLanguage(language)
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Info Card
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = localizedString(LocalizationKeys.SETTINGS_LANGUAGE_INFO_TITLE),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = localizedString(LocalizationKeys.SETTINGS_LANGUAGE_INFO_BODY),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = { navController.navigateUp() },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors()
            ) {
                Text(text = localizedString(LocalizationKeys.COMMON_BACK))
            }
        }

        Spacer(modifier = Modifier.height(80.dp))
    }
}

@Composable
private fun LanguageOptionCard(
    language: LocalizationManager.Language,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected)
                MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
            else
                MaterialTheme.colorScheme.surface
        ),
        border = if (isSelected)
            androidx.compose.foundation.BorderStroke(
                2.dp,
                MaterialTheme.colorScheme.primary
            )
        else
            null,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 4.dp else 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            RadioButton(selected = isSelected, onClick = onClick)

            Spacer(modifier = Modifier.width(12.dp))

            val flag = when (language) {
                LocalizationManager.Language.ENGLISH -> "\uD83C\uDDEC\uD83C\uDDE7" // 🇬🇧
                LocalizationManager.Language.ARABIC -> "\uD83C\uDDF8\uD83C\uDDE6" // 🇸🇦
                LocalizationManager.Language.GERMAN -> "\uD83C\uDDE9\uD83C\uDDEA" // 🇩🇪
            }
            Text(text = flag, style = MaterialTheme.typography.titleLarge)

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = language.displayName,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = language.code.uppercase(),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
