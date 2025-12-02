package com.fairlaunch.domain.usecase

import com.fairlaunch.domain.repository.MapPointRepository
import com.fairlaunch.domain.repository.ProximityRepository
import com.fairlaunch.domain.util.Result

class DeleteMapPointUseCase(
    private val mapPointRepository: MapPointRepository,
    private val proximityRepository: ProximityRepository
) {
    suspend operator fun invoke(id: Long): Result<Unit> {
        proximityRepository.clearProximityState(id)
        return mapPointRepository.deletePoint(id)
    }
}
