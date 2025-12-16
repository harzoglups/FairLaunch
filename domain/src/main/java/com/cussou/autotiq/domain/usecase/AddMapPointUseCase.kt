package com.cussou.autotiq.domain.usecase

import com.cussou.autotiq.domain.model.MapPoint
import com.cussou.autotiq.domain.repository.MapPointRepository
import com.cussou.autotiq.domain.util.Result

class AddMapPointUseCase(
    private val repository: MapPointRepository
) {
    suspend operator fun invoke(latitude: Double, longitude: Double, name: String? = null): Result<Long> {
        val point = MapPoint(
            latitude = latitude,
            longitude = longitude,
            name = name ?: ""
        )
        return repository.insertPoint(point)
    }
}
