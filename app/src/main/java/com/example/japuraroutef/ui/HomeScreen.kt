package com.example.japuraroutef.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.example.japuraroutef.R

@Composable
fun HomeScreen(onNavigateToMap: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // Background image with heavy blur for liquid glass effect
        val bgPainter: Painter = painterResource(id = R.drawable.gemini_generated_image_hxfk22hxfk22hxfk)
        Image(
            painter = bgPainter,
            contentDescription = "Home Background",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .blur(radius = 80.dp)
        )

        // Dark gradient overlay for depth and contrast
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xCC000000),
                            Color(0x99000000),
                            Color(0xDD000000)
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            // Scrollable content
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp)
                    .padding(top = 20.dp)
            ) {
                Spacer(modifier = Modifier.height(20.dp))

            // Header with Shield Logo and Title
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Shield Logo with golden outline
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .border(
                            width = 2.dp,
                            color = Color(0xFFD4AF37),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .background(
                            color = Color.Transparent,
                            shape = RoundedCornerShape(12.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "F",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFD4AF37)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Faculty Super App",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White,
                    fontWeight = FontWeight.Normal,
                    fontSize = 22.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Glassmorphic Welcome Card with golden border
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp)
                    .border(
                        width = 1.dp,
                        color = Color(0x80D4AF37),
                        shape = RoundedCornerShape(32.dp)
                    )
                    .background(
                        color = Color(0x40000000),
                        shape = RoundedCornerShape(32.dp)
                    )
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Welcome back!",
                            color = Color.White,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Check your updated Class Schedule.",
                            color = Color(0xFFAAAAAA),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Normal
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                        tint = Color(0xFFAAAAAA),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Main Feature Cards Grid - Exact layout from reference
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Row 1: Campus Map & Auditorium Booking
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    FeatureCard(
                        modifier = Modifier.weight(1f),
                        title = "Campus\nMap",
                        buttonText = "View",
                        icon = Icons.Default.LocationOn,
                        gradient = Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF6B2D1D),
                                Color(0xFF8B4513),
                                Color(0xFFD4A574)
                            )
                        ),
                        textColor = Color.White,
                        iconTint = Color(0xFFD4AF37),
                        onClick = onNavigateToMap
                    )
                    FeatureCard(
                        modifier = Modifier.weight(1f),
                        title = "Auditorium\nBooking",
                        buttonText = "Book",
                        icon = Icons.Default.DateRange,
                        gradient = Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFFD8D8D8),
                                Color(0xFFE8E8E8),
                                Color(0xFFCDB891)
                            )
                        ),
                        textColor = Color.Black,
                        iconTint = Color(0xFF5D4037),
                        onClick = { /* TODO */ }
                    )
                }

                // Row 2: Events & Places & Services
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    FeatureCard(
                        modifier = Modifier.weight(1f),
                        title = "Events",
                        buttonText = "View",
                        icon = Icons.Default.Notifications,
                        gradient = Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFFFFEB3B),
                                Color(0xFFFDD835),
                                Color(0xFFFBC02D)
                            )
                        ),
                        textColor = Color.Black,
                        iconTint = Color(0xFF424242),
                        onClick = { /* TODO */ }
                    )
                    FeatureCard(
                        modifier = Modifier.weight(1f),
                        title = "Places &\nServices",
                        buttonText = "View",
                        icon = Icons.Default.Place,
                        gradient = Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF64B5F6),
                                Color(0xFF90CAF9),
                                Color(0xFFBBDEFB)
                            )
                        ),
                        textColor = Color.Black,
                        iconTint = Color(0xFF1976D2),
                        onClick = { /* TODO */ }
                    )
                }

                // Row 3: GPA Calculator & Class Schedule
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    FeatureCard(
                        modifier = Modifier.weight(1f),
                        title = "GPA\nCalculator",
                        buttonText = "View",
                        icon = Icons.Default.AccountBox,
                        gradient = Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF6D5043),
                                Color(0xFF8D6E63),
                                Color(0xFF9E8275)
                            )
                        ),
                        textColor = Color.White,
                        iconTint = Color(0xFFD4AF37),
                        onClick = { /* TODO */ }
                    )
                    FeatureCard(
                        modifier = Modifier.weight(1f),
                        title = "Class\nSchedule",
                        buttonText = "View",
                        icon = Icons.Default.Info,
                        gradient = Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF1A1A1A),
                                Color(0xFF2A2A2A),
                                Color(0xFF212121)
                            )
                        ),
                        textColor = Color.White,
                        iconTint = Color(0xFFD4AF37),
                        onClick = { /* TODO */ }
                    )
                }

                // Row 4: Notices, Transport Info & Study Resources (3 small cards)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    SmallFeatureCard(
                        modifier = Modifier.weight(1f),
                        title = "Notices",
                        buttonText = "Access",
                        icon = Icons.Default.Warning,
                        backgroundColor = Color(0xFFFFCC80),
                        textColor = Color.Black,
                        onClick = { /* TODO */ }
                    )
                    SmallFeatureCard(
                        modifier = Modifier.weight(1f),
                        title = "Transport\nInfo",
                        buttonText = "Access",
                        icon = Icons.Default.Call,
                        backgroundColor = Color(0xFF2196F3),
                        textColor = Color.White,
                        onClick = { /* TODO */ }
                    )
                    SmallFeatureCard(
                        modifier = Modifier.weight(1f),
                        title = "Study\nResources",
                        buttonText = "Access",
                        icon = Icons.Default.Email,
                        backgroundColor = Color(0xFFB39DDB),
                        textColor = Color.Black,
                        onClick = { /* TODO */ }
                    )
                }
            }

            // Bottom padding for the navigation bar space
            Spacer(modifier = Modifier.height(100.dp))
        }
        }

        // Fixed Bottom Navigation Bar
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp)
        ) {
            BottomNavigationBar()
        }
    }
}

