package com.fairlaunch.domain.usecase

import com.fairlaunch.domain.model.MapPoint
import com.fairlaunch.domain.repository.MapPointRepository
import kotlinx.coroutines.flow.Flow

class GetMapPointsUseCase(
    private val repository: MapPointRepository
) {
    operator fun invoke(): Flow<List<MapPoint>> {
        return repository.getAllPoints()
    }
}
