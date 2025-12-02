package com.fairlaunch.domain.repository

import com.fairlaunch.domain.model.MapPoint
import com.fairlaunch.domain.util.Result
import kotlinx.coroutines.flow.Flow

interface MapPointRepository {
    fun getAllPoints(): Flow<List<MapPoint>>
    suspend fun getPointById(id: Long): Result<MapPoint>
    suspend fun insertPoint(point: MapPoint): Result<Long>
    suspend fun deletePoint(id: Long): Result<Unit>
    suspend fun updatePoint(point: MapPoint): Result<Unit>
}
