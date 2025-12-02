package com.fairlaunch.domain.usecase

import com.fairlaunch.domain.model.MapPoint
import com.fairlaunch.domain.model.ProximityState
import com.fairlaunch.domain.repository.MapPointRepository
import com.fairlaunch.domain.repository.ProximityRepository
import kotlinx.coroutines.flow.first
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class CheckProximityUseCase(
    private val mapPointRepository: MapPointRepository,
    private val proximityRepository: ProximityRepository
) {
    suspend operator fun invoke(
        currentLatitude: Double,
        currentLongitude: Double,
        proximityDistanceMeters: Int
    ): List<MapPoint> {
        val points = mapPointRepository.getAllPoints().first()
        val pointsToTrigger = mutableListOf<MapPoint>()

        points.forEach { point ->
            val distance = calculateDistance(
                currentLatitude,
                currentLongitude,
                point.latitude,
                point.longitude
            )

            val isInside = distance <= proximityDistanceMeters
            val previousState = proximityRepository.getProximityState(point.id).first()

            // Trigger if:
            // 1. We're inside the zone AND
            // 2. Either we have no previous state OR we were outside before
            if (isInside && (previousState == null || !previousState.isInside)) {
                pointsToTrigger.add(point)
            }

            // Update the state
            proximityRepository.updateProximityState(
                ProximityState(
                    pointId = point.id,
                    isInside = isInside
                )
            )
        }

        return pointsToTrigger
    }

    private fun calculateDistance(
        lat1: Double,
        lon1: Double,
        lat2: Double,
        lon2: Double
    ): Float {
        // Haversine formula to calculate distance between two coordinates
        val earthRadius = 6371000.0 // meters
        
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        
        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2) * sin(dLon / 2)
        
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        
        return (earthRadius * c).toFloat()
    }
}

