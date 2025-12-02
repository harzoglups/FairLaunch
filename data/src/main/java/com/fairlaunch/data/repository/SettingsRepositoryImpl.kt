package com.fairlaunch.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.fairlaunch.domain.model.AppSettings
import com.fairlaunch.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingsRepositoryImpl @Inject constructor(
    private val context: Context
) : SettingsRepository {

    private object PreferencesKeys {
        val CHECK_INTERVAL = intPreferencesKey("check_interval_seconds")
        val PROXIMITY_DISTANCE = intPreferencesKey("proximity_distance_meters")
        val LOCATION_TRACKING_ENABLED = booleanPreferencesKey("location_tracking_enabled")
    }

    override fun getSettings(): Flow<AppSettings> {
        return context.dataStore.data.map { preferences ->
            AppSettings(
                checkIntervalSeconds = preferences[PreferencesKeys.CHECK_INTERVAL] ?: 300,
                proximityDistanceMeters = preferences[PreferencesKeys.PROXIMITY_DISTANCE] ?: 200,
                isLocationTrackingEnabled = preferences[PreferencesKeys.LOCATION_TRACKING_ENABLED] ?: false
            )
        }
    }

    override suspend fun updateCheckInterval(seconds: Int) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.CHECK_INTERVAL] = seconds
        }
    }

    override suspend fun updateProximityDistance(meters: Int) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.PROXIMITY_DISTANCE] = meters
        }
    }

    override suspend fun updateLocationTrackingEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.LOCATION_TRACKING_ENABLED] = enabled
        }
    }
}
