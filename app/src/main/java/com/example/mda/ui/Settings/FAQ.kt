package com.example.mda.ui.Settings


import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.mda.ui.navigation.TopBarState
import com.example.mda.ui.theme.AppBackgroundGradient
import com.example.mda.localization.LocalizationKeys
import com.example.mda.localization.localizedString

@Composable
fun HelpScreen(
    navController: NavController,
    onTopBarStateChange: (TopBarState) -> Unit
) {
    onTopBarStateChange(
        TopBarState(
            title = localizedString(LocalizationKeys.HELP_TITLE),
            showBackButton = true
        )
    )
    val faqs = listOf(
        localizedString(LocalizationKeys.HELP_FAQ_Q1) to localizedString(LocalizationKeys.HELP_FAQ_A1),
        localizedString(LocalizationKeys.HELP_FAQ_Q2) to localizedString(LocalizationKeys.HELP_FAQ_A2),
        localizedString(LocalizationKeys.HELP_FAQ_Q3) to localizedString(LocalizationKeys.HELP_FAQ_A3),
        localizedString(LocalizationKeys.HELP_FAQ_Q4) to localizedString(LocalizationKeys.HELP_FAQ_A4),
        localizedString(LocalizationKeys.HELP_FAQ_Q5) to localizedString(LocalizationKeys.HELP_FAQ_A5),
        localizedString(LocalizationKeys.HELP_FAQ_Q6) to localizedString(LocalizationKeys.HELP_FAQ_A6)
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

            faqs.forEach { (question, answer) ->
                FAQItem(question = question, answer = answer)
            }
        }
    }
}

@Composable
fun FAQItem(question: String, answer: String) {
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.2f))
            .padding(14.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = !expanded },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = question,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.weight(1f)
            )

            Icon(
                imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        }

        AnimatedVisibility(visible = expanded) {
            Text(
                text = answer,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}
