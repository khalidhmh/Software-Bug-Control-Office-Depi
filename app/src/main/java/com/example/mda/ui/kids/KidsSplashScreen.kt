package com.example.mda.ui.kids

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.*
import com.example.mda.R
import kotlinx.coroutines.delay

@Composable
fun KidsSplashScreen(
    onFinished: () -> Unit
) {
    // تحميل الأنيميشن
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.kids_mode))

    // الأنيميشن يتكرر مرتين فقط
    val progress by animateLottieCompositionAsState(
        composition,
        iterations = 2
    )

    // بعد ما يخلص مرتين (حوالي ثانيتين مثلاً) نعمل الانتقال
    LaunchedEffect(progress) {
        if (progress == 1f) {
            // تأخير بسيط علشان يعمل تأثير الـ Fade بلُطف
            delay(300)
            onFinished()
        }
    }

    // لون النص يتأقلم تلقائيًا حسب الثيم الحالي (فاتح أو غامق)
    val textColor = MaterialTheme.colorScheme.onBackground

    //  شاشة الأنيميشن مع نص ترحيبي
    Crossfade(targetState = progress < 1f, label = "fade_anim") { isPlaying ->
        if (isPlaying) {
            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    //  عرض الأنيميشن في المنتصف
                    LottieAnimation(
                        composition = composition,
                        progress = { progress },
                        modifier = Modifier.size(220.dp)
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    //  النص الترحيبي
                    Text(
                        text = "WELCOME TO KIDS MODE ",
                        color = textColor,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}