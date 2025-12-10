package com.example.japuraroutef.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.EventNote
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.ui.draw.clip
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.japuraroutef.R
import com.example.japuraroutef.viewmodel.HomeViewModel
import com.example.japuraroutef.viewmodel.ClassInfo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    onNavigateToMap: () -> Unit = {},
    onNavigateToGrades: () -> Unit = {},
    onNavigateToSchedule: () -> Unit = {},
    onLogout: () -> Unit = {},
    viewModel: HomeViewModel = viewModel()
) {
    val colorScheme = MaterialTheme.colorScheme
    val classInfo = viewModel.currentClassInfo.value
    val greeting = viewModel.getGreeting()
    val userName = viewModel.userName.value
    val tiles = remember {
        listOf(
            HomeActionTile(
                title = "Class Schedule",
                icon = Icons.AutoMirrored.Filled.EventNote,
                background = colorScheme.secondaryContainer,
                textColor = colorScheme.onSecondaryContainer,
                iconTint = colorScheme.onSecondaryContainer,
                iconBackground = com.example.japuraroutef.ui.theme.SurfaceContainerHighestDark,
                span = 2,
                onClick = onNavigateToSchedule
            ),
            HomeActionTile(
                title = "Grades",
                icon = Icons.Default.Leaderboard,
                background = com.example.japuraroutef.ui.theme.SurfaceContainerDark,
                textColor = com.example.japuraroutef.ui.theme.OnSurfaceDark,
                iconTint = com.example.japuraroutef.ui.theme.OnSurfaceVariantDark,
                iconBackground = com.example.japuraroutef.ui.theme.SurfaceContainerHighestDark,
                borderColor = com.example.japuraroutef.ui.theme.OutlineDark,
                onClick = onNavigateToGrades
            ),
            HomeActionTile(
                title = "Campus Map",
                icon = Icons.Default.LocationOn,
                background = com.example.japuraroutef.ui.theme.SurfaceContainerHighDark,
                textColor = com.example.japuraroutef.ui.theme.OnSurfaceDark,
                iconTint = colorScheme.primary,
                iconBackground = Color.Transparent,
                onClick = onNavigateToMap
            ),
            HomeActionTile(
                title = "Events",
                icon = Icons.Default.Celebration,
                background = com.example.japuraroutef.ui.theme.SurfaceContainerHighDark,
                textColor = com.example.japuraroutef.ui.theme.OnSurfaceDark,
                iconTint = colorScheme.primary,
                iconBackground = Color.Transparent
            ),
            HomeActionTile(
                title = "Notices",
                icon = Icons.Default.Notifications,
                background = com.example.japuraroutef.ui.theme.SurfaceContainerHighDark,
                textColor = com.example.japuraroutef.ui.theme.OnSurfaceDark,
                iconTint = colorScheme.primary,
                iconBackground = Color.Transparent
            ),
            HomeActionTile(
                title = "Booking",
                icon = Icons.Default.Book,
                background = com.example.japuraroutef.ui.theme.SurfaceContainerHighDark,
                textColor = com.example.japuraroutef.ui.theme.OnSurfaceDark,
                iconTint = colorScheme.primary,
                iconBackground = Color.Transparent
            ),
            HomeActionTile(
                title = "Transport",
                icon = Icons.Default.DirectionsBus,
                background = com.example.japuraroutef.ui.theme.SurfaceContainerHighDark,
                textColor = com.example.japuraroutef.ui.theme.OnSurfaceDark,
                iconTint = colorScheme.primary,
                iconBackground = Color.Transparent
            ),
            HomeActionTile(
                title = "Services",
                icon = Icons.Default.Build,
                background = com.example.japuraroutef.ui.theme.SurfaceContainerHighDark,
                textColor = com.example.japuraroutef.ui.theme.OnSurfaceDark,
                iconTint = colorScheme.primary,
                iconBackground = Color.Transparent
            )
        )
    }

    Scaffold(
        modifier = modifier,
        bottomBar = { HomeBottomBar() },
        containerColor = colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            HomeTopAppBar(onLogout = onLogout)

            LazyVerticalGrid(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                columns = GridCells.Fixed(3),
                horizontalArrangement = Arrangement.spacedBy(16.dp), // gap-4
                verticalArrangement = Arrangement.spacedBy(24.dp), // space-y-6
                contentPadding = PaddingValues(top = 4.dp, bottom = 64.dp) // Space for 3-button navigation bar
            ) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                ImageCarousel()
            }

            item(span = { GridItemSpan(maxLineSpan) }) {
                HomeGreetingCard(
                    greeting = greeting,
                    userName = userName,
                    classInfo = classInfo
                )
            }

            item(span = { GridItemSpan(maxLineSpan) }) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp) // gap-4
                ) {
                    HomeFeatureTile(
                        tile = tiles[0],  // Class Schedule (col-span-3)
                        modifier = Modifier.weight(3f)
                    )
                    HomeFeatureTile(
                        tile = tiles[1],  // Grades (col-span-2)
                        modifier = Modifier.weight(2f)
                    )
                }
            }

            items(
                items = tiles.drop(2),
                span = { GridItemSpan(it.span) }
            ) { tile ->
                HomeFeatureTile(tile)
            }
        }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeTopAppBar(onLogout: () -> Unit = {}) {
    val colors = MaterialTheme.colorScheme
    Surface(
        color = Color.Transparent,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(72.dp)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Logo and Title
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_fotgo_logo),
                    contentDescription = "FOTGo Logo",
                    modifier = Modifier.size(48.dp)
                )
                Text(
                    text = "FOTGo",
                    style = MaterialTheme.typography.titleLarge,
                    color = colors.onSurface,
                    fontWeight = FontWeight.Bold
                )
            }

            // Actions
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = CircleShape,
                    color = colors.surfaceVariant,
                    tonalElevation = 4.dp
                ) {
                    IconButton(onClick = { }) {
                        Icon(Icons.Default.Search, contentDescription = "Search", tint = colors.onSurface)
                    }
                }
                Surface(
                    shape = CircleShape,
                    color = colors.surfaceVariant,
                    tonalElevation = 4.dp
                ) {
                    IconButton(onClick = onLogout) {
                        Icon(Icons.Default.AccountCircle, contentDescription = "Logout", tint = colors.onSurface)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ImageCarousel() {
    val images = remember {
        listOf(
            R.drawable.gemini_generated_image_hxfk22hxfk22hxfk,
            R.drawable.gemini_generated_image_hxfk22hxfk22hxfk,
            R.drawable.gemini_generated_image_hxfk22hxfk22hxfk
        )
    }
    val pagerState = rememberPagerState(pageCount = { images.size })

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxWidth()
        ) { page ->
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = com.example.japuraroutef.ui.theme.SurfaceContainerHighDark,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
            ) {
                Image(
                    painter = painterResource(id = images[page]),
                    contentDescription = "Event ${page + 1}",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
        }

        Spacer(Modifier.height(12.dp))

        // Pager indicator
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(images.size) { index ->
                Box(
                    modifier = Modifier
                        .size(if (pagerState.currentPage == index) 24.dp else 8.dp, 8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(
                            if (pagerState.currentPage == index)
                                com.example.japuraroutef.ui.theme.Primary
                            else
                                com.example.japuraroutef.ui.theme.OutlineDark
                        )
                )
                if (index < images.size - 1) {
                    Spacer(Modifier.width(8.dp))
                }
            }
        }
    }
}

@Composable
private fun HomeGreetingCard(
    greeting: String,
    userName: String?,
    classInfo: ClassInfo?
) {
    val gradient = Brush.linearGradient(
        colors = listOf(
            com.example.japuraroutef.ui.theme.DynamicEveningStart,
            com.example.japuraroutef.ui.theme.DynamicEveningEnd
        )
    )

    // Determine icon based on time of day
    val greetingIcon = when {
        greeting.contains("Morning") -> Icons.Default.WbSunny
        greeting.contains("Afternoon") -> Icons.Default.WbTwilight
        greeting.contains("Evening") -> Icons.Default.NightsStay
        else -> Icons.Default.DarkMode
    }

    // Determine class message and icon
    val (classMessage, classIcon) = when (classInfo) {
        is ClassInfo.NextClass -> {
            val message = "Your next class, ${classInfo.moduleName}, is in ${classInfo.location} at ${classInfo.startTime}."
            Pair(message, Icons.Default.Schedule)
        }
        is ClassInfo.InClass -> {
            val message = "You're currently in ${classInfo.moduleName} at ${classInfo.location}. Class ends at ${classInfo.endTime}."
            Pair(message, Icons.Default.School)
        }
        is ClassInfo.NoClassToday -> {
            Pair("No classes scheduled for today. Enjoy your free time!", Icons.Default.FreeBreakfast)
        }
        is ClassInfo.ClassesFinished -> {
            Pair("All classes for today are finished. Great work!", Icons.Default.CheckCircle)
        }
        null -> {
            Pair("Loading your schedule...", Icons.Default.HourglassEmpty)
        }
    }

    Surface(
        shape = RoundedCornerShape(28.dp), // extra-large radius
        color = Color.Transparent,
        shadowElevation = 24.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .background(gradient)
                .padding(24.dp) // p-6 = 24px
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.Top
            ) {
                // Time-of-day Icon
                Icon(
                    imageVector = greetingIcon,
                    contentDescription = greeting,
                    tint = com.example.japuraroutef.ui.theme.Primary,
                    modifier = Modifier.size(48.dp)
                )

                Column {
                    // Greeting with optional user name
                    Text(
                        text = if (userName != null) "$greeting, $userName!" else "$greeting!",
                        color = Color.White, // white for better contrast
                        fontSize = 30.sp, // text-3xl
                        fontWeight = FontWeight.Normal
                    )

                    Spacer(Modifier.height(12.dp)) // mt-2

                    // Class information with icon
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = classIcon,
                            contentDescription = null,
                            tint = com.example.japuraroutef.ui.theme.Primary,
                            modifier = Modifier.size(20.dp)
                        )

                        Text(
                            text = classMessage,
                            color = com.example.japuraroutef.ui.theme.OnSurfaceVariantDark,
                            fontSize = 14.sp, // text-sm
                            lineHeight = 22.sp // leading-relaxed
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun HomeFeatureTile(tile: HomeActionTile, modifier: Modifier = Modifier ) {
    Surface(
        shape = RoundedCornerShape(16.dp), // large radius for all tiles
        color = tile.background,
        tonalElevation = 0.dp,
        border = tile.borderColor?.let { BorderStroke(1.dp, it) },
        modifier = modifier
            .then(
                if (tile.span == 2 || tile.iconBackground != Color.Transparent) {
                    Modifier.height(128.dp) // h-32 for first row
                } else {
                    Modifier.aspectRatio(1f) // aspect-square for 3-column tiles
                }
            )
            .fillMaxWidth()
            .clickable(onClick = tile.onClick)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(if (tile.span == 2 || tile.iconBackground != Color.Transparent) 16.dp else 12.dp),
            verticalArrangement = if (tile.iconBackground != Color.Transparent) Arrangement.SpaceBetween else Arrangement.Center,
            horizontalAlignment = if (tile.iconBackground != Color.Transparent) Alignment.Start else Alignment.CenterHorizontally
        ) {
            if (tile.iconBackground != Color.Transparent) {
                Box(
                    modifier = Modifier
                        .size(48.dp) // w-12 h-12
                        .background(tile.iconBackground, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = tile.icon,
                        contentDescription = tile.title,
                        tint = tile.iconTint,
                        modifier = Modifier.size(24.dp)
                    )
                }
            } else {
                Icon(
                    imageVector = tile.icon,
                    contentDescription = tile.title,
                    tint = tile.iconTint,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(Modifier.height(8.dp)) // gap-2
            }
            Text(
                text = tile.title,
                style = if (tile.span == 2 || tile.iconBackground != Color.Transparent) {
                    MaterialTheme.typography.titleMedium.copy(fontSize = 16.sp, fontWeight = FontWeight.Medium)
                } else {
                    MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp, fontWeight = FontWeight.Medium)
                },
                color = tile.textColor,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                textAlign = if (tile.iconBackground != Color.Transparent) androidx.compose.ui.text.style.TextAlign.Start else androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}

@Composable
fun HomeBottomNavigationBar(
    selectedTab: Int = 0,
    onHomeClick: () -> Unit = {},
    onExploreClick: () -> Unit = {},
    onHistoryClick: () -> Unit = {},
    onAlertsClick: () -> Unit = {}
) {
    NavigationBar(
        containerColor = com.example.japuraroutef.ui.theme.SurfaceContainerHighDark,
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding() // Add 3-button navigation padding
    ) {
        NavigationBarItem(
            selected = selectedTab == 0,
            onClick = onHomeClick,
            icon = { Icon(Icons.Default.Home, contentDescription = null) },
            label = { Text("Home") }
        )
        NavigationBarItem(
            selected = selectedTab == 1,
            onClick = onExploreClick,
            icon = { Icon(Icons.Default.Explore, contentDescription = null) },
            label = { Text("Explore") }
        )
        NavigationBarItem(
            selected = selectedTab == 2,
            onClick = onHistoryClick,
            icon = { Icon(Icons.Default.History, contentDescription = null) },
            label = { Text("History") }
        )
        NavigationBarItem(
            selected = selectedTab == 3,
            onClick = onAlertsClick,
            icon = { Icon(Icons.Default.Notifications, contentDescription = null) },
            label = { Text("Alerts") }
        )
    }
}

@Composable
private fun HomeBottomBar() {
    HomeBottomNavigationBar(
        selectedTab = 0,
        onHomeClick = { /* Already on Home */ }
    )
}

private data class HomeActionTile(
    val title: String,
    val icon: ImageVector,
    val background: Color,
    val textColor: Color,
    val iconTint: Color,
    val iconBackground: Color,
    val borderColor: Color? = null,
    val span: Int = 1,
    val onClick: () -> Unit = {}
)
