package com.example.mda.ui.screens.onboarding

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.mda.R
import com.example.mda.data.datastore.IntroDataStore
import com.example.mda.data.remote.model.Image
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(navController: NavController) {
    val pagerState = rememberPagerState(pageCount = { onboardingItems.size })
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val introDataStore = remember { IntroDataStore(context) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        HorizontalPager(
            state = pagerState
        ) { page ->
            Crossfade(targetState = onboardingItems[page]) { item ->
                OnboardingPage(item = item)
            }
        }
        }

        // ✅ Indicators + Buttons
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Bottom,
        ) {
            PagerIndicators(
                totalPages = onboardingItems.size,
                currentPage = pagerState.currentPage
            )

            Spacer(modifier = Modifier.height(32.dp))

            // الأزرار في الأسفل
            if (pagerState.currentPage < onboardingItems.lastIndex) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // زر Skip
                    TextButton(onClick = {
                        scope.launch { introDataStore.setIntroShown(true) }
                        navController.navigate("splash") {
                            popUpTo("onboarding") { inclusive = true }
                        }
                    }) {
                        Text("Skip", color = Color.White.copy(alpha = 0.7f))
                    }

                    // زر Next
                    TextButton(onClick = {
                        scope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        }
                    }) {
                        Text("Next →", color = Color.White)
                    }
                }
            } else {
                // في آخر صفحة يظهر زر "Start Discovering"
                // في آخر صفحة يظهر زر "Start Discovering"
                Button(
                    onClick = {
                        scope.launch {
                            introDataStore.setIntroShown(true)
                            kotlinx.coroutines.delay(200)
                            navController.navigate("splash") {
                                popUpTo("onboarding") { inclusive = true }
                                launchSingleTop = true
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    contentPadding = PaddingValues()
                ) {
                    Box(
                        modifier = Modifier
                            .background(
                                brush = Brush.horizontalGradient(
                                    listOf(Color(0xFF6A1B9A), Color(0xFF00BCD4))
                                ),
                                shape = RoundedCornerShape(16.dp)
                            )
                            .padding(vertical = 12.dp)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Start Discovering",
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Discover. Explore. Preview.",
                modifier = Modifier.align(Alignment.CenterHorizontally),
                color = Color.White.copy(alpha = 0.6f),
                fontSize = 12.sp
            )
        }
    }

@Composable
fun OnboardingPage(item: OnboardingItem) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    listOf(Color(0xFF15002C), Color(0xFF0E022A))
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = item.imageRes),
            contentDescription = item.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .alpha(0.25f)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp, vertical = 80.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(modifier = Modifier.height(48.dp))

            // Icon
            Text(
                text = item.icon,
                fontSize = 64.sp,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Title & Description
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                GradientText(
                    text = item.title,
                    gradient = Brush.horizontalGradient(
                        listOf(Color(0xFF00BCD4), Color(0xFFB388FF), Color(0xFFFF4081))
                    ),
                    fontSize = 32.sp,
                    fontWeight = FontWeight.ExtraBold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = item.subtitle,
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = item.description,
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp
                )
            }

            Spacer(modifier = Modifier.height(60.dp))
        }
    }
}

@Composable
fun PagerIndicators(totalPages: Int, currentPage: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        repeat(totalPages) { index ->
            val color = if (index == currentPage)
                Brush.horizontalGradient(listOf(Color(0xFF00BCD4), Color(0xFFB388FF)))
            else
                Brush.horizontalGradient(listOf(Color.White.copy(alpha = 0.3f), Color.White.copy(alpha = 0.3f)))

            Box(
                modifier = Modifier
                    .padding(horizontal = 4.dp)
                    .height(8.dp)
                    .width(if (index == currentPage) 24.dp else 8.dp)
                    .background(
                        brush = color,
                        shape = CircleShape
                    )
            )
        }
    }
}

@Composable
fun GradientText(
    text: String,
    gradient: Brush,
    fontSize: androidx.compose.ui.unit.TextUnit,
    fontWeight: FontWeight = FontWeight.Normal
) {
    Text(
        text = text,
        style = TextStyle(
            brush = gradient,
            fontSize = fontSize,
            fontWeight = fontWeight,
            textAlign = TextAlign.Center
        )
    )
}

// Data model
data class OnboardingItem(
    val title: String,
    val subtitle: String,
    val description: String,
    val imageRes: Int,
    val icon: String
)

val onboardingItems = listOf(
    OnboardingItem(
        title = "Discover",
        subtitle = "Movies You'll Love",
        description = "Explore trending films, hidden gems, and upcoming releases tailored to your taste.",
        imageRes = com.example.mda.R.drawable.discover,
        icon = "🔍"
    ),
    OnboardingItem(
        title = "Browse",
        subtitle = "By Genre & Category",
        description = "From Action to Comedy, find the right movie for every mood.",
        imageRes = com.example.mda.R.drawable.browse,
        icon = "🎭"
    ),
    OnboardingItem(
        title = "Preview",
        subtitle = "Stay Informed, Watch Trailers",
        description = "Check ratings, cast details, and watch trailers before you decide.",
        imageRes = R.drawable.preview,
        icon = "🎬"
    )
)