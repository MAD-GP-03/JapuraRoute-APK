package com.example.japuraroutef.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
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

/**
 * Get Started Screen - matches ExtendedSplashScreen design with a "Get Started" button
 */
@Composable
fun GetStartedScreen(
    onGetStartedClick: () -> Unit
) {
    var startAnimation by remember { mutableStateOf(false) }

    // Fade in animation
    val alphaAnim = animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(durationMillis = 1000),
        label = "Fade In"
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
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                color = com.example.japuraroutef.ui.theme.BackgroundDark
            )
    ) {
        // Main content - centered
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp)
                .navigationBarsPadding()
                .alpha(alphaAnim.value),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(modifier = Modifier.height(1.dp))

            // Centered logo and text
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.weight(1f)
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
                        painter = painterResource(id = R.drawable.ic_fotgo_logo),
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

            // Get Started Button at bottom
            Button(
                onClick = onGetStartedClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = MaterialTheme.shapes.extraLarge,
                colors = ButtonDefaults.buttonColors(
                    containerColor = com.example.japuraroutef.ui.theme.Primary,
                    contentColor = Color(0xFF1C1B1F) // Dark text color
                )
            ) {
                Text(
                    text = "Get Started",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1C1B1F) // Dark text color
                )
            }
        }
    }
}

