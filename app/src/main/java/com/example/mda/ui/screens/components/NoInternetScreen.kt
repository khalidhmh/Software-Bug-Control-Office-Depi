package com.example.mda.ui.screens.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.mda.R
import com.example.mda.ui.theme.AppBackgroundGradient

@Composable
fun NoInternetScreen(
    isDarkTheme: Boolean, // ✅ نستقبل حالة الثيم من الـ Main
    onRetry: () -> Unit
) {
    // ❌ قمنا بإزالة: val isDark = isSystemInDarkTheme()
    // لكي نعتمد على إعدادات التطبيق

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackgroundGradient(isDarkTheme)), // ✅ استخدام الثيم الممرر
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(32.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.network),
                contentDescription = "No Internet Connection",
                modifier = Modifier
                    .size(250.dp)
                    .padding(bottom = 16.dp)
            )

            Text(
                text = "Ooops!",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "No Internet Connection\nPlease check your network.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                modifier = Modifier.fillMaxWidth(0.6f)
            ) {
                Text(text = "Retry")
            }
        }
    }
}