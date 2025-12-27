package com.example.japuraroutef.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.japuraroutef.model.OperatingHourDto
import com.example.japuraroutef.model.PlaceResponseDto
import com.example.japuraroutef.viewmodel.PlaceViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaceDetailScreen(
    placeId: String,
    onNavigateBack: () -> Unit = {},
    viewModel: PlaceViewModel = viewModel()
) {
    val uiState by viewModel.placeDetailUiState.collectAsState()

    LaunchedEffect(placeId) {
        viewModel.loadPlaceById(placeId)
    }

    DisposableEffect(Unit) {
        onDispose {
            viewModel.clearPlaceDetail()
        }
    }

    Scaffold(
        containerColor = com.example.japuraroutef.ui.theme.SurfaceDark
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = com.example.japuraroutef.ui.theme.Primary
                    )
                }
                uiState.place != null -> {
                    PlaceDetailContent(
                        place = uiState.place!!,
                        onNavigateBack = onNavigateBack
                    )
                }
                uiState.error != null -> {
                    ErrorMessage(
                        message = uiState.error ?: "An error occurred",
                        onRetry = { viewModel.loadPlaceById(placeId) },
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }
    }
}

@Composable
private fun PlaceDetailContent(
    place: PlaceResponseDto,
    onNavigateBack: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            // Hero Image with overlay header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(320.dp)
            ) {
                // Place image
                if (place.images.isNotEmpty()) {
                    AsyncImage(
                        model = place.images.first(),
                        contentDescription = place.name,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(com.example.japuraroutef.ui.theme.SurfaceContainerHighestDark)
                    )
                }

                // Gradient overlay
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    Color.Black.copy(alpha = 0.6f),
                                    Color.Transparent,
                                    com.example.japuraroutef.ui.theme.SurfaceDark
                                )
                            )
                        )
                )

                // Header buttons
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(com.example.japuraroutef.ui.theme.SurfaceDark.copy(alpha = 0.4f))
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = com.example.japuraroutef.ui.theme.OnSurfaceDark
                        )
                    }

                    IconButton(
                        onClick = { /* TODO: Handle favorite */ },
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(com.example.japuraroutef.ui.theme.SurfaceDark.copy(alpha = 0.4f))
                    ) {
                        Icon(
                            imageVector = Icons.Default.FavoriteBorder,
                            contentDescription = "Favorite",
                            tint = com.example.japuraroutef.ui.theme.OnSurfaceDark
                        )
                    }
                }
            }
        }

        item {
            // Content section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .offset(y = (-40).dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Title and status
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = place.name,
                        style = MaterialTheme.typography.displaySmall,
                        fontWeight = FontWeight.Normal,
                        color = com.example.japuraroutef.ui.theme.OnSurfaceDark
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Open/Closed status badge
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = com.example.japuraroutef.ui.theme.Primary.copy(alpha = 0.1f),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                com.example.japuraroutef.ui.theme.Primary.copy(alpha = 0.2f)
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(com.example.japuraroutef.ui.theme.Primary)
                                )
                                Text(
                                    text = "Open Now",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Medium,
                                    color = com.example.japuraroutef.ui.theme.Primary
                                )
                            }
                        }

                        // Category/Tags
                        if (place.tags.isNotEmpty()) {
                            Text(
                                text = place.tags.first(),
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Medium,
                                color = com.example.japuraroutef.ui.theme.OnSurfaceVariantDark
                            )
                        }

                        Text(
                            text = "•",
                            color = com.example.japuraroutef.ui.theme.OnSurfaceVariantDark
                        )

                        // Rating
                        if (place.rating != null) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null,
                                    tint = com.example.japuraroutef.ui.theme.Primary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = String.format("%.1f", place.rating),
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Medium,
                                    color = com.example.japuraroutef.ui.theme.Primary
                                )
                            }
                        }
                    }
                }

                // Action buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    ActionButton(
                        icon = Icons.Default.Directions,
                        label = "Route",
                        onClick = { /* TODO */ },
                        isPrimary = true
                    )
                    ActionButton(
                        icon = Icons.Default.Call,
                        label = "Call",
                        onClick = { /* TODO */ }
                    )
                    ActionButton(
                        icon = Icons.Default.Language,
                        label = "Website",
                        onClick = { /* TODO */ }
                    )
                    ActionButton(
                        icon = Icons.Default.Share,
                        label = "Share",
                        onClick = { /* TODO */ }
                    )
                }

                HorizontalDivider(
                    color = com.example.japuraroutef.ui.theme.OutlineDark.copy(alpha = 0.2f)
                )

                // About section
                if (!place.description.isNullOrEmpty()) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "About",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Normal,
                            color = com.example.japuraroutef.ui.theme.OnSurfaceDark
                        )
                        Text(
                            text = place.description,
                            style = MaterialTheme.typography.bodyLarge,
                            color = com.example.japuraroutef.ui.theme.OnSurfaceVariantDark,
                            lineHeight = MaterialTheme.typography.bodyLarge.lineHeight.times(1.5f)
                        )
                    }
                }

                // Operating Hours
                if (place.operatingHours.isNotEmpty()) {
                    OperatingHoursSection(operatingHours = place.operatingHours)
                }

                // Location section
                LocationSection(place = place)

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
private fun ActionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit,
    isPrimary: Boolean = false
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.width(56.dp)
    ) {
        Surface(
            onClick = onClick,
            shape = CircleShape,
            color = if (isPrimary)
                com.example.japuraroutef.ui.theme.PrimaryContainerDark
            else
                com.example.japuraroutef.ui.theme.SurfaceContainerHighDark,
            border = if (!isPrimary)
                androidx.compose.foundation.BorderStroke(
                    1.dp,
                    com.example.japuraroutef.ui.theme.OutlineDark.copy(alpha = 0.2f)
                )
            else null,
            modifier = Modifier.size(56.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = if (isPrimary)
                        com.example.japuraroutef.ui.theme.OnPrimaryContainerDark
                    else
                        com.example.japuraroutef.ui.theme.Primary,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Medium,
            color = com.example.japuraroutef.ui.theme.OnSurfaceVariantDark
        )
    }
}

