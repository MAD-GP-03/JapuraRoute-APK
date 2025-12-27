package com.example.japuraroutef.ui

import android.Manifest
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.japuraroutef.R
import com.example.japuraroutef.model.PlaceCategory
import com.example.japuraroutef.model.PlaceResponseDto
import com.example.japuraroutef.viewmodel.PlaceViewModel
import com.mapbox.geojson.Point
import com.mapbox.geojson.LineString
import com.mapbox.maps.CameraBoundsOptions
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.mapbox.maps.extension.compose.style.MapStyle
import com.mapbox.maps.extension.compose.MapEffect
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.PolylineAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.CircleAnnotationOptions
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager
import com.mapbox.maps.plugin.annotation.generated.createPolylineAnnotationManager
import com.mapbox.maps.plugin.annotation.generated.createCircleAnnotationManager
import com.mapbox.maps.plugin.annotation.generated.PolylineAnnotationManager
import com.mapbox.maps.plugin.locationcomponent.location
import com.mapbox.maps.MapView
import com.mapbox.maps.plugin.animation.MapAnimationOptions
import com.mapbox.maps.plugin.animation.flyTo
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

data class DirectionsResponse(
    val routes: List<Route>
)

data class Route(
    val geometry: String,
    val distance: Double,
    val duration: Double
)

