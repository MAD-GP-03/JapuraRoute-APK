package com.example.japuraroutef.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.japuraroutef.R
import kotlinx.coroutines.delay

/**
 * Extended Splash Screen with custom animation
 * This composable shows after the system splash screen
 * Mimics the HTML design with background blob and FOTGo branding
 */
@Composable
fun ExtendedSplashScreen(
    onSplashFinished: () -> Unit
) {
    var startAnimation by remember { mutableStateOf(false) }

    // Fade in animation
    val alphaAnim = animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(durationMillis = 1000),
        label = "Splash Fade"
    )

    // Scale animation for logo
    val scaleAnim = animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0.8f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "Logo Scale"
    )

    LaunchedEffect(Unit) {
        startAnimation = true
        delay(2500) // Show splash for 2.5 seconds
        onSplashFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                color = com.example.japuraroutef.ui.theme.BackgroundDark
            )
    ) {
        // Background blob decoration - positioned at top (optional decoration)
        // Uncomment if you want the background blob effect
        /* Image(
            painter = painterResource(id = R.drawable.splash_background_blob),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp)
                .align(Alignment.TopCenter)
                .offset(y = (-100).dp)
                .alpha(0.7f),
            alpha = 0.7f
        ) */

        // Main content - centered
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp)
                .alpha(alphaAnim.value),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Logo container with glassmorphism effect
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .scale(scaleAnim.value)
                    .background(
                        color = Color(0x1AFFFFFF), // 10% white transparency
                        shape = MaterialTheme.shapes.extraLarge
                    )
                    .padding(1.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_splash_logo),
                    contentDescription = "FOTGo Logo",
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // App name
            Text(
                text = "FOTGo",
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold,
                color = com.example.japuraroutef.ui.theme.OnBackgroundDark,
                letterSpacing = (-1).sp // tracking-tighter
            )
        }
    }
}

