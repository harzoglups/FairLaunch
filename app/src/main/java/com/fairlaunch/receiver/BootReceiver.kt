package com.fairlaunch.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.fairlaunch.worker.LocationCheckWorker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.io.File

class BootReceiver : BroadcastReceiver() {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            Log.d("BootReceiver", "Device booted, checking if tracking was enabled")
            
            // Use goAsync to allow async operations in BroadcastReceiver
            val pendingResult = goAsync()
            
            scope.launch {
                try {
                    // Check if the datastore file exists to avoid creating a new instance
                    val datastoreFile = File(context.filesDir, "datastore/settings.preferences_pb")
                    
                    if (datastoreFile.exists()) {
                        // DataStore exists, schedule worker which will read settings itself
                        Log.d("BootReceiver", "Settings file exists, scheduling worker to check settings")
                        
                        // Add delay to let GPS initialize after boot (cold start issue)
                        val workRequest = OneTimeWorkRequestBuilder<LocationCheckWorker>()
                            .setInitialDelay(30, java.util.concurrent.TimeUnit.SECONDS)
                            .build()
                        
                        WorkManager.getInstance(context).enqueue(workRequest)
                        
                        Log.d("BootReceiver", "Worker scheduled successfully with 30s delay")
                    } else {
                        Log.d("BootReceiver", "No settings file found, tracking was never enabled")
                    }
                } catch (e: Exception) {
                    Log.e("BootReceiver", "Error scheduling worker after boot", e)
                } finally {
                    pendingResult.finish()
                }
            }
        }
    }
}