interface MapboxDirectionsService {
    @GET("directions/v5/mapbox/walking/{coordinates}")
    suspend fun getDirections(
        @Path("coordinates") coordinates: String,
        @Query("geometries") geometries: String = "polyline6",
        @Query("overview") overview: String = "full",
        @Query("access_token") accessToken: String
    ): DirectionsResponse
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    onNavigateBack: () -> Unit,
    onNavigateToPlaceDetail: (String) -> Unit = {},
    initialPlaceId: String? = null,
    viewModel: PlaceViewModel = viewModel()
) {
    val context = LocalContext.current
    val placesUiState by viewModel.placesUiState.collectAsState()

    var selectedPlace by remember { mutableStateOf<PlaceResponseDto?>(null) }
    var selectedCategory by remember { mutableStateOf(PlaceCategory.ALL) }
    var searchQuery by remember { mutableStateOf("") }
    var currentRoute by remember { mutableStateOf<Route?>(null) }
    var currentLocation by remember { mutableStateOf<Point?>(null) }
    var mapLoadError by remember { mutableStateOf<String?>(null) }
    var mapViewInstance by remember { mutableStateOf<MapView?>(null) }
    var polylineManager by remember { mutableStateOf<PolylineAnnotationManager?>(null) }


    val coroutineScope = rememberCoroutineScope()
    val accessToken = context.getString(R.string.mapbox_access_token)

    // Initialize Retrofit for Mapbox Directions API
    val directionsService = remember {
        Retrofit.Builder()
            .baseUrl("https://api.mapbox.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(MapboxDirectionsService::class.java)
    }

    // Location permission launcher
    val locationPermissionRequest = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                // Precise location access granted
            }
            permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                // Only approximate location access granted
            }
        }
    }

    LaunchedEffect(Unit) {
        // Load places when screen opens
        Log.d("MapScreen", "Calling viewModel.loadPlaces()")
        viewModel.loadPlaces()

        // Request location permissions
        locationPermissionRequest.launch(arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ))
    }

    // Handle initial place selection from PlaceDetailScreen navigation
    LaunchedEffect(initialPlaceId, placesUiState.places) {
        if (initialPlaceId != null && placesUiState.places.isNotEmpty()) {
            val place = placesUiState.places.find { it.id == initialPlaceId }
            if (place != null) {
                selectedPlace = place
                // Focus camera on the place
                place.latitude?.let { lat ->
                    place.longitude?.let { lng ->
                        mapViewInstance?.mapboxMap?.flyTo(
                            CameraOptions.Builder()
                                .center(Point.fromLngLat(lng, lat))
                                .zoom(18.0)
                                .pitch(60.0)
                                .bearing(-17.6)
                                .build(),
                            MapAnimationOptions.mapAnimationOptions {
                                duration(1500)
                            }
                        )
                    }
                }
            }
        }
    }

    // Log places state changes
    LaunchedEffect(placesUiState.places) {
        Log.d("MapScreen", "Places state changed: ${placesUiState.places.size} places, isLoading: ${placesUiState.isLoading}, error: ${placesUiState.error}")
        placesUiState.places.forEachIndexed { index, place ->
            Log.d("MapScreen", "Place $index: ${place.name}, lat: ${place.latitude}, lng: ${place.longitude}")
        }
    }

    // Cleanup when leaving screen
    DisposableEffect(Unit) {
        onDispose {
            // Clear any resources if needed
            polylineManager?.deleteAll()
            polylineManager = null
            currentRoute = null
        }
    }

    // Filter places based on category and search
    val filteredPlaces = remember(placesUiState.places, selectedCategory, searchQuery) {
        val filtered = placesUiState.places.filter { place ->
            val matchesCategory = selectedCategory == PlaceCategory.ALL ||
                place.tags.any { it.equals(selectedCategory.tag, ignoreCase = true) }

            val matchesSearch = searchQuery.isEmpty() ||
                place.name.contains(searchQuery, ignoreCase = true) ||
                place.description?.contains(searchQuery, ignoreCase = true) == true

            matchesCategory && matchesSearch
        }
        Log.d("MapScreen", "Filtered places: ${filtered.size} out of ${placesUiState.places.size} (category: ${selectedCategory.displayName}, search: '$searchQuery')")
        filtered
    }

    // Camera bounds for the map
    val cameraBoundsOptions = CameraBoundsOptions.Builder()
        .minZoom(15.0)
        .maxZoom(22.0)
        .build()

    Box(modifier = Modifier.fillMaxSize()) {
        // Show error if map failed to load, otherwise show map
        if (mapLoadError != null) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.padding(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ErrorOutline,
                        contentDescription = "Error",
                        tint = com.example.japuraroutef.ui.theme.OnSurfaceVariantDark,
                        modifier = Modifier.size(64.dp)
                    )
                    Text(
                        text = "Map failed to load",
                        style = MaterialTheme.typography.titleMedium,
                        color = com.example.japuraroutef.ui.theme.OnSurfaceDark
                    )
                    Text(
                        text = mapLoadError ?: "Please check your internet connection and Mapbox token",
                        style = MaterialTheme.typography.bodyMedium,
                        color = com.example.japuraroutef.ui.theme.OnSurfaceVariantDark
                    )
                    Button(onClick = onNavigateBack) {
                        Text("Go Back")
                    }
                }
            }
        } else {
            MapboxMap(
                Modifier.fillMaxSize(),
                compass = {
                    Compass( alignment = Alignment.CenterStart )
                },
                mapViewportState = rememberMapViewportState {
                    setCameraOptions {
                        zoom(17.5)
                        center(Point.fromLngLat(80.04168, 6.82314))
                        pitch(60.0)
                        bearing(-17.6)
                    }
                },
                style = {
                    MapStyle(style = "mapbox://styles/nuwankonara/cmifk82fz001y01qw6bsdhv86")
                }
            ) {
            MapEffect(filteredPlaces, selectedPlace, currentRoute) { mapView ->
                mapView.mapboxMap.setBounds(cameraBoundsOptions)

                mapViewInstance = mapView

                Log.d("MapScreen", "Filtered places count: ${filteredPlaces.size}")

                // Add markers for filtered places using Circle Annotations for guaranteed visibility
                val annotationApi = mapView.annotations

                // Use circle annotations for markers
                val circleAnnotationManager = annotationApi.createCircleAnnotationManager()
                circleAnnotationManager.deleteAll()

                // Use point annotations for text labels
                val pointAnnotationManager = annotationApi.createPointAnnotationManager()
                pointAnnotationManager.deleteAll()

                // Create or reuse polyline annotation manager for routes - single instance
                if (polylineManager == null) {
                    polylineManager = annotationApi.createPolylineAnnotationManager()
                }
                polylineManager?.deleteAll()

                // Create a map to track annotations and their places
                val annotationPlaceMap = mutableMapOf<String, PlaceResponseDto>()

                filteredPlaces.forEach { place ->
                    if (place.latitude != null && place.longitude != null) {
                        Log.d("MapScreen", "Adding marker for: ${place.name} at ${place.latitude}, ${place.longitude}")

                        try {
                            // Create a visible circle marker
                            val circleAnnotationOptions = CircleAnnotationOptions()
                                .withPoint(Point.fromLngLat(place.longitude, place.latitude))
                                .withCircleRadius(8.0)
                                .withCircleColor("#D1BCFF")
                                .withCircleStrokeWidth(2.0)
                                .withCircleStrokeColor("#FFFFFF")

                            val annotation = circleAnnotationManager.create(circleAnnotationOptions)

                            // Store place reference using annotation ID
                            annotationPlaceMap[annotation.id] = place

                            // Add text label for the place name
                            val pointAnnotationOptions = PointAnnotationOptions()
                                .withPoint(Point.fromLngLat(place.longitude, place.latitude))
                                .withTextField(place.name)
                                .withTextColor("#FFFFFF")
                                .withTextSize(12.0)
                                .withTextHaloColor("#000000")
                                .withTextHaloWidth(1.0)
                                .withTextOffset(listOf(0.0, -1.5))

                            pointAnnotationManager.create(pointAnnotationOptions)

                            Log.d("MapScreen", "Successfully added marker with ID: ${annotation.id}")
                        } catch (e: Exception) {
                            Log.e("MapScreen", "Error adding marker for ${place.name}: ${e.message}")
                        }
                    } else {
                        Log.w("MapScreen", "Place ${place.name} has no coordinates (lat: ${place.latitude}, lng: ${place.longitude})")
                    }
                }

                // Add click listener for markers
                circleAnnotationManager.addClickListener { annotation ->
                    Log.d("MapScreen", "Marker clicked with ID: ${annotation.id}")
                    val clickedPlace = annotationPlaceMap[annotation.id]
                    if (clickedPlace != null) {
                        Log.d("MapScreen", "Found place: ${clickedPlace.name}")
                        selectedPlace = clickedPlace
                    } else {
                        Log.w("MapScreen", "No place found for annotation ID: ${annotation.id}")
                    }
                    true
                }

                Log.d("MapScreen", "Total markers added: ${circleAnnotationManager.annotations.size}")
                Log.d("MapScreen", "Annotation place map size: ${annotationPlaceMap.size}")

                // Enable location component and get current location
                mapView.location.updateSettings {
                    enabled = true
                    pulsingEnabled = true
                }

                // Get current location safely
                try {
                    mapView.location.addOnIndicatorPositionChangedListener { point ->
                        currentLocation = point
                    }
                } catch (e: Exception) {
                    Log.e("MapScreen", "Error setting up location listener: ${e.message}")
                }

                // Draw route if available
                currentRoute?.let { route ->
                    try {
                        val points = LineString.fromPolyline(route.geometry, 6).coordinates()

                        val polylineAnnotationOptions = PolylineAnnotationOptions()
                            .withPoints(points)
                            .withLineColor("#1E88E5")  // Darker blue for better visibility
                            .withLineWidth(6.0)  // Slightly thicker for better visibility
                        polylineManager?.create(polylineAnnotationOptions)
                        Log.d("MapScreen", "Route drawn successfully")
                    } catch (e: Exception) {
                        Log.e("MapScreen", "Error drawing route: ${e.message}", e)
                    }
                } ?: run {
                    Log.d("MapScreen", "No route to draw - route cleared")
                }
            }
        }
        }

        // Top gradient overlay
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            com.example.japuraroutef.ui.theme.BackgroundDark.copy(alpha = 0.9f),
                            Color.Transparent
                        )
                    )
                )
        )

        // UI Overlay
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Top Section - Search and Filters
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Search Bar
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(28.dp),
                    color = com.example.japuraroutef.ui.theme.SurfaceContainerHighDark,
                    shadowElevation = 4.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        IconButton(onClick = onNavigateBack) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "Menu",
                                tint = com.example.japuraroutef.ui.theme.OnSurfaceVariantDark
                            )
                        }

                        androidx.compose.foundation.text.BasicTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            modifier = Modifier.weight(1f),
                            textStyle = MaterialTheme.typography.bodyLarge.copy(
                                color = com.example.japuraroutef.ui.theme.OnSurfaceDark
                            ),
                            singleLine = true,
                            decorationBox = { innerTextField ->
                                Box {
                                    if (searchQuery.isEmpty()) {
                                        Text(
                                            "Search University Places",
                                            style = MaterialTheme.typography.bodyLarge,
                                            color = com.example.japuraroutef.ui.theme.OnSurfaceVariantDark
                                        )
                                    }
                                    innerTextField()
                                }
                            }
                        )
                    }
                }

                // Category Filter Chips
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(horizontal = 4.dp)
                ) {
                    items(PlaceCategory.entries.filter { it != PlaceCategory.ALL }) { category ->
                        CategoryChipMap(
                            category = category,
                            isSelected = selectedCategory == category,
                            onClick = {
                                selectedCategory = if (selectedCategory == category) {
                                    PlaceCategory.ALL
                                } else {
                                    category
                                }
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Location Control Buttons
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 16.dp, bottom = if (selectedPlace != null || filteredPlaces.isNotEmpty()) 16.dp else 80.dp)
                    .navigationBarsPadding(),
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // My Location Button
                Surface(
                    onClick = {
                        currentLocation?.let { location ->
                            mapViewInstance?.mapboxMap?.flyTo(
                                CameraOptions.Builder()
                                    .center(location)
                                    .zoom(17.5)
                                    .pitch(60.0)
                                    .bearing(-17.6)
                                    .build(),
                                MapAnimationOptions.mapAnimationOptions {
                                    duration(1200)
                                }
                            )
                        }
                    },
                    modifier = Modifier.size(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    color = com.example.japuraroutef.ui.theme.SurfaceContainerHighDark,
                    shadowElevation = 8.dp,
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        com.example.japuraroutef.ui.theme.OutlineDark.copy(alpha = 0.2f)
                    )
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.MyLocation,
                            contentDescription = "My Location",
                            tint = com.example.japuraroutef.ui.theme.Primary
                        )
                    }
                }

                // Faculty Default Location Button
                Surface(
                    onClick = {
                        mapViewInstance?.mapboxMap?.flyTo(
                            CameraOptions.Builder()
                                .center(Point.fromLngLat(80.04168, 6.82314))
                                .zoom(17.5)
                                .pitch(60.0)
                                .bearing(-17.6)
                                .build(),
                            MapAnimationOptions.mapAnimationOptions {
                                duration(1200)
                            }
                        )
                    },
                    modifier = Modifier.size(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    color = com.example.japuraroutef.ui.theme.SurfaceContainerHighDark,
                    shadowElevation = 8.dp,
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        com.example.japuraroutef.ui.theme.OutlineDark.copy(alpha = 0.2f)
                    )
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.School,
                            contentDescription = "Faculty Location",
                            tint = com.example.japuraroutef.ui.theme.Primary
                        )
                    }
                }
            }



            // Bottom Sheet with Place Details or Simple Place List
            if (selectedPlace != null) {
                PlaceDetailBottomSheet(
                    place = selectedPlace!!,
                    onClose = {
                        selectedPlace = null
                        // Don't clear route - keep it visible after closing
                    },
                    onGetDirections = {
                        coroutineScope.launch {
                            try {
                                // Clear previous route first and wait for UI to update
                                currentRoute = null
                                kotlinx.coroutines.delay(100) // Small delay to ensure MapEffect clears the old route

                                val origin = currentLocation ?: Point.fromLngLat(80.04168, 6.82314)
                                val destination = Point.fromLngLat(
                                    selectedPlace!!.longitude ?: 0.0,
                                    selectedPlace!!.latitude ?: 0.0
                                )
                                val coords = "${origin.longitude()},${origin.latitude()};${destination.longitude()},${destination.latitude()}"
                                val response = directionsService.getDirections(coords, accessToken = accessToken)
                                if (response.routes.isNotEmpty()) {
                                    currentRoute = response.routes[0]
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    },
                    onViewMore = {
                        selectedPlace?.id?.let { placeId ->
                            onNavigateToPlaceDetail(placeId)
                        }
                    }
                )
            } else if (filteredPlaces.isNotEmpty()) {
                // Simple fixed place list (not collapsible)
                SimplePlaceList(
                    places = filteredPlaces.take(5),
                    onPlaceClick = { place ->
                        // Don't clear route when selecting a place - only clear when clicking Get Directions
                        selectedPlace = place
                    }
                )
            }
        }
    }
}

