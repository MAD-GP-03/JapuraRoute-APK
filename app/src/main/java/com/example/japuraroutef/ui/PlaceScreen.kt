package com.example.japuraroutef.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBackIos
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.japuraroutef.model.PlaceCategory
import com.example.japuraroutef.model.PlaceResponseDto
import com.example.japuraroutef.viewmodel.PlaceViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaceScreen(
    onNavigateBack: () -> Unit = {},
    onPlaceClick: (String) -> Unit = {},
    viewModel: PlaceViewModel = viewModel()
) {
    val uiState by viewModel.placesUiState.collectAsState()

    // Show error as snackbar
    LaunchedEffect(uiState.error) {
        if (uiState.error != null) {
            // Handle error display if needed
            viewModel.clearError()
        }
    }

    Scaffold(
        topBar = {
            PlaceScreenTopBar(
                onNavigateBack = onNavigateBack,
                searchQuery = uiState.searchQuery,
                onSearchQueryChange = { viewModel.updateSearchQuery(it) },
                selectedCategory = uiState.selectedCategory,
                onCategorySelected = { viewModel.updateSelectedCategory(it) }
            )
        },
        bottomBar = {
            HomeBottomNavigationBar(
                selectedTab = 1, // Explore tab
                onHomeClick = onNavigateBack
            )
        },
        containerColor = com.example.japuraroutef.ui.theme.SurfaceDark
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = com.example.japuraroutef.ui.theme.Primary
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(vertical = 16.dp)
                ) {
                    items(uiState.filteredPlaces) { place ->
                        PlaceCard(
                            place = place,
                            onClick = { place.id?.let { onPlaceClick(it) } }
                        )
                    }

                    // Empty state
                    if (uiState.filteredPlaces.isEmpty() && !uiState.isLoading) {
                        item {
                            EmptyPlacesMessage()
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PlaceScreenTopBar(
    onNavigateBack: () -> Unit,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    selectedCategory: PlaceCategory,
    onCategorySelected: (PlaceCategory) -> Unit
) {
    Surface(
        color = com.example.japuraroutef.ui.theme.SurfaceDark,
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(top = 16.dp, bottom = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBackIos,
                            contentDescription = "Back",
                            tint = com.example.japuraroutef.ui.theme.OnSurfaceDark
                        )
                    }
                    Text(
                        text = "Places & Services",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Normal,
                        color = com.example.japuraroutef.ui.theme.OnSurfaceDark
                    )
                }
            }

            Surface(
                shape = RoundedCornerShape(24.dp),
                color = com.example.japuraroutef.ui.theme.SurfaceContainerHighDark,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = com.example.japuraroutef.ui.theme.OnSurfaceVariantDark,
                        modifier = Modifier.size(24.dp)
                    )

                    BasicTextField(
                        value = searchQuery,
                        onValueChange = onSearchQueryChange,
                        modifier = Modifier.weight(1f),
                        textStyle = MaterialTheme.typography.bodyLarge.copy(
                            color = com.example.japuraroutef.ui.theme.OnSurfaceDark
                        ),
                        singleLine = true
                    ) { innerTextField ->
                        Box {
                            if (searchQuery.isEmpty()) {
                                Text(
                                    text = "Search campus...",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = com.example.japuraroutef.ui.theme.OnSurfaceVariantDark
                                )
                            }
                            innerTextField()
                        }
                    }

                    Icon(
                        imageVector = Icons.Default.Mic,
                        contentDescription = "Voice search",
                        tint = com.example.japuraroutef.ui.theme.OnSurfaceVariantDark,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            // Category filter chips
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(bottom = 8.dp)
            ) {
                items(PlaceCategory.entries) { category ->
                    CategoryChip(
                        category = category,
                        isSelected = selectedCategory == category,
                        onClick = { onCategorySelected(category) }
                    )
                }
            }
        }
    }
}

@Composable
private fun CategoryChip(
    category: PlaceCategory,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = if (isSelected)
            com.example.japuraroutef.ui.theme.SecondaryContainerDark
        else
            com.example.japuraroutef.ui.theme.SurfaceContainerDark,
        border = BorderStroke(1.dp, com.example.japuraroutef.ui.theme.OutlineDark),
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Text(
            text = category.displayName,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Medium,
            color = if (isSelected)
                com.example.japuraroutef.ui.theme.OnSecondaryContainerDark
            else
                com.example.japuraroutef.ui.theme.OnSurfaceVariantDark
        )
    }
}

@Composable
private fun PlaceCard(
    place: PlaceResponseDto,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = com.example.japuraroutef.ui.theme.SurfaceContainerDark,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shadowElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Place image
            if (place.images.isNotEmpty()) {
                AsyncImage(
                    model = place.images.first(),
                    contentDescription = place.name,
                    modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(16.dp)),
                    contentScale = ContentScale.Crop
                )
            } else {
                // Fallback icon if no image
                Surface(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(16.dp)),
                    color = com.example.japuraroutef.ui.theme.SurfaceContainerHighestDark
                ) {
                    Icon(
                        imageVector = Icons.Default.Place,
                        contentDescription = place.name,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(12.dp),
                        tint = com.example.japuraroutef.ui.theme.Primary
                    )
                }
            }

            // Place info
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = place.name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    color = com.example.japuraroutef.ui.theme.OnSurfaceDark,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = place.shortDescription ?: place.description ?: "",
                    style = MaterialTheme.typography.bodyMedium,
                    color = com.example.japuraroutef.ui.theme.OnSurfaceVariantDark,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Arrow icon
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                contentDescription = null,
                tint = com.example.japuraroutef.ui.theme.OnSurfaceVariantDark,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
private fun EmptyPlacesMessage() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Icon(
            imageVector = Icons.Default.SearchOff,
            contentDescription = null,
            tint = com.example.japuraroutef.ui.theme.OnSurfaceVariantDark,
            modifier = Modifier.size(64.dp)
        )
        Text(
            text = "No places found",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Medium,
            color = com.example.japuraroutef.ui.theme.OnSurfaceDark
        )
        Text(
            text = "Try adjusting your filters or search",
            style = MaterialTheme.typography.bodyMedium,
            color = com.example.japuraroutef.ui.theme.OnSurfaceVariantDark
        )
    }
}

