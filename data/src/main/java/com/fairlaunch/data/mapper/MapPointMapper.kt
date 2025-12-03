package com.fairlaunch.data.mapper

import com.fairlaunch.data.local.entity.MapPointEntity
import com.fairlaunch.domain.model.MapPoint

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
