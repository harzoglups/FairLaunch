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
    data class ProximityCheckDetails(
        val pointsToTrigger: List<MapPoint>,
        val allPointsDetails: List<PointCheckDetail>
    )
    
    data class PointCheckDetail(
        val point: MapPoint,
        val distance: Float,
        val isInside: Boolean,
        val wasInside: Boolean,
        val triggered: Boolean
    )

    suspend operator fun invoke(
        currentLatitude: Double,
        currentLongitude: Double,
        proximityDistanceMeters: Int
    ): List<MapPoint> {
        val result = invokeWithDetails(currentLatitude, currentLongitude, proximityDistanceMeters)
        return result.pointsToTrigger
    }
    
    suspend fun invokeWithDetails(
        currentLatitude: Double,
        currentLongitude: Double,
        proximityDistanceMeters: Int
    ): ProximityCheckDetails {
        val points = mapPointRepository.getAllPoints().first()
        val pointsToTrigger = mutableListOf<MapPoint>()
        val allPointsDetails = mutableListOf<PointCheckDetail>()

        points.forEach { point ->
            val distance = calculateDistance(
                currentLatitude,
                currentLongitude,
                point.latitude,
                point.longitude
            )

            val isInside = distance <= proximityDistanceMeters
            val previousState = proximityRepository.getProximityState(point.id).first()
            val wasInside = previousState?.isInside ?: false

            // Trigger if:
            // 1. We're inside the zone AND
            // 2. Either we have no previous state OR we were outside before
            val triggered = isInside && (previousState == null || !previousState.isInside)
            if (triggered) {
                pointsToTrigger.add(point)
            }
            
            allPointsDetails.add(
                PointCheckDetail(
                    point = point,
                    distance = distance,
                    isInside = isInside,
                    wasInside = wasInside,
                    triggered = triggered
                )
            )

            // Update the state
            proximityRepository.updateProximityState(
                ProximityState(
                    pointId = point.id,
                    isInside = isInside
                )
            )
        }

        return ProximityCheckDetails(pointsToTrigger, allPointsDetails)
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