@Composable
private fun CategoryChipMap(
    category: PlaceCategory,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val icon = when (category) {
        PlaceCategory.STUDY -> Icons.Default.School
        PlaceCategory.FOOD -> Icons.Default.Restaurant
        PlaceCategory.ADMIN -> Icons.Default.Business
        PlaceCategory.HEALTH -> Icons.Default.LocalHospital
        PlaceCategory.SPORTS -> Icons.Default.FitnessCenter
        else -> Icons.Default.Place
    }

    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(8.dp),
        color = if (isSelected)
            com.example.japuraroutef.ui.theme.SecondaryContainerDark
        else
            com.example.japuraroutef.ui.theme.SurfaceContainerHighestDark,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isSelected) Color.Transparent else com.example.japuraroutef.ui.theme.OutlineDark.copy(alpha = 0.3f)
        ),
        modifier = Modifier.height(32.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
                tint = if (isSelected)
                    com.example.japuraroutef.ui.theme.OnSecondaryContainerDark
                else
                    com.example.japuraroutef.ui.theme.OnSurfaceVariantDark
            )
            Text(
                text = category.displayName,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Medium,
                color = if (isSelected)
                    com.example.japuraroutef.ui.theme.OnSecondaryContainerDark
                else
                    com.example.japuraroutef.ui.theme.OnSurfaceVariantDark
            )
        }
    }
}

