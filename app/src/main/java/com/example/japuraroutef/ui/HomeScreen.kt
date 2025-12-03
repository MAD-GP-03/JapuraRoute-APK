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
import androidx.compose.ui.draw.drawBehind
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
                // Shield Logo with golden glowing outline
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

            // Glassmorphic Welcome Card with golden glowing border
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
                        tintColor = Color(0xFFFFFFFF), // Base white for transparent effect
                        glowColor = Color(0xFFFFE8A3),
                        isTransparent = true, // Make this card transparent
                        textColorOverride = Color.White, // White text
                        onClick = onNavigateToMap
                    )

                    LiquidGlassCard(
                        modifier = Modifier.weight(1f),
                        title = "Auditorium\nBooking",
                        buttonText = "Book",
                        icon = Icons.Default.DateRange,
                        isEmphasized = false,
                        tintColor = Color(0xFFFFFFFF), // Base white for transparent effect
                        glowColor = Color(0xFFFFE8A3),
                        textColorOverride = Color.Black, // White text
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
                        isEmphasized = false,
                        tintColor = Color(0xFFFCD34D), // Yellow-300
                        glowColor = Color(0xFFFFE8A3),
                        isTransparent = false, // Keep vibrant color
                        textColorOverride = Color.Black, // Black text for light background
                        onClick = { /* TODO */ }
                    )
                    LiquidGlassCard(
                        modifier = Modifier.weight(1f),
                        title = "Places &\nServices",
                        buttonText = "View",
                        icon = Icons.Default.Place,
                        isEmphasized = false,
                        tintColor = Color(0xFF7DD3FC), // Sky-300
                        glowColor = Color(0xFFFFE8A3),
                        isTransparent = false, // Keep vibrant color
                        textColorOverride = Color.Black, // Black text for light background
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
                        tintColor = Color(0xFF27272A), // Zinc-800
                        glowColor = Color(0xFFFFE8A3),
                        isTransparent = false, // Keep vibrant color
                        textColorOverride = Color.White, // White text for dark background
                        onClick = { /* TODO */ }
                    )
                    LiquidGlassCard(
                        modifier = Modifier.weight(1f),
                        title = "Class\nSchedule",
                        buttonText = "View",
                        icon = Icons.Default.Info,
                        isEmphasized = true,
                        tintColor = Color(0xFF18181B), // Zinc-900
                        glowColor = Color(0xFFFFE8A3),
                        isTransparent = false, // Keep vibrant color
                        textColorOverride = Color.White, // White text for dark background
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
    glowColor: Color = Color(0xFFFFE8A3),
    isTransparent: Boolean = false, // NEW: for transparent glass effect
    textColorOverride: Color? = null, // NEW: override text color (white or black)
    onClick: () -> Unit
) {
    // Determine readable text color
    val textColor = textColorOverride ?: when (tintColor) {
        Color(0xFFD1D5DB) -> Color(0xFF1F2937) // dark text for light gray background
        else -> Color(0xFFF7F7F7) // slightly softened white for dark backgrounds
    }

    // Icon background (glass) depending on tint
    val iconBgColor = when (tintColor) {
        Color(0xFFD1D5DB) -> Color(0x20000000) // dark semi-transparent for light bg
        else -> Color(0x20FFFFFF) // light semi-transparent for dark bg
    }

    // Button fill (glass)
    val buttonFill = when (tintColor) {
        Color(0xFFD1D5DB) -> Color(0x40000000) // slightly darker glass for light tint
        else -> Color(0x30FFFFFF) // translucent white for dark tint
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
        // Outer container draws a subtle glow ring using a horizontal gradient stroke
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(2.dp) // tiny inner padding so border/glow has room
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            glowColor.copy(alpha = 0.18f),
                            glowColor.copy(alpha = 0.08f)
                        )
                    ),
                    shape = RoundedCornerShape(28.dp)
                )
                .padding(1.dp) // spacing between glow ring and main glass panel
        ) {
            // Main glass panel - conditional transparency
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = if (isTransparent) {
                                // Transparent glass effect
                                listOf(
                                    tintColor.copy(alpha = 0.08f),
                                    tintColor.copy(alpha = 0.05f)
                                )
                            } else {
                                // Normal vibrant colors
                                listOf(
                                    tintColor.copy(alpha = 0.85f),
                                    tintColor.copy(alpha = 0.75f)
                                )
                            }
                        ),
                        shape = RoundedCornerShape(28.dp)
                    )
                    // inner subtle border using glowColor but very transparent
                    .border(
                        width = 1.dp,
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                glowColor.copy(alpha = 0.24f),
                                glowColor.copy(alpha = 0.14f)
                            )
                        ),
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

                        // Icon with glassmorphic background and golden glow when emphasized
                        Box(
                            modifier = Modifier
                                .size(48.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            // If emphasized, tint the icon with glowColor slightly; otherwise use textColor
                            Icon(
                                imageVector = icon,
                                contentDescription = title,
                                tint = if (isEmphasized) glowColor.copy(alpha = 0.95f) else textColor.copy(alpha = 0.92f),
                                modifier = Modifier.size(26.dp)
                            )
                        }
                    }

                    // Glassmorphic Button with golden glow outline
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .clip(RoundedCornerShape(23.dp))
                            .background(color = buttonFill, shape = RoundedCornerShape(23.dp))
                            // overlay thin glow stroke around button using glowColor
                            .border(
                                width = 1.dp,
                                brush = Brush.horizontalGradient(
                                    colors = listOf(
                                        glowColor.copy(alpha = 0.9f),
                                        glowColor.copy(alpha = 0.6f)
                                    )
                                ),
                                shape = RoundedCornerShape(23.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = buttonText,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 15.sp,
                            color = textColor.copy(alpha = 0.95f)
                        )
                    }
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
                            tintColor.copy(alpha = 0.9f),
                            tintColor.copy(alpha = 0.8f)
                        )
                    ),
                    shape = RoundedCornerShape(24.dp)
                )
                .border(
                    width = 1.dp,
                    color = tintColor.copy(alpha = 0.5f),
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

    val outerGlowBrush = Brush.horizontalGradient(
        colors = listOf(
            Color(0xFFF7EBAF), // Left golden glow
            Color(0xFFE8A565)  // Right orange glow
        )
    )

    val innerBackgroundBrush = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF232428), // Inner gradient top
            Color(0xFF18191C)  // Inner gradient bottom
        )
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp)
            .padding(horizontal = 16.dp)
            .background(
                brush = outerGlowBrush,
                shape = RoundedCornerShape(35.dp)
            )
            .padding(1.5.dp) // Glow border thickness
            .background(
                brush = innerBackgroundBrush,
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
            BottomNavItem(Icons.Default.Home, isSelected = true)   // Active
            BottomNavItem(Icons.Default.Search, isSelected = false)
            BottomNavItem(Icons.Default.Info, isSelected = false)
            BottomNavItem(Icons.Default.Notifications, isSelected = false)
            BottomNavItem(Icons.Default.Person, isSelected = false)
        }
    }
}

@Composable
fun BottomNavItem(icon: ImageVector, isSelected: Boolean) {

    val iconColor = if (isSelected) {
        Color(0xFFFFE48F) // Active glowing yellow
    } else {
        Color(0xFFBFBFBF) // Inactive gray
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconColor,
            modifier = Modifier.size(26.dp)
        )

        if (isSelected) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .background(
                        color = Color(0xFFFCE9B7), // Indicator dot color
                        shape = CircleShape
                    )
            )
        }
    }
}

