package com.cussou.autotiq.domain.usecase

import com.cussou.autotiq.domain.repository.MapPointRepository
import com.cussou.autotiq.domain.repository.ProximityRepository
import com.cussou.autotiq.domain.util.Result

class DeleteMapPointUseCase(
    private val mapPointRepository: MapPointRepository,
    private val proximityRepository: ProximityRepository
) {
    suspend operator fun invoke(id: Long): Result<Unit> {
        proximityRepository.clearProximityState(id)
        return mapPointRepository.deletePoint(id)
    }
}
