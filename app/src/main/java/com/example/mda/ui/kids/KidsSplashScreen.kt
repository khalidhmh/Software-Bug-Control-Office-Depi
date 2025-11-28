package com.example.mda.ui.kids

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.example.mda.ui.theme.AppBackgroundGradient
import kotlinx.coroutines.delay

@Composable
fun KidsSplashScreen(
    onFinished: () -> Unit,
) {
    LaunchedEffect(Unit) {
        delay(1200)
        onFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackgroundGradient()),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Kids Mode",
            color = MaterialTheme.colorScheme.primary,
            fontSize = 32.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .alpha(0.95f)
        )

        Text(
            text = "🎈",
            fontSize = 80.sp,
            modifier = Modifier.align(Alignment.Center)
        )

        Text(
            text = "Welcome to a safe space",
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .alpha(0.9f)
        )
    }
}