// Feature Card Component with glassmorphic styling
@Composable
fun FeatureCard(
    modifier: Modifier = Modifier,
    title: String,
    buttonText: String,
    icon: ImageVector,
    gradient: Brush,
    textColor: Color = Color.White,
    iconTint: Color = Color.White,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = modifier.height(180.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(gradient)
        ) {
            // Glassmorphic overlay with transparency
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.15f),
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.2f)
                            )
                        )
                    )
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = title,
                        fontSize = 19.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = textColor,
                        lineHeight = 23.sp
                    )
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = iconTint,
                        modifier = Modifier.size(30.dp)
                    )
                }

                // Button with border
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .border(
                            width = 1.5.dp,
                            color = textColor.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(22.dp)
                        )
                        .background(
                            color = Color.White.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(22.dp)
                        )
                        .clip(RoundedCornerShape(22.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = buttonText,
                        fontWeight = FontWeight.Medium,
                        fontSize = 15.sp,
                        color = textColor
                    )
                }
            }
        }
    }
}

// Small Feature Card for 3-column layout
@Composable
fun SmallFeatureCard(
    modifier: Modifier = Modifier,
    title: String,
    buttonText: String,
    icon: ImageVector,
    backgroundColor: Color,
    textColor: Color = Color.Black,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = modifier.height(180.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor)
        ) {
            // Subtle glassmorphic overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.1f),
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.1f)
                            )
                        )
                    )
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = title,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = textColor,
                        lineHeight = 19.sp
                    )
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = textColor,
                        modifier = Modifier.size(26.dp)
                    )
                }

                // Button with border
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp)
                        .border(
                            width = 1.5.dp,
                            color = textColor.copy(alpha = 0.4f),
                            shape = RoundedCornerShape(20.dp)
                        )
                        .background(
                            color = Color.White.copy(alpha = 0.25f),
                            shape = RoundedCornerShape(20.dp)
                        )
                        .clip(RoundedCornerShape(20.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = buttonText,
                        fontWeight = FontWeight.Medium,
                        fontSize = 13.sp,
                        color = textColor
                    )
                }
            }
        }
    }
}

@Composable
fun BottomNavigationBar() {
    // Glassmorphic bottom nav with golden border
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp)
            .padding(horizontal = 16.dp)
            .border(
                width = 1.dp,
                color = Color(0x80D4AF37),
                shape = RoundedCornerShape(35.dp)
            )
            .background(
                color = Color(0x80000000),
                shape = RoundedCornerShape(35.dp)
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomNavItem(Icons.Default.Home, true)
            BottomNavItem(Icons.Default.Search, false)
            BottomNavItem(Icons.Default.Info, false)
            BottomNavItem(Icons.Default.Notifications, false)
            BottomNavItem(Icons.Default.Person, false)
        }
    }
}

@Composable
fun BottomNavItem(icon: ImageVector, isSelected: Boolean) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.padding(vertical = 8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (isSelected) Color(0xFFD4AF37) else Color(0xFF808080),
            modifier = Modifier.size(26.dp)
        )

        if (isSelected) {
            Spacer(modifier = Modifier.height(4.dp))
            Box(
                modifier = Modifier
                    .size(4.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFD4AF37))
            )
        }
    }
}
