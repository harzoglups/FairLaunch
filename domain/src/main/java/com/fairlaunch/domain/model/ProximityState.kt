package com.fairlaunch.domain.model

data class ProximityState(
    val pointId: Long,
    val isInside: Boolean,
    val lastChecked: Long = System.currentTimeMillis()
)
