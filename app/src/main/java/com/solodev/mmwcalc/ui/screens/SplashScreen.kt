package com.solodev.mmwcalc.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun MMWCalcSplashScreen(onFinished: () -> Unit) {

    val primaryBlue   = Color(0xFF185FA5)
    val purple        = Color(0xFF534AB7)
    val darkBg        = Color(0xFF1A1A2E)
    val lightBlue     = Color(0xFF7EC8FF)

    // Animate progress bar
    val progress = remember { Animatable(0f) }
    val quoteAlpha = remember { Animatable(0f) }
    val loadingAlpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        // Fade in quote
        quoteAlpha.animateTo(1f,
            animationSpec = tween(600, easing = EaseInOut))
        // Fade in loading
        loadingAlpha.animateTo(1f,
            animationSpec = tween(400))
        // Animate progress
        progress.animateTo(1f,
            animationSpec = tween(1800, easing = EaseInOut))
        delay(200)
        onFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(darkBg),
        contentAlignment = Alignment.Center
    ) {
        // Quote — top area
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .padding(top = 120.dp, start = 40.dp, end = 40.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text  = "❝",
                    fontSize = 32.sp,
                    color = purple.copy(alpha = quoteAlpha.value),
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text      = "You matter more\nthan your grades",
                    fontSize  = 22.sp,
                    fontWeight = FontWeight.SemiBold,
                    color     = lightBlue.copy(alpha = quoteAlpha.value),
                    textAlign = TextAlign.Center,
                    lineHeight = 32.sp
                )
                Spacer(Modifier.height(10.dp))
                Text(
                    text      = "— soloDev",
                    fontSize  = 14.sp,
                    fontStyle = FontStyle.Italic,
                    color     = purple.copy(alpha = quoteAlpha.value),
                    textAlign = TextAlign.Center
                )
            }
        }

        // Center — app name
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text       = "MMWCalc",
                fontSize   = 36.sp,
                fontWeight = FontWeight.Bold,
                color      = Color.White,
                letterSpacing = 2.sp
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text      = "Mathematics in the Modern World",
                fontSize  = 12.sp,
                color     = lightBlue.copy(alpha = 0.7f),
                letterSpacing = 1.sp
            )
        }

        // Loading — bottom area
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(bottom = 100.dp, start = 48.dp, end = 48.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            LinearProgressIndicator(
                progress       = { progress.value },
                modifier       = Modifier
                    .fillMaxWidth()
                    .height(4.dp),
                color          = primaryBlue,
                trackColor     = purple.copy(alpha = 0.2f),
                strokeCap      = StrokeCap.Round
            )
            Text(
                text      = "Loading...",
                fontSize  = 12.sp,
                color     = Color.White.copy(alpha = loadingAlpha.value * 0.5f),
                letterSpacing = 2.sp
            )
        }
    }
}