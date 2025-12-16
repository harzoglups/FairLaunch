package com.cussou.autotiq.domain.usecase

import com.cussou.autotiq.domain.model.MapPoint
import com.cussou.autotiq.domain.repository.MapPointRepository
import kotlinx.coroutines.flow.Flow

class GetMapPointsUseCase(
    private val repository: MapPointRepository
) {
    operator fun invoke(): Flow<List<MapPoint>> {
        return repository.getAllPoints()
    }
}
