package com.example.mda.ui.screens.splash

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.mda.R
import com.example.mda.data.datastore.KidsSecurityDataStore
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first

@Composable
fun SplashScreen(navController: NavController) {
    var scale by remember { mutableStateOf(0f) }

    val anim = rememberInfiniteTransition()

    val scaleAnim by anim.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            tween(1200, easing = EaseInOutQuad),
            repeatMode = RepeatMode.Reverse
        )
    )

    val context = LocalContext.current
    val kidsStore = remember { KidsSecurityDataStore(context) }

    LaunchedEffect(Unit) {
        val isKidsActive = try { kidsStore.activeFlow.first() } catch (e: Exception) { false }
        delay(2000)
        val target = if (isKidsActive) "kids" else "home"
        navController.navigate(target) {
            popUpTo("splash") { inclusive = true }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(R.mipmap.splash),
            contentDescription = "App Logo",
            modifier = Modifier
                .scale(scaleAnim)
                .size(320.dp)
        )
    }
}