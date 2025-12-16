package com.cussou.autotiq.domain.repository

import com.cussou.autotiq.domain.model.ProximityState
import kotlinx.coroutines.flow.Flow

interface ProximityRepository {
    fun getProximityState(pointId: Long): Flow<ProximityState?>
    suspend fun updateProximityState(state: ProximityState)
    suspend fun clearProximityState(pointId: Long)
}
