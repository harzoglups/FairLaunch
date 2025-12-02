package com.fairlaunch.domain.repository

import com.fairlaunch.domain.model.ProximityState
import kotlinx.coroutines.flow.Flow

interface ProximityRepository {
    fun getProximityState(pointId: Long): Flow<ProximityState?>
    suspend fun updateProximityState(state: ProximityState)
    suspend fun clearProximityState(pointId: Long)
}
