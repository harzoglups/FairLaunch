package com.fairlaunch.domain.usecase

import com.fairlaunch.domain.repository.SettingsRepository

class UpdateSettingsUseCase(
    private val repository: SettingsRepository
) {
    suspend fun updateCheckInterval(seconds: Int) {
        repository.updateCheckInterval(seconds)
    }

    suspend fun updateProximityDistance(meters: Int) {
        repository.updateProximityDistance(meters)
    }

    suspend fun updateLocationTrackingEnabled(enabled: Boolean) {
        repository.updateLocationTrackingEnabled(enabled)
    }
}
