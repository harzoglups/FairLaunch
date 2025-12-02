package com.fairlaunch.domain.model

data class AppSettings(
    val checkIntervalSeconds: Int = 300, // 5 minutes = 300 seconds
    val proximityDistanceMeters: Int = 200,
    val isLocationTrackingEnabled: Boolean = false
)