@Composable
private fun SimplePlaceList(
    places: List<PlaceResponseDto>,
    onPlaceClick: (PlaceResponseDto) -> Unit
) {
    if (places.isEmpty()) return

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding(),
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        color = com.example.japuraroutef.ui.theme.SurfaceContainerDark.copy(alpha = 0.95f),
        shadowElevation = 16.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, bottom = 16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Nearby Places",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium,
                        color = com.example.japuraroutef.ui.theme.OnSurfaceDark
                    )
                    Text(
                        text = "${places.size} places found",
                        style = MaterialTheme.typography.bodySmall,
                        color = com.example.japuraroutef.ui.theme.OnSurfaceVariantDark
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Horizontal scrolling place cards
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(horizontal = 16.dp)
            ) {
                items(places) { place ->
                    CompactPlaceCard(
                        place = place,
                        onClick = { onPlaceClick(place) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun CollapsiblePlaceList(
    places: List<PlaceResponseDto>,
    isExpanded: Boolean,
    onToggleExpand: () -> Unit,
    onPlaceClick: (PlaceResponseDto) -> Unit
) {
    if (places.isEmpty()) return

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding(),
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        color = com.example.japuraroutef.ui.theme.SurfaceContainerDark.copy(alpha = 0.95f),
        shadowElevation = 16.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, bottom = 16.dp)
        ) {
            // Header with toggle button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Nearby Places",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium,
                        color = com.example.japuraroutef.ui.theme.OnSurfaceDark
                    )
                    Text(
                        text = "${places.size} places found",
                        style = MaterialTheme.typography.bodySmall,
                        color = com.example.japuraroutef.ui.theme.OnSurfaceVariantDark
                    )
                }

                IconButton(
                    onClick = onToggleExpand,
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.ExpandMore else Icons.Default.ExpandLess,
                        contentDescription = if (isExpanded) "Hide" else "Show",
                        tint = com.example.japuraroutef.ui.theme.Primary
                    )
                }
            }

            // Expandable list
            AnimatedVisibility(
                visible = isExpanded,
                enter = slideInVertically(initialOffsetY = { -it }),
                exit = slideOutVertically(targetOffsetY = { -it })
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 400.dp)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(places) { place ->
                        PlacePreviewCard(
                            place = place,
                            onClick = { onPlaceClick(place) }
                        )
                    }
                }
            }

            // Show compact preview when collapsed
            if (!isExpanded && places.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp)
                ) {
                    items(places.take(3)) { place ->
                        CompactPlaceCard(
                            place = place,
                            onClick = { onPlaceClick(place) }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun CompactPlaceCard(
    place: PlaceResponseDto,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        color = com.example.japuraroutef.ui.theme.SurfaceContainerHighDark,
        modifier = Modifier.width(140.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (place.images.isNotEmpty()) {
                AsyncImage(
                    model = place.images.first(),
                    contentDescription = place.name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(com.example.japuraroutef.ui.theme.SurfaceContainerHighestDark),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Place,
                        contentDescription = null,
                        tint = com.example.japuraroutef.ui.theme.Primary,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }

            Text(
                text = place.name,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Medium,
                color = com.example.japuraroutef.ui.theme.OnSurfaceDark,
                maxLines = 2,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
            )

            if (place.rating != null) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = com.example.japuraroutef.ui.theme.Primary,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = String.format(java.util.Locale.US, "%.1f", place.rating),
                        style = MaterialTheme.typography.labelSmall,
                        color = com.example.japuraroutef.ui.theme.Primary
                    )
                }
            }
        }
    }
}

