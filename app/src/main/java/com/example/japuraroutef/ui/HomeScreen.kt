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
        // Background image with blur for liquid glass effect
        val bgPainter: Painter = painterResource(id = R.drawable.gemini_generated_image_hxfk22hxfk22hxfk)
        Image(
            painter = bgPainter,
            contentDescription = "Home Background",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .blur(radius = 70.dp)
        )

        // Lighter gradient overlay for better background visibility
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0x88000000),
                            Color(0x66000000),
                            Color(0x99000000)
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

            // Main Feature Cards Grid - Liquid Glass Design
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Row 1: Campus Map (emphasized with color) & Auditorium Booking
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    LiquidGlassCard(
                        modifier = Modifier.weight(1f),
                        title = "Campus\nMap",
                        buttonText = "View",
                        icon = Icons.Default.LocationOn,
                        isEmphasized = true,
                        tintColor = Color(0xFF007AFF), // iOS blue for emphasis
                        onClick = onNavigateToMap
                    )
                    LiquidGlassCard(
                        modifier = Modifier.weight(1f),
                        title = "Auditorium\nBooking",
                        buttonText = "Book",
                        icon = Icons.Default.DateRange,
                        isEmphasized = false,
                        onClick = { /* TODO */ }
                    )
                }

                // Row 2: Events & Places & Services
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    LiquidGlassCard(
                        modifier = Modifier.weight(1f),
                        title = "Events",
                        buttonText = "View",
                        icon = Icons.Default.Notifications,
                        isEmphasized = false,
                        onClick = { /* TODO */ }
                    )
                    LiquidGlassCard(
                        modifier = Modifier.weight(1f),
                        title = "Places &\nServices",
                        buttonText = "View",
                        icon = Icons.Default.Place,
                        isEmphasized = false,
                        onClick = { /* TODO */ }
                    )
                }

                // Row 3: GPA Calculator & Class Schedule
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    LiquidGlassCard(
                        modifier = Modifier.weight(1f),
                        title = "GPA\nCalculator",
                        buttonText = "Calculate",
                        icon = Icons.Default.AccountBox,
                        isEmphasized = true,
                        tintColor = Color(0xFF34C759), // iOS green for emphasis
                        onClick = { /* TODO */ }
                    )
                    LiquidGlassCard(
                        modifier = Modifier.weight(1f),
                        title = "Class\nSchedule",
                        buttonText = "View",
                        icon = Icons.Default.Info,
                        isEmphasized = false,
                        onClick = { /* TODO */ }
                    )
                }

                // Row 4: Notices, Transport Info & Study Resources (3 small cards)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    SmallLiquidGlassCard(
                        modifier = Modifier.weight(1f),
                        title = "Notices",
                        buttonText = "Access",
                        icon = Icons.Default.Warning,
                        isEmphasized = true,
                        tintColor = Color(0xFFFF3B30), // iOS red for status indicator
                        onClick = { /* TODO */ }
                    )
                    SmallLiquidGlassCard(
                        modifier = Modifier.weight(1f),
                        title = "Transport\nInfo",
                        buttonText = "Access",
                        icon = Icons.Default.Call,
                        isEmphasized = false,
                        onClick = { /* TODO */ }
                    )
                    SmallLiquidGlassCard(
                        modifier = Modifier.weight(1f),
                        title = "Study\nResources",
                        buttonText = "Access",
                        icon = Icons.Default.Email,
                        isEmphasized = false,
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

// Liquid Glass Card - Apple Design Principles
// Larger elements are more opaque for legibility, color used sparingly for emphasis
@Composable
fun LiquidGlassCard(
    modifier: Modifier = Modifier,
    title: String,
    buttonText: String,
    icon: ImageVector,
    isEmphasized: Boolean = false,
    tintColor: Color = Color(0xFF007AFF),
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = modifier.height(180.dp),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp,
            pressedElevation = 0.dp
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    // More opaque for larger elements (sidebar-like)
                    color = if (isEmphasized)
                        tintColor.copy(alpha = 0.18f)
                    else
                        Color.White.copy(alpha = 0.12f),
                    shape = RoundedCornerShape(28.dp)
                )
                .border(
                    width = 0.5.dp,
                    color = Color.White.copy(alpha = 0.18f),
                    shape = RoundedCornerShape(28.dp)
                )
        ) {

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
                        // Monochromatic - lighter text on dark background
                        color = Color.White.copy(alpha = 0.95f),
                        lineHeight = 24.sp
                    )

                    // Icon with minimal styling
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(
                                color = Color.White.copy(alpha = 0.08f),
                                shape = RoundedCornerShape(14.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = title,
                            // Color only for emphasized elements
                            tint = if (isEmphasized) tintColor else Color.White.copy(alpha = 0.7f),
                            modifier = Modifier.size(26.dp)
                        )
                    }
                }

                // Button - emphasized with tint only when needed
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                        .background(
                            // Color tint only for key actions
                            color = if (isEmphasized)
                                tintColor.copy(alpha = 0.25f)
                            else
                                Color.White.copy(alpha = 0.12f),
                            shape = RoundedCornerShape(23.dp)
                        )
                        .border(
                            width = 0.5.dp,
                            color = if (isEmphasized)
                                tintColor.copy(alpha = 0.4f)
                            else
                                Color.White.copy(alpha = 0.25f),
                            shape = RoundedCornerShape(23.dp)
                        )
                        .clip(RoundedCornerShape(23.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = buttonText,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp,
                        color = if (isEmphasized) tintColor else Color.White.copy(alpha = 0.9f)
                    )
                }
            }
        }
    }
}

// Small Liquid Glass Card - Toolbar/Tab Bar Style
// Smaller elements adapt between light/dark appearance, monochromatic scheme
@Composable
fun SmallLiquidGlassCard(
    modifier: Modifier = Modifier,
    title: String,
    buttonText: String,
    icon: ImageVector,
    isEmphasized: Boolean = false,
    tintColor: Color = Color(0xFF007AFF),
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = modifier.height(180.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp,
            pressedElevation = 0.dp
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    // Less opaque for smaller toolbar-like elements
                    color = if (isEmphasized)
                        tintColor.copy(alpha = 0.15f)
                    else
                        Color.White.copy(alpha = 0.10f),
                    shape = RoundedCornerShape(24.dp)
                )
                .border(
                    width = 0.5.dp,
                    color = Color.White.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(24.dp)
                )
        ) {

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
                        // Monochromatic - lighter on dark
                        color = Color.White.copy(alpha = 0.95f),
                        lineHeight = 19.sp
                    )

                    // Minimal icon styling
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(
                                color = Color.White.copy(alpha = 0.06f),
                                shape = RoundedCornerShape(12.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = title,
                            // Color only for status indicators
                            tint = if (isEmphasized) tintColor else Color.White.copy(alpha = 0.7f),
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }

                // Button with minimal tinting
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(42.dp)
                        .background(
                            color = if (isEmphasized)
                                tintColor.copy(alpha = 0.22f)
                            else
                                Color.White.copy(alpha = 0.10f),
                            shape = RoundedCornerShape(21.dp)
                        )
                        .border(
                            width = 0.5.dp,
                            color = if (isEmphasized)
                                tintColor.copy(alpha = 0.35f)
                            else
                                Color.White.copy(alpha = 0.22f),
                            shape = RoundedCornerShape(21.dp)
                        )
                        .clip(RoundedCornerShape(21.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = buttonText,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp,
                        color = if (isEmphasized) tintColor else Color.White.copy(alpha = 0.9f)
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
