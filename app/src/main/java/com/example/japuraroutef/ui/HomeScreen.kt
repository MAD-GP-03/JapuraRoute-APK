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
                // Row 1: Campus Map (dark red-brown) & Auditorium Booking (gray light)
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
                        tintColor = Color(0xFF3F1F1F), // Dark red-brown (zinc-800 to red-950)
                        onClick = onNavigateToMap
                    )
                    LiquidGlassCard(
                        modifier = Modifier.weight(1f),
                        title = "Auditorium\nBooking",
                        buttonText = "Book",
                        icon = Icons.Default.DateRange,
                        isEmphasized = true,
                        tintColor = Color(0xFFD1D5DB), // Light gray (gray-300)
                        onClick = { /* TODO */ }
                    )
                }

                // Row 2: Events (yellow-orange) & Places & Services (sky-blue)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    LiquidGlassCard(
                        modifier = Modifier.weight(1f),
                        title = "Events",
                        buttonText = "View",
                        icon = Icons.Default.Notifications,
                        isEmphasized = true,
                        tintColor = Color(0xFFFCD34D), // Yellow-300 (yellow-200 to orange-400)
                        onClick = { /* TODO */ }
                    )
                    LiquidGlassCard(
                        modifier = Modifier.weight(1f),
                        title = "Places &\nServices",
                        buttonText = "View",
                        icon = Icons.Default.Place,
                        isEmphasized = true,
                        tintColor = Color(0xFF7DD3FC), // Sky-300 (sky-200 to blue-400)
                        onClick = { /* TODO */ }
                    )
                }

                // Row 3: GPA Calculator (zinc dark) & Class Schedule (zinc-black)
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
                        tintColor = Color(0xFF27272A), // Zinc-800 (dark gray)
                        onClick = { /* TODO */ }
                    )
                    LiquidGlassCard(
                        modifier = Modifier.weight(1f),
                        title = "Class\nSchedule",
                        buttonText = "View",
                        icon = Icons.Default.Info,
                        isEmphasized = true,
                        tintColor = Color(0xFF18181B), // Zinc-900 (very dark gray)
                        onClick = { /* TODO */ }
                    )
                }

                // Row 4: Notices (orange), Transport Info (sky-blue) & Study Resources (purple-indigo)
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
                        tintColor = Color(0xFFFED7AA), // Orange-200
                        onClick = { /* TODO */ }
                    )
                    SmallLiquidGlassCard(
                        modifier = Modifier.weight(1f),
                        title = "Transport\nInfo",
                        buttonText = "Access",
                        icon = Icons.Default.Call,
                        isEmphasized = true,
                        tintColor = Color(0xFF0EA5E9), // Sky-500 (sky-400 to blue-600)
                        onClick = { /* TODO */ }
                    )
                    SmallLiquidGlassCard(
                        modifier = Modifier.weight(1f),
                        title = "Study\nResources",
                        buttonText = "Access",
                        icon = Icons.Default.Email,
                        isEmphasized = true,
                        tintColor = Color(0xFFC4B5FD), // Purple-300 (purple-300 to indigo-400)
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

// Liquid Glass Card with Gradient Colors
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
    // Determine text color based on background
    val textColor = when (tintColor) {
        Color(0xFFD1D5DB) -> Color(0xFF1F2937) // Dark text for light gray background
        else -> Color.White
    }

    val iconBgColor = when (tintColor) {
        Color(0xFFD1D5DB) -> Color(0x20000000) // Dark semi-transparent for light bg
        else -> Color(0x20FFFFFF) // Light semi-transparent for dark bg
    }

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
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            tintColor.copy(alpha = 0.7f),
                            tintColor.copy(alpha = 0.5f)
                        )
                    ),
                    shape = RoundedCornerShape(28.dp)
                )
                .border(
                    width = 1.dp,
                    color = tintColor.copy(alpha = 0.4f),
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
                        color = textColor,
                        lineHeight = 24.sp
                    )

                    // Icon with glassmorphic background
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(
                                color = iconBgColor,
                                shape = RoundedCornerShape(14.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = title,
                            tint = textColor.copy(alpha = 0.9f),
                            modifier = Modifier.size(26.dp)
                        )
                    }
                }

                // Glassmorphic Button
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                        .background(
                            color = when (tintColor) {
                                Color(0xFFD1D5DB) -> Color(0x40000000)
                                else -> Color(0x30FFFFFF)
                            },
                            shape = RoundedCornerShape(23.dp)
                        )
                        .border(
                            width = 1.dp,
                            color = when (tintColor) {
                                Color(0xFFD1D5DB) -> Color(0x40000000)
                                else -> Color(0x40FFFFFF)
                            },
                            shape = RoundedCornerShape(23.dp)
                        )
                        .clip(RoundedCornerShape(23.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = buttonText,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp,
                        color = textColor.copy(alpha = 0.9f)
                    )
                }
            }
        }
    }
}

// Small Liquid Glass Card with Gradient Colors
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
    // Determine text color based on background - all small cards use white text
    val textColor = Color.White

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
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            tintColor.copy(alpha = 0.75f),
                            tintColor.copy(alpha = 0.6f)
                        )
                    ),
                    shape = RoundedCornerShape(24.dp)
                )
                .border(
                    width = 1.dp,
                    color = tintColor.copy(alpha = 0.4f),
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
                        color = textColor,
                        lineHeight = 19.sp
                    )

                    // Icon with glassmorphic background
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(
                                color = Color(0x20FFFFFF),
                                shape = RoundedCornerShape(12.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = title,
                            tint = textColor.copy(alpha = 0.9f),
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }

                // Glassmorphic Button
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(42.dp)
                        .background(
                            color = Color(0x30FFFFFF),
                            shape = RoundedCornerShape(21.dp)
                        )
                        .border(
                            width = 1.dp,
                            color = Color(0x40FFFFFF),
                            shape = RoundedCornerShape(21.dp)
                        )
                        .clip(RoundedCornerShape(21.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = buttonText,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp,
                        color = textColor.copy(alpha = 0.9f)
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