@Composable
private fun OperatingHoursSection(operatingHours: List<OperatingHourDto>) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Operating Hours",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Normal,
            color = com.example.japuraroutef.ui.theme.OnSurfaceDark
        )

        Surface(
            shape = RoundedCornerShape(16.dp),
            color = com.example.japuraroutef.ui.theme.SurfaceContainerDark,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                operatingHours.forEachIndexed { index, hour ->
                    if (index > 0) {
                        HorizontalDivider(
                            color = com.example.japuraroutef.ui.theme.OutlineDark.copy(alpha = 0.1f),
                            thickness = 1.dp
                        )
                    }
                    OperatingHourRow(hour)
                }
            }
        }
    }
}

@Composable
private fun OperatingHourRow(hour: OperatingHourDto) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = hour.day,
            style = MaterialTheme.typography.bodyMedium,
            color = com.example.japuraroutef.ui.theme.OnSurfaceVariantDark
        )

        val timeText = if (hour.startTime != null && hour.endTime != null) {
            "${hour.startTime} - ${hour.endTime}"
        } else if (hour.note != null) {
            hour.note
        } else {
            "24 Hours"
        }

        Text(
            text = timeText,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (hour.note != null) FontWeight.Bold else FontWeight.Medium,
            color = com.example.japuraroutef.ui.theme.OnSurfaceDark
        )
    }
}

@Composable
private fun LocationSection(place: PlaceResponseDto) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Location",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Normal,
            color = com.example.japuraroutef.ui.theme.OnSurfaceDark
        )

        // Map placeholder
        Surface(
            shape = RoundedCornerShape(28.dp),
            color = com.example.japuraroutef.ui.theme.SurfaceContainerHighDark,
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center
            ) {
                Button(
                    onClick = { /* TODO: Open map */ },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = com.example.japuraroutef.ui.theme.SecondaryContainerDark,
                        contentColor = com.example.japuraroutef.ui.theme.OnSecondaryContainerDark
                    ),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Map,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "View on Map",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        // Address info
        if (!place.address.isNullOrEmpty() || !place.location.isNullOrEmpty()) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.Top,
                modifier = Modifier.padding(top = 4.dp)
            ) {
                Surface(
                    shape = CircleShape,
                    color = com.example.japuraroutef.ui.theme.SurfaceContainerHighDark,
                    modifier = Modifier.size(40.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = com.example.japuraroutef.ui.theme.Primary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                Column {
                    Text(
                        text = place.location ?: place.name,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium,
                        color = com.example.japuraroutef.ui.theme.OnSurfaceDark
                    )
                    if (!place.address.isNullOrEmpty()) {
                        Text(
                            text = place.address,
                            style = MaterialTheme.typography.bodyMedium,
                            color = com.example.japuraroutef.ui.theme.OnSurfaceVariantDark
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ErrorMessage(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Icon(
            imageVector = Icons.Default.ErrorOutline,
            contentDescription = null,
            tint = com.example.japuraroutef.ui.theme.OnSurfaceVariantDark,
            modifier = Modifier.size(64.dp)
        )
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = com.example.japuraroutef.ui.theme.OnSurfaceVariantDark
        )
        Button(
            onClick = onRetry,
            colors = ButtonDefaults.buttonColors(
                containerColor = com.example.japuraroutef.ui.theme.PrimaryContainerDark,
                contentColor = com.example.japuraroutef.ui.theme.OnPrimaryContainerDark
            )
        ) {
            Text("Retry")
        }
    }
}

