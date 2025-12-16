package com.cussou.autotiq.data.mapper

import com.cussou.autotiq.data.local.entity.ProximityStateEntity
import com.cussou.autotiq.domain.model.ProximityState

fun ProximityStateEntity.toDomain(): ProximityState {
    return ProximityState(
        pointId = pointId,
        isInside = isInside,
        lastChecked = lastChecked
    )
}

fun ProximityState.toEntity(): ProximityStateEntity {
    return ProximityStateEntity(
        pointId = pointId,
        isInside = isInside,
        lastChecked = lastChecked
    )
}
