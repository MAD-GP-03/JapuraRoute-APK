package com.example.japuraroutef.ui

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.japuraroutef.R
import com.mapbox.geojson.Point
import com.mapbox.geojson.LineString
import com.mapbox.maps.CameraBoundsOptions
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.mapbox.maps.extension.compose.style.MapStyle
import com.mapbox.maps.extension.compose.MapEffect
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.PolylineAnnotationOptions
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager
import com.mapbox.maps.plugin.annotation.generated.createPolylineAnnotationManager
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

data class FacultyPlace(
    val name: String,
    val point: Point,
    val description: String
)

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
fun MapScreen(onNavigateBack: () -> Unit) {
    val context = LocalContext.current
    var selectedDestination by remember { mutableStateOf<FacultyPlace?>(null) }
    var showPlacesList by remember { mutableStateOf(false) }
    var currentRoute by remember { mutableStateOf<Route?>(null) }
    val currentLocation by remember { mutableStateOf<Point?>(null) }
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
        locationPermissionRequest.launch(arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ))
    }

    // Faculty places
    val facultyPlaces = remember {
        listOf(
            FacultyPlace("Student Canteen", Point.fromLngLat(80.04289457106671, 6.82193306312118), "Faculty main gate"),
            FacultyPlace("Library", Point.fromLngLat(80.04018, 6.82436), "Central library building"),
            FacultyPlace("Lecture Hall A", Point.fromLngLat(80.04073, 6.82404), "Main lecture hall complex"),
            FacultyPlace("Computer Lab", Point.fromLngLat(80.04125, 6.82335), "Computing facilities"),
            FacultyPlace("Cafeteria", Point.fromLngLat(80.04155, 6.82242), "Student cafeteria"),
            FacultyPlace("Admin Office", Point.fromLngLat(80.04168, 6.82203), "Administration building"),
            FacultyPlace("Sports Ground", Point.fromLngLat(80.04199, 6.82108), "Sports facilities"),
            FacultyPlace("Parking Area", Point.fromLngLat(80.04360, 6.82177), "Vehicle parking")
        )
    }

    val cameraBoundsOptions = CameraBoundsOptions.Builder()
        .minZoom(14.0)
        .maxZoom(20.0)
        .build()

    Box(modifier = Modifier.fillMaxSize()) {
        // Mapbox Map
        MapboxMap(
            Modifier.fillMaxSize(),
            mapViewportState = rememberMapViewportState {
                setCameraOptions {
                    zoom(17.0)
                    center(Point.fromLngLat(80.04168, 6.82314))
                    pitch(60.0)
                    bearing(0.0)
                }
            },
            style = {
                MapStyle(style = "mapbox://styles/nuwankonara/cmifk82fz001y01qw6bsdhv86")
            }
        ) {
            MapEffect(Unit) { mapView ->
                mapView.mapboxMap.setBounds(cameraBoundsOptions)

                // Add markers for all places
                val annotationApi = mapView.annotations
                val pointAnnotationManager = annotationApi.createPointAnnotationManager()

                facultyPlaces.forEach { place ->
                    val pointAnnotationOptions = PointAnnotationOptions()
                        .withPoint(place.point)
                        .withIconImage("marker-icon-red")
                    pointAnnotationManager.create(pointAnnotationOptions)
                }
            }

            // Update route on map when available
            MapEffect(currentRoute) { mapView ->
                currentRoute?.let { route ->
                    val annotationApi = mapView.annotations
                    val polylineAnnotationManager = annotationApi.createPolylineAnnotationManager()

                    try {
                        val points = LineString.fromPolyline(route.geometry, 6).coordinates()

                        val polylineAnnotationOptions = PolylineAnnotationOptions()
                            .withPoints(points)
                            .withLineColor("#0080FF")
                            .withLineWidth(5.0)
                        polylineAnnotationManager.create(polylineAnnotationOptions)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }

        // Top App Bar with back button - Material 3 theme
        val colorScheme = MaterialTheme.colorScheme
        TopAppBar(
            title = {
                Text(
                    "Campus Map",
                    style = MaterialTheme.typography.titleLarge
                )
            },
            navigationIcon = {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = colorScheme.surface.copy(alpha = 0.95f),
                titleContentColor = colorScheme.onSurface,
                navigationIconContentColor = colorScheme.onSurface
            )
        )

        // Bottom sheet with places
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
        ) {
            if (showPlacesList) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = colorScheme.surface,
                    shadowElevation = 8.dp,
                    tonalElevation = 3.dp,
                    shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
                ) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 400.dp)
                            .padding(vertical = 16.dp)
                    ) {
                        items(facultyPlaces) { place ->
                            PlaceItem(
                                place = place,
                                onSelect = {
                                    selectedDestination = place
                                    showPlacesList = false

                                    // Request route
                                    coroutineScope.launch {
                                        try {
                                            val origin = currentLocation ?: Point.fromLngLat(80.03948, 6.82478)
                                            val coords = "${origin.longitude()},${origin.latitude()};${place.point.longitude()},${place.point.latitude()}"
                                            val response = directionsService.getDirections(coords, accessToken = accessToken)
                                            if (response.routes.isNotEmpty()) {
                                                currentRoute = response.routes[0]
                                            }
                                        } catch (e: Exception) {
                                            e.printStackTrace()
                                        }
                                    }
                                }
                            )
                        }
                    }
                }
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                elevation = CardDefaults.cardElevation(8.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    if (selectedDestination != null) {
                        Text(
                            text = "Navigating to: ${selectedDestination!!.name}",
                            style = MaterialTheme.typography.titleMedium,
                            color = colorScheme.onSurface
                        )
                        Text(
                            text = selectedDestination!!.description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = {
                                selectedDestination = null
                                currentRoute = null
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = colorScheme.error
                            )
                        ) {
                            Text("Clear Route")
                        }
                    } else {
                        Button(
                            onClick = { showPlacesList = !showPlacesList },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = colorScheme.primary
                            )
                        ) {
                            Text(
                                if (showPlacesList) "Hide Places" else "Select Destination"
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PlaceItem(place: FacultyPlace, onSelect: () -> Unit) {
    val colorScheme = MaterialTheme.colorScheme

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onSelect)
            .padding(horizontal = 24.dp, vertical = 12.dp)
    ) {
        Text(
            text = place.name,
            style = MaterialTheme.typography.titleMedium,
            color = colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = place.description,
            style = MaterialTheme.typography.bodySmall,
            color = colorScheme.onSurfaceVariant
        )
        HorizontalDivider(
            modifier = Modifier.padding(top = 12.dp),
            color = colorScheme.outlineVariant
        )
    }
}

