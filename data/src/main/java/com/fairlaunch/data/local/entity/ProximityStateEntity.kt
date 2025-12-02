package com.fairlaunch.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "proximity_states")
data class ProximityStateEntity(
    @PrimaryKey
    val pointId: Long,
    val isInside: Boolean,
    val lastChecked: Long
)