@Composable
private fun PlaceListPreview(
    places: List<PlaceResponseDto>,
    onPlaceClick: (PlaceResponseDto) -> Unit
) {
    if (places.isEmpty()) return

    var offsetY by remember { mutableStateOf(0f) }
    val maxDragDistance = 300f

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .offset { androidx.compose.ui.unit.IntOffset(0, offsetY.toInt()) }
            .pointerInput(Unit) {
                detectVerticalDragGestures(
                    onDragEnd = {
                        // Snap back to position
                        offsetY = 0f
                    },
                    onVerticalDrag = { _, dragAmount ->
                        val newOffset = offsetY + dragAmount
                        // Only allow dragging down
                        if (newOffset >= 0 && newOffset <= maxDragDistance) {
                            offsetY = newOffset
                        }
                    }
                )
            },
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        color = com.example.japuraroutef.ui.theme.SurfaceContainerDark.copy(alpha = 0.95f),
        shadowElevation = 16.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 400.dp)
                .padding(top = 20.dp, bottom = 20.dp)
        ) {
            // Drag Handle
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .width(48.dp)
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(com.example.japuraroutef.ui.theme.OutlineDark.copy(alpha = 0.4f))
                )
            }

            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 16.dp)
            ) {
                items(places) { place ->
                    PlacePreviewCard(place = place, onClick = { onPlaceClick(place) })
                }
            }
        }
    }
}

