package com.fairlaunch.ui.settings

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fairlaunch.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val settings by viewModel.settings.collectAsStateWithLifecycle()
    val permissionStatus by viewModel.permissionStatus.collectAsStateWithLifecycle()
    val context = LocalContext.current
    
    var showBackgroundLocationDialog by remember { mutableStateOf(false) }
    
    // Launcher for background location permission (Android 10+)
    val backgroundLocationLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        viewModel.onBackgroundLocationPermissionResult(isGranted)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "Location Tracking",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            // Permission status card for Android 10+
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                when (permissionStatus) {
                    PermissionStatus.NOT_GRANTED -> {
                        SettingCard {
                            Column {
                                Text(
                                    text = stringResource(R.string.permission_background_location_required),
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )
                                Text(
                                    text = stringResource(R.string.permission_location_rationale),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(bottom = 12.dp)
                                )
                                Button(
                                    onClick = { 
                                        showBackgroundLocationDialog = true
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(stringResource(R.string.permission_background_location_button))
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                    PermissionStatus.FOREGROUND_ONLY -> {
                        SettingCard {
                            Column {
                                Text(
                                    text = "Permission limitée à l'utilisation de l'app",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.tertiary,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )
                                Text(
                                    text = stringResource(R.string.permission_background_location_rationale),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(bottom = 12.dp)
                                )
                                Button(
                                    onClick = { 
                                        showBackgroundLocationDialog = true
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("Autoriser \"Toujours\"")
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                    PermissionStatus.GRANTED -> {
                        // Permission granted, no need to show warning
                    }
                }
            }

            SettingCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Enable Background Tracking",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = "Monitor your location in the background",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = settings.isLocationTrackingEnabled,
                        onCheckedChange = { enabled ->
                            if (enabled && Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                // Check if we have background permission before enabling
                                if (permissionStatus != PermissionStatus.GRANTED) {
                                    showBackgroundLocationDialog = true
                                } else {
                                    viewModel.updateLocationTracking(true)
                                }
                            } else {
                                viewModel.updateLocationTracking(enabled)
                            }
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Detection Parameters",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            SettingCard {
                Column {
                    var intervalText by remember(settings.checkIntervalSeconds) {
                        mutableStateOf<String>(settings.checkIntervalSeconds.toString())
                    }

                    Text(
                        text = "Check Interval (seconds)",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Text(
                        text = "How often to check your location",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    OutlinedTextField(
                        value = intervalText,
                        onValueChange = { newValue: String ->
                            intervalText = newValue
                            newValue.toIntOrNull()?.let { seconds: Int ->
                                if (seconds > 0) {
                                    viewModel.updateCheckInterval(seconds)
                                }
                            }
                        },
                        label = { Text("Seconds") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            SettingCard {
                Column {
                    var distanceText by remember(settings.proximityDistanceMeters) {
                        mutableStateOf<String>(settings.proximityDistanceMeters.toString())
                    }

                    Text(
                        text = "Proximity Distance (meters)",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Text(
                        text = "Distance to trigger Fairtiq launch",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    OutlinedTextField(
                        value = distanceText,
                        onValueChange = { newValue: String ->
                            distanceText = newValue
                            newValue.toIntOrNull()?.let { meters: Int ->
                                if (meters > 0) {
                                    viewModel.updateProximityDistance(meters)
                                }
                            }
                        },
                        label = { Text("Meters") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Info",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            SettingCard {
                Column {
                    Text(
                        text = "How it works",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Text(
                        text = "• Long press on the map to add a point (edit dialog opens automatically)\n" +
                                "• Short tap on a marker to view its details\n" +
                                "• Long press on a marker to delete it\n" +
                                "• Configure point properties: name and active time window (hour:minute)\n" +
                                "• Enable background tracking to monitor your location\n" +
                                "• When you enter a point's zone during its active time window:\n" +
                                "  - Fairtiq launches automatically\n" +
                                "  - The phone vibrates (3-burst pattern)\n" +
                                "  - The screen wakes up if locked\n" +
                                "• You must exit the zone and re-enter to trigger again (anti-spam)",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
    
    // Background location permission explanation dialog
    if (showBackgroundLocationDialog) {
        AlertDialog(
            onDismissRequest = { showBackgroundLocationDialog = false },
            title = { 
                Text(stringResource(R.string.permission_background_location_required)) 
            },
            text = { 
                Text(stringResource(R.string.permission_background_location_rationale))
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showBackgroundLocationDialog = false
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            // Try to request background location permission
                            backgroundLocationLauncher.launch(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                        }
                    }
                ) {
                    Text("Autoriser")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { 
                        showBackgroundLocationDialog = false
                    }
                ) {
                    Text("Plus tard")
                }
            }
        )
    }
}

@Composable
private fun SettingCard(
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            content()
        }
    }
}
