package com.fairlaunch.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fairlaunch.domain.model.AppSettings
import com.fairlaunch.domain.usecase.GetSettingsUseCase
import com.fairlaunch.domain.usecase.UpdateSettingsUseCase
import com.fairlaunch.worker.LocationWorkScheduler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    getSettingsUseCase: GetSettingsUseCase,
    private val updateSettingsUseCase: UpdateSettingsUseCase,
    private val locationWorkScheduler: LocationWorkScheduler
) : ViewModel() {

    val settings: StateFlow<AppSettings> = getSettingsUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = AppSettings()
        )

    fun updateCheckInterval(seconds: Int) {
        viewModelScope.launch {
            updateSettingsUseCase.updateCheckInterval(seconds)
            // If tracking is enabled, reschedule with new interval
            if (settings.value.isLocationTrackingEnabled) {
                locationWorkScheduler.scheduleLocationChecks(seconds)
            }
        }
    }

    fun updateProximityDistance(meters: Int) {
        viewModelScope.launch {
            updateSettingsUseCase.updateProximityDistance(meters)
        }
    }

    fun updateLocationTracking(enabled: Boolean) {
        viewModelScope.launch {
            updateSettingsUseCase.updateLocationTrackingEnabled(enabled)
            
            if (enabled) {
                locationWorkScheduler.scheduleLocationChecks(settings.value.checkIntervalSeconds)
            } else {
                locationWorkScheduler.cancelLocationChecks()
            }
        }
    }
}
