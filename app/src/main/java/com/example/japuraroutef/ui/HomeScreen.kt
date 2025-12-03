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

            // Main Feature Cards Grid - Professional Glassmorphism Design
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Row 1: Campus Map & Auditorium Booking
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    ModernGlassCard(
                        modifier = Modifier.weight(1f),
                        title = "Campus\nMap",
                        buttonText = "View",
                        icon = Icons.Default.LocationOn,
                        glassColor = Color(0x40667EEA), // Modern blue glass
                        iconTint = Color(0xFF667EEA),
                        textColor = Color.White,
                        onClick = onNavigateToMap
                    )
                    ModernGlassCard(
                        modifier = Modifier.weight(1f),
                        title = "Auditorium\nBooking",
                        buttonText = "Book",
                        icon = Icons.Default.DateRange,
                        glassColor = Color(0x40F093FB), // Modern pink glass
                        iconTint = Color(0xFFF093FB),
                        textColor = Color.White,
                        onClick = { /* TODO */ }
                    )
                }

                // Row 2: Events & Places & Services
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    ModernGlassCard(
                        modifier = Modifier.weight(1f),
                        title = "Events",
                        buttonText = "View",
                        icon = Icons.Default.Notifications,
                        glassColor = Color(0x404FACFE), // Modern cyan glass
                        iconTint = Color(0xFF4FACFE),
                        textColor = Color.White,
                        onClick = { /* TODO */ }
                    )
                    ModernGlassCard(
                        modifier = Modifier.weight(1f),
                        title = "Places &\nServices",
                        buttonText = "View",
                        icon = Icons.Default.Place,
                        glassColor = Color(0x4043E97B), // Modern green glass
                        iconTint = Color(0xFF43E97B),
                        textColor = Color.White,
                        onClick = { /* TODO */ }
                    )
                }

                // Row 3: GPA Calculator & Class Schedule
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    ModernGlassCard(
                        modifier = Modifier.weight(1f),
                        title = "GPA\nCalculator",
                        buttonText = "Calculate",
                        icon = Icons.Default.AccountBox,
                        glassColor = Color(0x40FA709A), // Modern coral glass
                        iconTint = Color(0xFFFA709A),
                        textColor = Color.White,
                        onClick = { /* TODO */ }
                    )
                    ModernGlassCard(
                        modifier = Modifier.weight(1f),
                        title = "Class\nSchedule",
                        buttonText = "View",
                        icon = Icons.Default.Info,
                        glassColor = Color(0x40FEE140), // Modern yellow glass
                        iconTint = Color(0xFFFEE140),
                        textColor = Color.White,
                        onClick = { /* TODO */ }
                    )
                }

                // Row 4: Notices, Transport Info & Study Resources (3 small cards)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    SmallModernGlassCard(
                        modifier = Modifier.weight(1f),
                        title = "Notices",
                        buttonText = "Access",
                        icon = Icons.Default.Warning,
                        glassColor = Color(0x40FD1D1D), // Modern red glass
                        iconTint = Color(0xFFFD1D1D),
                        textColor = Color.White,
                        onClick = { /* TODO */ }
                    )
                    SmallModernGlassCard(
                        modifier = Modifier.weight(1f),
                        title = "Transport\nInfo",
                        buttonText = "Access",
                        icon = Icons.Default.Call,
                        glassColor = Color(0x408E54E9), // Modern purple glass
                        iconTint = Color(0xFF8E54E9),
                        textColor = Color.White,
                        onClick = { /* TODO */ }
                    )
                    SmallModernGlassCard(
                        modifier = Modifier.weight(1f),
                        title = "Study\nResources",
                        buttonText = "Access",
                        icon = Icons.Default.Email,
                        glassColor = Color(0x4000D2FF), // Modern sky blue glass
                        iconTint = Color(0xFF00D2FF),
                        textColor = Color.White,
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

// Modern Glassmorphic Card Component - Professional & Clean
@Composable
fun ModernGlassCard(
    modifier: Modifier = Modifier,
    title: String,
    buttonText: String,
    icon: ImageVector,
    glassColor: Color,
    iconTint: Color,
    textColor: Color = Color.White,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = modifier.height(180.dp),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 8.dp,
            pressedElevation = 12.dp
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    color = glassColor,
                    shape = RoundedCornerShape(28.dp)
                )
                .border(
                    width = 1.dp,
                    color = Color.White.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(28.dp)
                )
        ) {
            // Top light reflection for glass effect
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(70.dp)
                    .align(Alignment.TopCenter)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.2f),
                                Color.Transparent
                            )
                        ),
                        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
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
                        fontWeight = FontWeight.Bold,
                        color = textColor,
                        lineHeight = 24.sp
                    )

                    // Icon container with subtle glass effect
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(
                                color = Color.White.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(14.dp)
                            )
                            .border(
                                width = 1.dp,
                                color = Color.White.copy(alpha = 0.3f),
                                shape = RoundedCornerShape(14.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = title,
                            tint = iconTint,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                }

                // Glass button
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                        .background(
                            color = Color.White.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(23.dp)
                        )
                        .border(
                            width = 1.dp,
                            color = Color.White.copy(alpha = 0.4f),
                            shape = RoundedCornerShape(23.dp)
                        )
                        .clip(RoundedCornerShape(23.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = buttonText,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = textColor
                    )
                }
            }
        }
    }
}

// Small Modern Glassmorphic Card
@Composable
fun SmallModernGlassCard(
    modifier: Modifier = Modifier,
    title: String,
    buttonText: String,
    icon: ImageVector,
    glassColor: Color,
    iconTint: Color,
    textColor: Color = Color.White,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = modifier.height(180.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 8.dp,
            pressedElevation = 12.dp
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    color = glassColor,
                    shape = RoundedCornerShape(24.dp)
                )
                .border(
                    width = 1.dp,
                    color = Color.White.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(24.dp)
                )
        ) {
            // Top light reflection
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .align(Alignment.TopCenter)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.2f),
                                Color.Transparent
                            )
                        ),
                        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
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
                        fontWeight = FontWeight.Bold,
                        color = textColor,
                        lineHeight = 19.sp
                    )

                    // Icon container
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(
                                color = Color.White.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .border(
                                width = 1.dp,
                                color = Color.White.copy(alpha = 0.3f),
                                shape = RoundedCornerShape(12.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = title,
                            tint = iconTint,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }

                // Glass button
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(42.dp)
                        .background(
                            color = Color.White.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(21.dp)
                        )
                        .border(
                            width = 1.dp,
                            color = Color.White.copy(alpha = 0.4f),
                            shape = RoundedCornerShape(21.dp)
                        )
                        .clip(RoundedCornerShape(21.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = buttonText,
                        fontWeight = FontWeight.Bold,
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