@Composable
private fun PlacePreviewCard(
    place: PlaceResponseDto,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        color = com.example.japuraroutef.ui.theme.SurfaceContainerHighDark,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (place.images.isNotEmpty()) {
                AsyncImage(
                    model = place.images.first(),
                    contentDescription = place.name,
                    modifier = Modifier
                        .size(60.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(com.example.japuraroutef.ui.theme.SurfaceContainerHighestDark),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Place,
                        contentDescription = null,
                        tint = com.example.japuraroutef.ui.theme.Primary
                    )
                }
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = place.name,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Medium,
                    color = com.example.japuraroutef.ui.theme.OnSurfaceDark
                )
                Text(
                    text = place.shortDescription ?: place.location ?: "",
                    style = MaterialTheme.typography.bodySmall,
                    color = com.example.japuraroutef.ui.theme.OnSurfaceVariantDark
                )
                if (place.rating != null) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = com.example.japuraroutef.ui.theme.Primary,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = String.format(java.util.Locale.US, "%.1f", place.rating),
                            style = MaterialTheme.typography.labelSmall,
                            color = com.example.japuraroutef.ui.theme.Primary
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PlaceDetailBottomSheet(
    place: PlaceResponseDto,
    onClose: () -> Unit,
    onGetDirections: () -> Unit,
    onViewMore: () -> Unit
) {
    var offsetY by remember { mutableStateOf(0f) }
    val maxDragDistance = 400f

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .offset { androidx.compose.ui.unit.IntOffset(0, offsetY.toInt()) }
            .pointerInput(Unit) {
                detectVerticalDragGestures(
                    onDragEnd = {
                        // If dragged down more than 200dp, close it
                        if (offsetY > 200f) {
                            onClose()
                        } else {
                            // Snap back to position
                            offsetY = 0f
                        }
                    },
                    onVerticalDrag = { _, dragAmount ->
                        val newOffset = offsetY + dragAmount
                        // Only allow dragging down
                        if (newOffset >= 0 && newOffset <= maxDragDistance) {
                            offsetY = newOffset
                        }
                    }
                )
            },
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        color = com.example.japuraroutef.ui.theme.SurfaceContainerDark.copy(alpha = 0.98f),
        shadowElevation = 24.dp
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 600.dp)
        ) {
            item {
                // Drag Handle
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .width(48.dp)
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(com.example.japuraroutef.ui.theme.OutlineDark.copy(alpha = 0.4f))
                    )
                }
            }

            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Header with title, bookmark, and close button
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = place.name,
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Normal,
                                color = com.example.japuraroutef.ui.theme.OnSurfaceDark
                            )
                            Text(
                                text = "${place.location ?: "Campus"} • Open until 10 PM",
                                style = MaterialTheme.typography.bodyMedium,
                                color = com.example.japuraroutef.ui.theme.OnSurfaceVariantDark
                            )
                            if (place.rating != null) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = String.format(java.util.Locale.US, "%.1f", place.rating),
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Medium,
                                        color = com.example.japuraroutef.ui.theme.Primary
                                    )
                                    repeat(5) { index ->
                                        Icon(
                                            imageVector = if (index < place.rating.toInt()) Icons.Default.Star else Icons.Default.StarBorder,
                                            contentDescription = null,
                                            tint = com.example.japuraroutef.ui.theme.Primary,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                    Text(
                                        text = "(${place.ratingCount} reviews)",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = com.example.japuraroutef.ui.theme.OnSurfaceVariantDark
                                    )
                                }
                            }
                        }

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            IconButton(
                                onClick = { /* TODO: Bookmark */ },
                                modifier = Modifier.size(48.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.BookmarkBorder,
                                    contentDescription = "Bookmark",
                                    tint = com.example.japuraroutef.ui.theme.Primary
                                )
                            }
                            IconButton(
                                onClick = onClose,
                                modifier = Modifier.size(48.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Close",
                                    tint = com.example.japuraroutef.ui.theme.OnSurfaceVariantDark
                                )
                            }
                        }
                    }

                    // Images Gallery
                    if (place.images.isNotEmpty()) {
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            contentPadding = PaddingValues(vertical = 8.dp)
                        ) {
                            items(place.images) { imageUrl ->
                                AsyncImage(
                                    model = imageUrl,
                                    contentDescription = place.name,
                                    modifier = Modifier
                                        .height(112.dp)
                                        .width(160.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .border(
                                            1.dp,
                                            com.example.japuraroutef.ui.theme.OutlineDark.copy(alpha = 0.1f),
                                            RoundedCornerShape(12.dp)
                                        ),
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }
                    }

                    // Action Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            onClick = onGetDirections,
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp),
                            shape = RoundedCornerShape(24.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = com.example.japuraroutef.ui.theme.Primary,
                                contentColor = com.example.japuraroutef.ui.theme.OnPrimaryContainerLight
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Directions,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "Get Directions",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        OutlinedButton(
                            onClick = onViewMore,
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp),
                            shape = RoundedCornerShape(24.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = com.example.japuraroutef.ui.theme.Primary
                            ),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                com.example.japuraroutef.ui.theme.OutlineDark
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "View More",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}

