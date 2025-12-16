package com.cussou.autotiq.domain.usecase

import com.cussou.autotiq.domain.model.MapPoint
import com.cussou.autotiq.domain.repository.MapPointRepository
import com.cussou.autotiq.domain.util.Result

class UpdateMapPointUseCase(
    private val repository: MapPointRepository
) {
    suspend operator fun invoke(point: MapPoint): Result<Unit> {
        return repository.updatePoint(point)
    }
}
