package com.example.mda.ui.screens.settings

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.mda.localization.LocalizationKeys
import com.example.mda.localization.localizedString
import com.example.mda.ui.navigation.TopBarState

@Composable
fun PrivacyPolicyScreen(
    navController: NavController,
    onTopBarStateChange: (TopBarState) -> Unit
) {
    onTopBarStateChange(
        TopBarState(
            title = localizedString(LocalizationKeys.PRIVACY_TITLE),
            showBackButton = true
        )
    )
    val context = LocalContext.current

    val policies = listOf(
        localizedString(LocalizationKeys.PRIVACY_COMMITMENT_TITLE) to localizedString(LocalizationKeys.PRIVACY_COMMITMENT_DESC),
        localizedString(LocalizationKeys.PRIVACY_DATA_TITLE) to localizedString(LocalizationKeys.PRIVACY_DATA_DESC),
        localizedString(LocalizationKeys.PRIVACY_MANAGEMENT_TITLE) to localizedString(LocalizationKeys.PRIVACY_MANAGEMENT_DESC),
        localizedString(LocalizationKeys.PRIVACY_LEARN_MORE_TITLE) to localizedString(LocalizationKeys.PRIVACY_LEARN_MORE_DESC)
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {

            Spacer(Modifier.height(16.dp))

            policies.forEachIndexed { index, (title, desc) ->
                if (index == policies.lastIndex) {
                    PrivacyItem(
                        question = title,
                        answer = desc,
                        showButton = true,
                        onClickButton = {
                            val intent = Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse("https://www.themoviedb.org/privacy-policy")
                            )
                            context.startActivity(intent)
                        }
                    )
                } else {
                    PrivacyItem(question = title, answer = desc)
                }
            }

            Spacer(Modifier.height(80.dp))
        }
    }
}

@Composable
fun PrivacyItem(
    question: String,
    answer: String,
    showButton: Boolean = false,
    onClickButton: (() -> Unit)? = null
) {
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.2f))
            .padding(14.dp)
    ) {
        // عنوان القسم
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = !expanded },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = question,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Medium),
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        }

        // النص داخل السؤال
        AnimatedVisibility(visible = expanded) {
            Column(
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Text(
                    text = answer,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
                    )
                )

                if (showButton && onClickButton != null) {
                    Spacer(Modifier.height(12.dp))
                    Button(
                        onClick = onClickButton,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Icon(Icons.Default.Policy, contentDescription = null)
                        Spacer(Modifier.width(6.dp))
                        Text(localizedString(LocalizationKeys.PRIVACY_VIEW_TMDB))
                    }
                }
            }
        }
    }
}