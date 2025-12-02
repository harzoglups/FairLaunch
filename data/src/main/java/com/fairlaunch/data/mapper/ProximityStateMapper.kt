package com.fairlaunch.data.mapper

import com.fairlaunch.data.local.entity.ProximityStateEntity
import com.fairlaunch.domain.model.ProximityState

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
