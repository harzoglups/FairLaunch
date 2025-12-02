package com.fairlaunch.worker

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.fairlaunch.domain.usecase.CheckProximityUseCase
import com.fairlaunch.domain.usecase.GetSettingsUseCase
import com.google.android.gms.location.LocationServices
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.tasks.await

@HiltWorker
class LocationCheckWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted workerParams: WorkerParameters,
    private val checkProximityUseCase: CheckProximityUseCase,
    private val getSettingsUseCase: GetSettingsUseCase
) : CoroutineWorker(context, workerParams) {

    companion object {
        private const val TAG = "LocationCheckWorker"
        const val WORK_NAME = "location_check_work"
        private const val FAIRTIQ_PACKAGE = "com.fairtiq.android"
    }

    override suspend fun doWork(): androidx.work.ListenableWorker.Result {
        Log.d(TAG, "Starting location check...")

        // Check location permission
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.e(TAG, "Location permission not granted")
            return androidx.work.ListenableWorker.Result.failure()
        }

        try {
            // Get settings first
            val settings = getSettingsUseCase().first()
            Log.d(TAG, "Settings: interval=${settings.checkIntervalSeconds}s, distance=${settings.proximityDistanceMeters}m, enabled=${settings.isLocationTrackingEnabled}")
            
            // If tracking is disabled, don't reschedule
            if (!settings.isLocationTrackingEnabled) {
                Log.d(TAG, "Tracking disabled, stopping worker")
                return androidx.work.ListenableWorker.Result.success()
            }
            
            // Get current location
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
            val location = fusedLocationClient.lastLocation.await()

            if (location == null) {
                Log.w(TAG, "Location is null")
                rescheduleIfNeeded(settings.checkIntervalSeconds)
                return androidx.work.ListenableWorker.Result.success()
            }

            Log.d(TAG, "Current location: ${location.latitude}, ${location.longitude}")
            
            // Check proximity - returns list of points we've entered
            val pointsToTrigger = checkProximityUseCase(
                currentLatitude = location.latitude,
                currentLongitude = location.longitude,
                proximityDistanceMeters = settings.proximityDistanceMeters
            )

            if (pointsToTrigger.isNotEmpty()) {
                Log.d(TAG, "Entered proximity zone for ${pointsToTrigger.size} point(s)")
                launchFairtiqAndVibrate()
            } else {
                Log.d(TAG, "No proximity zones entered")
            }

            // Reschedule for next check if interval < 15 minutes
            rescheduleIfNeeded(settings.checkIntervalSeconds)
            
            return androidx.work.ListenableWorker.Result.success()
        } catch (e: Exception) {
            Log.e(TAG, "Error during location check", e)
            return androidx.work.ListenableWorker.Result.failure()
        }
    }
    
    private fun rescheduleIfNeeded(intervalSeconds: Int) {
        // Only reschedule if interval is less than 15 minutes (WorkManager minimum for PeriodicWork)
        if (intervalSeconds < 900) {
            Log.d(TAG, "Rescheduling next check in ${intervalSeconds}s")
            val scheduler = LocationWorkScheduler(context)
            scheduler.scheduleLocationChecks(intervalSeconds)
        }
    }

    private fun launchFairtiqAndVibrate() {
        try {
            // Launch Fairtiq app
            val intent = context.packageManager.getLaunchIntentForPackage(FAIRTIQ_PACKAGE)
            if (intent != null) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
                Log.d(TAG, "Launched Fairtiq app")
            } else {
                Log.w(TAG, "Fairtiq app not installed")
            }

            // Vibrate
            val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(
                    VibrationEffect.createWaveform(
                        longArrayOf(0, 200, 100, 200, 100, 200),
                        -1
                    )
                )
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(longArrayOf(0, 200, 100, 200, 100, 200), -1)
            }
            Log.d(TAG, "Vibration triggered")
        } catch (e: Exception) {
            Log.e(TAG, "Error launching Fairtiq or vibrating", e)
        }
    }
}
