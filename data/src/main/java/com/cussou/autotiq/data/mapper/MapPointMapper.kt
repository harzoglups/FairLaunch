package com.cussou.autotiq.data.mapper

import com.cussou.autotiq.data.local.entity.MapPointEntity
import com.cussou.autotiq.domain.model.MapPoint

fun MapPointEntity.toDomain(): MapPoint {
    return MapPoint(
        id = id,
        latitude = latitude,
        longitude = longitude,
        name = name,
        startHour = startHour,
        startMinute = startMinute,
        endHour = endHour,
        endMinute = endMinute,
        createdAt = createdAt
    )
}

fun MapPoint.toEntity(): MapPointEntity {
    return MapPointEntity(
        id = id,
        latitude = latitude,
        longitude = longitude,
        name = name,
        startHour = startHour,
        startMinute = startMinute,
        endHour = endHour,
        endMinute = endMinute,
        createdAt = createdAt
    )
}
