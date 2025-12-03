package com.fairlaunch.ui.map

import android.Manifest
import android.content.pm.PackageManager
import android.os.Handler
import android.os.Looper
import android.view.MotionEvent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.LocationOff
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fairlaunch.domain.model.MapLayerType
import com.fairlaunch.domain.model.MapPoint
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polygon
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    onNavigateToSettings: () -> Unit,
    viewModel: MapViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showLayerMenu by remember { mutableStateOf(false) }
    
    var hasLocationPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        hasLocationPermission = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("FairLaunch") },
                actions = {
                    when (val state = uiState) {
                        is MapUiState.Success -> {
                            // Location tracking toggle
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(end = 8.dp)
                            ) {
                                Icon(
                                    imageVector = if (state.settings.isLocationTrackingEnabled)
                                        Icons.Default.LocationOn
                                    else
                                        Icons.Default.LocationOff,
                                    contentDescription = "Location tracking"
                                )
                                Switch(
                                    checked = state.settings.isLocationTrackingEnabled,
                                    onCheckedChange = { viewModel.toggleLocationTracking(it) }
                                )
                            }
                        }
                        else -> {}
                    }
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                }
            )
        }
    ) { padding ->
        when (val state = uiState) {
            is MapUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is MapUiState.Success -> {
                Box(modifier = Modifier.fillMaxSize()) {
                    MapContent(
                        points = state.points,
                        proximityDistanceMeters = state.settings.proximityDistanceMeters,
                        mapLayerType = state.settings.mapLayerType,
                        hasLocationPermission = hasLocationPermission,
                        onRequestPermission = {
                            permissionLauncher.launch(
                                arrayOf(
                                    Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.ACCESS_COARSE_LOCATION
                                )
                            )
                        },
                        onAddPoint = { lat, lon -> viewModel.addPoint(lat, lon) },
                        onDeletePoint = { id -> viewModel.deletePoint(id) },
                        modifier = Modifier.padding(padding)
                    )
                    
                    // Floating layer selection button
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(padding)
                            .padding(16.dp)
                    ) {
                        SmallFloatingActionButton(
                            onClick = { showLayerMenu = true },
                            containerColor = MaterialTheme.colorScheme.surface,
                            contentColor = MaterialTheme.colorScheme.onSurface
                        ) {
                            Icon(Icons.Default.Layers, contentDescription = "Map layers")
                        }
                        DropdownMenu(
                            expanded = showLayerMenu,
                            onDismissRequest = { showLayerMenu = false }
                        ) {
                            MapLayerType.entries.forEach { layerType ->
                                DropdownMenuItem(
                                    text = { Text(layerType.displayName()) },
                                    onClick = {
                                        viewModel.updateMapLayer(layerType)
                                        showLayerMenu = false
                                    },
                                    leadingIcon = if (layerType == state.settings.mapLayerType) {
                                        { Text("✓") }
                                    } else null
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MapContent(
    points: List<MapPoint>,
    proximityDistanceMeters: Int,
    mapLayerType: MapLayerType,
    hasLocationPermission: Boolean,
    onRequestPermission: () -> Unit,
    onAddPoint: (Double, Double) -> Unit,
    onDeletePoint: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var mapView: MapView? by remember { mutableStateOf(null) }

    DisposableEffect(Unit) {
        Configuration.getInstance().userAgentValue = context.packageName
        onDispose {
            mapView?.onDetach()
        }
    }

    if (!hasLocationPermission) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    "Location permission required",
                    style = MaterialTheme.typography.titleMedium
                )
                androidx.compose.material3.Button(
                    onClick = onRequestPermission,
                    modifier = Modifier.padding(top = 16.dp)
                ) {
                    Text("Grant Permission")
                }
            }
        }
    } else {
        AndroidView(
            modifier = modifier.fillMaxSize(),
            factory = { ctx ->
                MapView(ctx).apply {
                    mapView = this
                    setTileSource(mapLayerType.toTileSource())
                    setMultiTouchControls(true)
                    
                    // Set default position (e.g., Zurich, Switzerland)
                    controller.setZoom(13.0)
                    controller.setCenter(GeoPoint(47.3769, 8.5417))

                    // Enable location overlay
                    val locationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(ctx), this)
                    locationOverlay.enableMyLocation()
                    locationOverlay.enableFollowLocation()
                    overlays.add(locationOverlay)
                    
                    // Center on user location when available
                    locationOverlay.runOnFirstFix {
                        post {
                            controller.animateTo(locationOverlay.myLocation)
                            controller.setZoom(15.0)
                        }
                    }

                    // Variables to track long press on map and markers
                    var touchStartTime = 0L
                    var touchStartX = 0f
                    var touchStartY = 0f
                    var touchedMarkerId: Long? = null
                    val longPressThreshold = 500L // milliseconds
                    val movementThreshold = 20f // pixels

                    setOnTouchListener { _, event ->
                        when (event.action) {
                            MotionEvent.ACTION_DOWN -> {
                                touchStartTime = System.currentTimeMillis()
                                touchStartX = event.x
                                touchStartY = event.y
                                
                                // Check if touch is on a marker
                                touchedMarkerId = null
                                val projection = this.projection
                                
                                // Find closest marker within touch range
                                // Note: We need to iterate through actual marker overlays
                                overlays.filterIsInstance<Marker>().forEach { marker ->
                                    val markerScreenPoint = projection.toPixels(marker.position, null)
                                    val dx = event.x - markerScreenPoint.x
                                    val dy = event.y - markerScreenPoint.y
                                    val distance = Math.sqrt((dx * dx + dy * dy).toDouble())
                                    
                                    if (distance < 100) { // 100 pixels touch radius (enlarged from 50)
                                        // Extract the point ID from marker title
                                        val title = marker.title
                                        if (title != null && title.contains("#")) {
                                            val idStr = title.substringAfter("#")
                                            touchedMarkerId = idStr.toLongOrNull()
                                        }
                                    }
                                }
                                false
                            }
                            MotionEvent.ACTION_UP -> {
                                val duration = System.currentTimeMillis() - touchStartTime
                                val dx = Math.abs(event.x - touchStartX)
                                val dy = Math.abs(event.y - touchStartY)
                                
                                if (dx < movementThreshold && dy < movementThreshold) {
                                    if (touchedMarkerId != null) {
                                        // Touch on marker
                                        if (duration >= longPressThreshold) {
                                            // Long press on marker - delete it
                                            onDeletePoint(touchedMarkerId!!)
                                        } else {
                                            // Short click on marker - reserved for future edit
                                            // TODO: Add edit dialog here
                                        }
                                        true // Consume the event to prevent map interaction
                                    } else if (duration >= longPressThreshold) {
                                        // Long press on map - add point
                                        val projection = this.projection
                                        val geoPoint = projection.fromPixels(
                                            event.x.toInt(),
                                            event.y.toInt()
                                        ) as GeoPoint
                                        onAddPoint(geoPoint.latitude, geoPoint.longitude)
                                        true
                                    } else {
                                        false
                                    }
                                } else {
                                    false
                                }
                            }
                            else -> false
                        }
                    }
                }
            },
            update = { map ->
                // Update tile source when layer changes
                if (map.tileProvider.tileSource != mapLayerType.toTileSource()) {
                    map.setTileSource(mapLayerType.toTileSource())
                }
                
                // Remove old markers and circles
                map.overlays.removeAll { it is Marker || it is Polygon }
                
                // Add circles and markers for each point
                points.forEach { mapPoint ->
                    // Create circle overlay for proximity distance
                    val circlePoints = Polygon.pointsAsCircle(
                        GeoPoint(mapPoint.latitude, mapPoint.longitude),
                        proximityDistanceMeters.toDouble()
                    )
                    val circle = Polygon(map)
                    circle.points = circlePoints
                    circle.fillPaint.color = 0x20FF0000.toInt() // Semi-transparent red
                    circle.outlinePaint.color = 0x80FF0000.toInt() // Semi-transparent red border
                    circle.outlinePaint.strokeWidth = 2f
                    map.overlays.add(circle)
                    
                    // Create marker
                    val marker = Marker(map).apply {
                        position = GeoPoint(mapPoint.latitude, mapPoint.longitude)
                        title = mapPoint.name ?: "Point #${mapPoint.id}"
                        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                    }
                    
                    map.overlays.add(marker)
                }
                
                map.invalidate()
            }
        )
    }
}

// Helper extension functions
private fun MapLayerType.toTileSource() = when (this) {
    MapLayerType.STREET -> TileSourceFactory.MAPNIK
    MapLayerType.TOPO -> TileSourceFactory.OpenTopo
}

private fun MapLayerType.displayName() = when (this) {
    MapLayerType.STREET -> "Street Map"
    MapLayerType.TOPO -> "Topographic"
}
